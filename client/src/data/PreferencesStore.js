import { ReduceStore } from "flux/utils"
import Dispatcher from './dispatcher'
import { Map } from "immutable"
import UserActions from "./UserActions"
import TaskActions from "./TaskActions"
import { LOCAL_STORAGE_PREFERENCES } from "../constants/index"
import {
    getJsonItem,
    setJsonItem,
} from "../util/storage"
import UserStore from "./UserStore"
import LibraryActions from "./LibraryActions"
import {
    addDistinct,
    removeDistinct,
} from "../util/arrayAsSet"

const Prefs = {
    ACTIVE_TASK_LIST: "activeTaskList",
    DEV_MODE: "devMode",
    STAGED_RECIPES: "stagedRecipes",
}

const setPref = (state, key, value) => {
    state = state.set(key, value)
    setJsonItem(LOCAL_STORAGE_PREFERENCES, state)
    return state
}

class PreferencesStore extends ReduceStore {
    constructor() {
        super(Dispatcher)
    }

    getInitialState() {
        return new Map(getJsonItem(LOCAL_STORAGE_PREFERENCES))
    }

    reduce(state, action) {
        switch (action.type) {
            case UserActions.RESTORE_PREFERENCES: {
                return new Map(action.preferences)
            }
            case TaskActions.SELECT_LIST: {
                return setPref(state, Prefs.ACTIVE_TASK_LIST, action.id)
            }
            case UserActions.SET_DEV_MODE: {
                if (!UserStore.isDeveloper()) return state
                return setPref(state, Prefs.DEV_MODE, action.enabled)
            }
            case LibraryActions.STAGE_RECIPE: {
                return setPref(state, Prefs.STAGED_RECIPES,
                    addDistinct(state.get(Prefs.STAGED_RECIPES), action.id))
            }
            case LibraryActions.UNSTAGE_RECIPE: {
                return setPref(state, Prefs.STAGED_RECIPES,
                    removeDistinct(state.get(Prefs.STAGED_RECIPES), action.id))
            }
            case LibraryActions.UNSTAGE_ALL_RECIPES: {
                return setPref(state, Prefs.STAGED_RECIPES, [])
            }
            default:
                return state
        }
    }

    getActiveTaskList() {
        return this.getState().get(Prefs.ACTIVE_TASK_LIST)
    }

    isDevMode() {
        return this.getState().get(Prefs.DEV_MODE)
    }

    getStagedRecipeIds() {
        return this.getState().get(Prefs.STAGED_RECIPES) || []
    }

    isStaged(id) {
        return this.getStagedRecipeIds().indexOf(id) >= 0
    }

}

export default new PreferencesStore()