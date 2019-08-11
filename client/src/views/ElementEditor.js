import React, { Component } from 'react'
import ContentEditable from "./ContentEditable"
import "./ElementEditor.scss"
import Caret from "./common/Caret"
import { findDOMNode } from 'react-dom'
import RecipeApi from "../data/RecipeApi"


function noop() {
}

class ElementEditor extends Component<{}> {
  
  constructor(props) {
    super(props)
    this.input = null
    this.immediateState = {
      raw: this.props.raw || ''
    }
    // AMOUNT, UNIT, NEW_UNIT, NEW_ITEM, ITEM there are five types of ranges
  }
  
  componentDidMount() {
    const raw = this.props.raw || ''
    
    this.immediateState = {
      raw,
      caret: Number.isFinite(this.props.caret) ? this.props.caret : raw.length,
    }
    this.setCaretPosition()
  }
  
  setCaretPosition = () => {
    const rawLength = this.immediateState.raw != null && this.immediateState.raw.length

    const newCaretPosition =
      this.immediateState.caret < rawLength
        ? this.immediateState.caret
        : rawLength
    const currentCaretPosition = this.caret.getPosition({avoidFocus: true})

    if (currentCaretPosition !== -1) {
      this.caret.setPosition(newCaretPosition >= 20 ? newCaretPosition : -1)
    }
  }
  
  handleCaretMove = e => {
    const caret = this.caret.getPosition()
    if ((caret !== this.immediateState.caret)) {
      this.immediateState.caret = caret
      this.requestData()
    }
  }
  
  requestData = () => {
  
    const MOCK = {
      "suggestions": [
        {
          "target": {
            "value": 49,
            "type": "ITEM",
            "end": 18,
            "start": 13
          },
          "name": "peanut oil"
        }
      ],
      "ranges": [
        {
          "value": 3,
          "type": "AMOUNT",
          "end": 0,
          "start": 0
        },
        {
          "value": 6,
          "type": "UNIT",
          "end": 5,
          "start": 2
        },
        {
          "value": 3,
          "type": "ITEM",
          "end": 11,
          "start": 7
        }
      ],
      "raw": "3 Tbsp Flour"
    }
  
  
    const raw = this.getRaw()
    RecipeApi.recognizeElement(raw).then( recognized => {
        // transform DOM style here
        if (this.getRaw() === recognized.raw) {
          this.transformStyle(MOCK)
        }
      }
    )
  }
  
  transformStyle(recognized) {
    const { raw, ranges } = recognized;
    
    const styled = Array.from(raw).map((letter, index) => {
      let className = ""
      const something = ranges.find(range => index >= range.start && index <= range.end)
      if(something) {
        className = something.type.toLowerCase()
      }
      return (
        <span
          // eslint-disable-next-line react/no-array-index-key
          key={index + letter}
          className={className}
        >{letter === ' ' ? '\u00a0' : letter}</span>
      )
    })
    
    this.immediateState = {
      raw: styled,
      caret: this.caret.getPosition()
    }
    
    this.forceUpdate()
  }
  
  getRaw() {
    return this.input.textContent.replace(/\s/g, ' ');
  }
  
  inputRef = node => {
    if (!node) {
      return;
    }
    
    this.input = findDOMNode(node);
    this.caret = new Caret(this.input);
  };
  
  handleKeyUp = e => {
    const props = {
      raw: this.getRaw(),
      caret: this.caret.getPosition()
    }

    if (this.immediateState.raw === props.raw) {
      this.handleCaretMove(e)
      return
    }
  
    this.immediateState = props
    this.requestData()
  }
  
  render() {
    const {raw} = this.immediateState
    
    return (
      <div>
        <ContentEditable
          ref={this.inputRef}
          onComponentUpdate={this.setCaretPosition}
          onKeyUp={this.handleKeyUp}
          onClick={this.handleCaretMove}
        >
          {<span>{raw}</span>}
        </ContentEditable>
      </div>
    )
  }
}

export default ElementEditor
