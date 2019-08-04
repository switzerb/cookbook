package com.brennaswitzer.cookbook.payload;

import com.brennaswitzer.cookbook.services.RecipeService;

import java.util.List;

public class RecipeAction {

    public enum Type {
        DISSECT_RAW_INGREDIENT,
        PREVIEW_SHOPPING_LIST,
        ASSEMBLE_SHOPPING_LIST,
    }

    private Type type;

    private Long listId;

    private List<Long> additionalRecipeIds;

    private RawIngredientDissection dissection;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public RawIngredientDissection getDissection() {
        return dissection;
    }

    public void setDissection(RawIngredientDissection dissection) {
        this.dissection = dissection;
    }

    public List<Long> getAdditionalRecipeIds() {
        return additionalRecipeIds;
    }

    public void setAdditionalRecipeIds(List<Long> additionalRecipeIds) {
        this.additionalRecipeIds = additionalRecipeIds;
    }

    public Object execute(Long recipeId, RecipeService service) {
        switch (getType()) {
            case DISSECT_RAW_INGREDIENT:
                service.recordDissection(dissection);
                return null;
            case PREVIEW_SHOPPING_LIST:
                return service.previewShoppingList(recipeId, additionalRecipeIds);
            case ASSEMBLE_SHOPPING_LIST:
                service.assembleShoppingList(recipeId, additionalRecipeIds, getListId(), true);
                return null;
        }
        throw new IllegalStateException("Unregcognized RecipeAction: " + getType());
    }

}
