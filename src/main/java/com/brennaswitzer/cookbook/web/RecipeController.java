package com.brennaswitzer.cookbook.web;

import com.brennaswitzer.cookbook.domain.Ingredient;
import com.brennaswitzer.cookbook.domain.Recipe;
import com.brennaswitzer.cookbook.payload.IngredientInfo;
import com.brennaswitzer.cookbook.payload.RecipeAction;
import com.brennaswitzer.cookbook.services.LabelService;
import com.brennaswitzer.cookbook.services.RecipeService;
import com.brennaswitzer.cookbook.services.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.validation.Valid;
import javax.xml.ws.Response;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/recipe")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private LabelService labelService;

    @GetMapping("/")
    public Iterable<IngredientInfo> getRecipes(
            @RequestParam(name = "scope", defaultValue = "mine") String scope
    ) {
        boolean hasFilter = filter.length() > 0;
        List<Recipe> recipes;
        if ("everyone".equals(scope)) {
             recipes = hasFilter
                ? IngredientInfo.fromRecipes(recipeService.findRecipeByName(filter.toLowerCase()))
                : recipeService.findEveryonesRecipes();
        } else {
             recipes =  hasFilter
                ? IngredientInfo.fromRecipes(recipeService.findRecipeByNameAndOwner(filter.toLowerCase()))
                : IngredientInfo.fromRecipes(recipeService.findMyRecipes());
        }

        return recipes
                .stream()
                .map(IngredientInfo::from)
                .collect(Collectors.toList());
    }

    @PostMapping("")
    @Transactional
    public ResponseEntity<?> createNewRecipe(@Valid @RequestBody IngredientInfo info, BindingResult result) {
        // begin kludge (1 of 3)
        Recipe recipe = info.asRecipe(em);
        // end kludge (1 of 3)
        ResponseEntity<?> errors = validationService.validationService(result);
        if(errors != null) return errors;

        Recipe recipe1 = recipeService.createNewRecipe(recipe);
        labelService.updateLabels(recipe1, info.getLabels());
        return new ResponseEntity<>(IngredientInfo.from(recipe1), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateRecipe(@Valid @RequestBody IngredientInfo info, BindingResult result) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // begin kludge (2 of 3)
        Recipe recipe = info.asRecipe(em);
        // end kludge (2 of 3)

        ResponseEntity<?> errors = validationService.validationService(result);
        if(errors != null) return errors;

        Recipe recipe1 = recipeService.updateRecipe(recipe);
        labelService.updateLabels(recipe1, info.getLabels());
        return new ResponseEntity<>(IngredientInfo.from(recipe1), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public IngredientInfo getRecipeById(@PathVariable("id") Long id) {
        Recipe recipe = getRecipe(id);
        return IngredientInfo.from(recipe);
    }

    // begin kludge (3 of 3)
    @Autowired private EntityManager em;
    @SuppressWarnings("JavaReflectionMemberAccess")
    @GetMapping("/or-ingredient/{id}")
    public IngredientInfo getIngredientById(@PathVariable("id") Long id) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Ingredient i = em.find(Ingredient.class, id);

        // dynamic dispatch sure would be nice!
//        IngredientInfo.from(i);

        // a Visitor is a tad heavy for a throwaway kludge...

        // Java doesn't let you switch except on primitives...
//        switch (i.getClass().getSimpleName()) {
//            case "PantryItem":
//                return IngredientInfo.from((PantryItem) i);
//            case "Recipe":
//                return IngredientInfo.from((Recipe) i);
//            default:
//                throw new IllegalArgumentException("Can't deal with " + i.getClass().getSimpleName() + ". Yet!");
//        }

        // just reflect it. Screw. That. Poop.
        return (IngredientInfo) IngredientInfo.class
                .getMethod("from", i.getClass())
                .invoke(null, i);
    }
    // end kludge (3 of 3)

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipeById(id);

        return new ResponseEntity<>("Recipe was deleted", HttpStatus.OK);
    }

    @PostMapping("/{id}/labels")
    @Transactional
    public ResponseEntity<?> addLabel(@PathVariable Long id, @RequestBody String label) {
        Recipe recipe = getRecipe(id);
        labelService.addLabel(recipe, label);
        return new ResponseEntity<>(label, HttpStatus.OK);
    }

    @PostMapping("/_actions")
    @ResponseBody
    public Object performGlobalAction(
            @RequestBody RecipeAction action
    ) {
        return action.execute(recipeService);
    }

    @PostMapping("/{id}/_actions")
    @ResponseBody
    public Object performRecipeAction(
            @PathVariable("id") Long id,
            @RequestBody RecipeAction action
    ) {
        return action.execute(id, recipeService);
    }

    private Recipe getRecipe(@PathVariable("id") Long id) {
        Optional<Recipe> recipe = recipeService.findRecipeById(id);
        recipe.orElseThrow(NoResultException::new);
        return recipe.get();
    }


}
