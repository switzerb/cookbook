import typedAction from "../util/typedAction"
import PropTypes from "prop-types"

const sendShape = {
    listId: PropTypes.number,
    recipeIds: PropTypes.arrayOf(PropTypes.number),
}

const dissectionComponentType = PropTypes.shape({
    start: PropTypes.number.isRequired,
    end: PropTypes.number.isRequired,
    text: PropTypes.string.isRequired,
})

const RecipeActions = {
    CREATE_RECIPE: 'recipe/create-recipe',
    RECIPE_CREATED: 'recipe/recipe-created',
    CANCEL_ADD: typedAction("recipe/cancel-add", {}),
    UPDATE_RECIPE: 'recipe/update-recipe',
    RECIPE_UPDATED: 'recipe/recipe-updated',
    CANCEL_EDIT: typedAction("recipe/cancel-edit", {
        id: PropTypes.number.isRequired,
    }),
    RECIPE_DELETED: 'recipe/recipe-deleted',
    LOAD_EMPTY_RECIPE: 'recipe/load-empty-recipe',
    LOAD_RECIPE_DRAFT: 'recipe/load-recipe/draft',
    DRAFT_RECIPE_UPDATED: 'recipe/draft-recipe-updated',
    SAVE_DRAFT_RECIPE: 'recipe/save-draft-recipe',
    ASSEMBLE_SHOPPING_LIST: typedAction("recipe/assemble-shopping-list", sendShape),
    SHOPPING_LIST_ASSEMBLED: typedAction("recipe/shopping-list-assembled", sendShape),
    SEND_TO_SHOPPING_LIST: "recipe/send-to-shopping-list",
    SHOPPING_LIST_SENT: "recipe/shopping-list-sent",
    RAW_INGREDIENT_DISSECTED: typedAction("recipe/raw-ingredient-dissected", {
        recipeId: PropTypes.number.isRequired,
        raw: PropTypes.string.isRequired,
        prep: PropTypes.string,
        quantity: dissectionComponentType,
        units: dissectionComponentType,
        name: dissectionComponentType.isRequired,
    }),
    DISSECTION_RECORDED: typedAction("recipe/dissection-recorded", {
        recipeId: PropTypes.number.isRequired,
        raw: PropTypes.string.isRequired,
    }),
    NEW_DRAFT_INGREDIENT_YO: "recipe/new-draft-ingredient-yo",
    KILL_DRAFT_INGREDIENT_YO: "recipe/kill-draft-ingredient-yo",
    MULTI_LINE_DRAFT_INGREDIENT_PASTE_YO: "recipe/multi-line-draft-ingredient-paste-yo",
}

export default RecipeActions