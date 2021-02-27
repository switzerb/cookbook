import PropTypes from "prop-types";
import React from "react";
import FriendStore from "../data/FriendStore";
import LibraryStore from "../data/LibraryStore";
import useFluxStore from "../data/useFluxStore";
import UserStore from "../data/UserStore";
import LoadObject from "../util/LoadObject";
import RecipeDetail from "../views/cook/RecipeDetail";

export const buildFullRecipeLO = id => {
    let lo = LibraryStore.getIngredientById(id);
    if (!lo.hasValue()) return lo;

    const subIds = new Set();
    const subs = [];
    let loading = false;
    const prepRecipe = recipe => ({
        ...recipe,
        ingredients: (recipe.ingredients || []).map(ref => {
            if (!ref.ingredientId) return ref;
            const iLO = LibraryStore.getIngredientById(ref.ingredientId);
            if (iLO.isLoading()) {
                loading = true;
            }
            if (!iLO.hasValue()) return ref;
            const ing = iLO.getValueEnforcing();
            ref = {
                ...ref,
                ingredient: ing,
            };
            if (ing.type !== "Recipe") return ref;
            if (subIds.has(ing.id)) return ref;
            subIds.add(ing.id);
            subs.push(prepRecipe(ing));
            return ref;
        }),
    });
    const recipe = prepRecipe(lo.getValueEnforcing());
    recipe.subrecipes = subs;
    lo = LoadObject.withValue(recipe);
    if (loading) {
        lo = lo.loading();
    }
    return lo;
};

const Recipe = ({match}) => {
    const id = parseInt(match.params.id, 10);
    const state = useFluxStore(
        () => {
            let recipeLO = buildFullRecipeLO(id);
            const state = {
                recipeLO,
            };
            if (!recipeLO.hasValue()) return state;

            const recipe = recipeLO.getValueEnforcing();
            state.subrecipes = recipe.subrecipes;
            const profileLO = UserStore.getProfileLO();
            if (!profileLO.hasValue()) return state;
            const me = profileLO.getValueEnforcing();
            state.mine = recipe.ownerId === me.id;
            state.ownerLO = state.mine
                ? profileLO
                : FriendStore.getFriendLO(recipe.ownerId);

            return state;
        },
        [
            LibraryStore,
            UserStore,
        ],
        [id],
    );

    return <RecipeDetail {...state} />;
};

Recipe.propTypes = {
    match: PropTypes.shape({
        params: PropTypes.shape({
            id: PropTypes.string.isRequired,
        }).isRequired,
    }).isRequired
};

export default Recipe;
