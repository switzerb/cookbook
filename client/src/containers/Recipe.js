import React from 'react'
import { Container } from 'flux/utils'
import { withRouter } from 'react-router-dom'
import RecipeDetail from '../views/RecipeDetail'
import RecipeStore from '../data/RecipeStore'
import LibraryStore from "../data/LibraryStore"
import PreferencesStore from "../data/PreferencesStore"

export default withRouter(Container.createFunctional(
    (props) => <RecipeDetail {...props}/>,
    () => [
        PreferencesStore,
        RecipeStore,
    ],
    (prevState, props) => {
        const { match } = props
        const id = parseInt(match.params.id, 10)
        const recipeLO = LibraryStore.getRecipeById(id)
        return {
            recipeLO,
            staged: PreferencesStore.isStaged(id),
        }
    },
    { withProps: true }
))