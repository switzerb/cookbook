import {ReduceStore} from "flux/utils";
import Dispatcher from './dispatcher';
import {
    List,
    OrderedMap
} from "immutable";
import PantryItemActions from "./PantryItemActions";
import PantryItem from "../models/PantryItem";

class PantryItemStore extends ReduceStore {
    
    constructor() {
        super(Dispatcher);
    }
    
    getInitialState() {
        return new OrderedMap({
            pantry_items: new List(),
        });
    }
    
    getPantryItems() {
        return this.getState().get('pantry_items');
    }
    
    reduce(state, action) {
        switch (action.type) {
            case PantryItemActions.PANTRYITEMS_LOADED: {
                let items = List(action.data.map( item => {
                    return (new PantryItem({
                        id: item.ingredientId,
                        name: item.name,
                        aisle: item.aisle
                    }))
                }));
        
                return state.setIn(['pantry_items'], items);
            }
    
            case PantryItemActions.PANTRYITEMS_ADDED: {
                if(!action.data) {
                    return state;
                }
                return state.set('pantry_items', state.get('pantry_items').push(action.data))
            }
    
            default: {
                return state;
            }
        }
    }
}

export default new PantryItemStore();