import { ReduceStore } from "flux/utils"
import Dispatcher from "./dispatcher"
import LoadObject from "../util/LoadObject"
import PreferencesStore from "./PreferencesStore"
import LibraryActions from "./LibraryActions"
import hotLoadObject from "../util/hotLoadObject"
import RecipeApi from "./RecipeApi"

class ListPreviewStore extends ReduceStore {
    constructor() {
        super(Dispatcher)
    }

    getInitialState() {
        return LoadObject.empty()
    }

    reduce(state, action) {
        switch (action.type) {
            case LibraryActions.LOAD_LIST_PREVIEW: {
                RecipeApi.previewShoppingList(PreferencesStore.getStagedRecipeIds())
                return state.loading()
            }

            case LibraryActions.LIST_PREVIEW_LOADED: {
                return LoadObject.withValue(action.data)
            }

            case LibraryActions.LIST_PREVIEW_ERROR: {
                return LoadObject.withError(action.error)
            }

            default:
                return state
        }
    }

    getPreview() {
        return hotLoadObject(
            () => this.getState(),
            () => Dispatcher.dispatch({
                type: LibraryActions.LOAD_LIST_PREVIEW,
            }),
        )
    }

}

export default new ListPreviewStore()