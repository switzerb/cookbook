import React, { Component } from 'react'
import ContentEditable from "./ContentEditable"
import "./ElementEditor.scss"
import Caret from "./common/Caret"
import { findDOMNode } from 'react-dom'

class ElementEditor extends Component<{}> {
  
  constructor(props) {
    super(props)
    this.input = null
    this.immediateState = null
    this.state = {
      showSuggestions: true
    }
  }
  
  componentDidMount() {
    const element = ''
    
    this.immediateState = {
      element,
      caret: Number.isFinite(this.props.caret) ? this.props.caret : element.length,
    }
    
    // request styling for the element
    
    this.setCaretPosition()
  }
  
  setCaretPosition = () => {
    const elementLength = this.immediateState.element != null && this.immediateState.element.length
    
    const newCaretPosition =
      this.immediateState.caret < elementLength
        ? this.immediateState.caret
        : elementLength
    const currentCaretPosition = this.caret.getPosition({avoidFocus: true})
    
    if (currentCaretPosition !== -1) {
      this.caret.setPosition(newCaretPosition >= 0 ? newCaretPosition : -1)
    }
  }
  
  handleInput = () => {
    const element = this.getElement()
  }
  
  handleCaretMove = e => {
    const caret = this.caret.getPosition()
    
    if ((caret !== this.immediateState.caret)) {
      this.immediateState.caret = caret
      this.requestData()
    }
  }
  
  requestData = () => {
    console.log("data things")
  }
  
  getElement() {
    return this.input.textContent.replace(/\s/g, ' ');
  }
  
  inputRef = node => {
    if (!node) {
      return;
    }
    
    this.input = findDOMNode(node);
    this.caret = new Caret(this.input);
  };
  
  render() {
    const { showSuggestions } = this.state;
    
    return (
      <div>
        <ContentEditable
          ref={this.inputRef}
          onInput={this.handleInput}
          onComponentUpdate={this.setCaretPosition}
          onClick={this.handleCaretMove}
        />
      </div>
    )
  }
}

export default ElementEditor
