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
            }],
        }
    }

    render() {
        return <div style={{height: 400}}>
            <SortableTree
                theme={TreeTheme}
                treeData={this.state.treeData}
                onChange={treeData => {
                    this.setState({treeData})
                }}
            />
        </div>

    }
}

export default TreeDemo
