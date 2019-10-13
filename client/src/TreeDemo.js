import React from "react"
import SortableTree from "react-sortable-tree"
import TreeTheme from "./TreeTheme"
import 'react-sortable-tree/style.css'

class TreeDemo extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            treeData: [{
                title: '1 lbs Chicken',
                children: [{
                    title: '3 Eggs',
                }],
                expanded: true,
            }],
        }
    }

    render() {
        return <div style={{height: 400}}>
            <SortableTree
                theme={TreeTheme}
                treeData={this.state.treeData}
                onChange={(data, ...rest) => {
                    console.log("TREE CHANGE", data, ...rest)
                }}
                onMoveNode={(...args) => {
                    console.log("TREE MOVE", ...args)
                }}
            />
        </div>

    }
}

export default TreeDemo
