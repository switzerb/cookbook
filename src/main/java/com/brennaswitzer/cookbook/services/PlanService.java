package com.brennaswitzer.cookbook.services;

import com.brennaswitzer.cookbook.domain.*;
import com.brennaswitzer.cookbook.message.MutatePlanTree;
import com.brennaswitzer.cookbook.message.PlanMessage;
import com.brennaswitzer.cookbook.payload.TaskInfo;
import com.brennaswitzer.cookbook.repositories.TaskRepository;
import com.brennaswitzer.cookbook.util.UserPrincipalAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
@Transactional
public class PlanService {

    @Autowired
    protected TaskRepository taskRepo;

    @Autowired
    protected UserPrincipalAccess principalAccess;

    @Autowired
    protected SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ItemService itemService;

    protected Task getTaskById(Long id) {
        return getTaskById(id, AccessLevel.VIEW);
    }

    protected Task getTaskById(Long id, AccessLevel requiredAccess) {
        Task task = taskRepo.getOne(id);
        task.getTaskList().ensurePermitted(
                principalAccess.getUser(),
                requiredAccess
        );
        return task;
    }

    public List<Task> getTreeById(Long id) {
        return treeHelper(getTaskById(id, AccessLevel.VIEW));
    }

    private List<Task> treeHelper(Task task) {
        List<Task> result = new LinkedList<>();
        treeHelper(task, result);
        return result;
    }

    private void treeHelper(Task task, Collection<Task> collector) {
        collector.add(task);
        if (task.hasSubtasks()) {
            task.getOrderedSubtasksView()
                    .forEach(t -> treeHelper(t, collector));
        }
    }

    public void mutateTree(List<Long> ids, Long parentId, Long afterId) {
        Task parent = getTaskById(parentId, AccessLevel.CHANGE);
        Task after = afterId == null ? null : getTaskById(afterId, AccessLevel.VIEW);
        for (Long id : ids) {
            Task t = getTaskById(id, AccessLevel.CHANGE);
            parent.addSubtaskAfter(t, after);
            after = t;
        }
        if (isMessagingCapable()) {
            PlanMessage m = new PlanMessage();
            m.setType("tree-mutation");
            m.setInfo(new MutatePlanTree(ids, parentId, afterId));
            sendMessage(parent, m);
        }
    }

    private boolean isMessagingCapable() {
        return messagingTemplate != null;
    }

    private void sendToPlan(AggregateIngredient r, Task rTask) {
        r.getIngredients().forEach(ir ->
                sendToPlan(ir, rTask));
    }

    private void sendToPlan(IngredientRef<?> ir, Task rTask) {
        Task t = new Task(ir.getRaw(), ir.getQuantity(), ir.getIngredient(), ir.getPreparation());
        rTask.addSubtask(t);
        if (ir.getIngredient() instanceof AggregateIngredient) {
            sendToPlan((AggregateIngredient) ir.getIngredient(), t);
        }
    }

    public void addRecipe(Long planId, Recipe r) {
        Task rTask = new Task(r.getName(), r);
        Task plan = getTaskById(planId, AccessLevel.CHANGE);
        plan.addSubtask(rTask);
        sendToPlan(r, rTask);
        if (isMessagingCapable()) {
            taskRepo.flush(); // so that IDs will be available
            sendMessage(plan, buildCreationMessage(rTask));
        }
    }

    private PlanMessage buildCreationMessage(Task task) {
        Task parent = task.getParent();
        PlanMessage m = new PlanMessage();
        m.setId(parent.getId());
        m.setType("create");
        List<Task> tree = new LinkedList<>();
        tree.add(parent);
        treeHelper(task, tree);
        m.setInfo(TaskInfo.fromTasks(tree));
        return m;
    }

    private void sendMessage(Task task, Object message) {
        messagingTemplate.convertAndSend(
                "/topic/plan/" + task.getTaskList().getId(),
                message);
    }

    public void createItem(Object id, Long parentId, Long afterId, String name) {
        Task parent = getTaskById(parentId, AccessLevel.CHANGE);
        Task after = afterId == null ? null : getTaskById(afterId, AccessLevel.VIEW);
        Task task = taskRepo.save(new Task(name).of(parent, after));
        itemService.autoRecognize(task);
        if (isMessagingCapable()) {
            taskRepo.flush(); // so that IDs will be available
            PlanMessage m = buildCreationMessage(task);
            Map<Long, Object> newIds = new HashMap<>();
            newIds.put(task.getId(), id);
            m.setNewIds(newIds);
            sendMessage(parent, m);
        }
    }

    public void renameItem(Long id, String name) {
        Task task = getTaskById(id, AccessLevel.CHANGE);
        task.setName(name);
        itemService.updateAutoRecognition(task);
        if (isMessagingCapable()) {
            PlanMessage m = new PlanMessage();
            m.setId(task.getId());
            m.setType("update");
            m.setInfo(TaskInfo.fromTask(task));
            sendMessage(task, m);
        }
    }

    public void setItemStatus(Long id, TaskStatus status) {
        if (TaskStatus.COMPLETED.equals(status) || TaskStatus.DELETED.equals(status)) {
            deleteItem(id);
            return;
        }
        Task task = getTaskById(id, AccessLevel.CHANGE);
        task.setStatus(status);
        if (isMessagingCapable()) {
            PlanMessage m = new PlanMessage();
            m.setId(task.getId());
            m.setType("update");
            m.setInfo(TaskInfo.fromTask(task));
            sendMessage(task, m);
        }
    }

    public void deleteItem(Long id) {
        Task task = getTaskById(id, AccessLevel.CHANGE);
        Task plan = task.getTaskList();
        deleteItem(task);
        if (isMessagingCapable()) {
            PlanMessage m = new PlanMessage();
            m.setId(id);
            m.setType("delete");
            sendMessage(plan, m);
        }
    }

    private void deleteItem(Task t) {
        if (t.hasParent()) {
            t.getParent().removeSubtask(t);
        }
        taskRepo.delete(t);
    }

}
