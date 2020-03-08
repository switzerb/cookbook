import React from "react"
import SortableTree from "react-sortable-tree"
import TreeTheme from "./TreeTheme"
import 'react-sortable-tree/style.css'

class TreeDemo extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            treeData: [{
                title: "Sunday 4/4",
                children: [{
                    title: 'Enchiladas',
                    expanded: true,
                    children: [{
                        title: 'Chicken',
                    }],
                }],
                expanded: true,
            }, {
                title: "Monday 4/5",
                children: [{
                    title: 'Enchiladas',
                    children: [{
                        title: 'Scallions',
                    }, {
                        title: 'Tortillas',
                    }],
                    expanded: true,
                }],
                expanded: true
            }],
        }
    }

    render() {
        return <div style={{height: 800}}>
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
