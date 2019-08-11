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
    this.state = {
      showSuggestions: true,
      raw: this.props.raw || ''
    }
  
    // AMOUNT, UNIT, NEW_UNIT, NEW_ITEM, ITEM there are five types of ranges
  
    // this is the data that comes back from recognize element
    this.stuff = {
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
          "value": 3.5,
          "type": "AMOUNT",
          "end": 5,
          "start": 0
        },
        {
          "value": 6,
          "type": "UNIT",
          "end": 12,
          "start": 6
        }
      ],
      "raw": "3 1/2 _Tbsp_ glerg"
    }
  }
  
  componentDidMount() {
    const raw = this.props.raw || ''
    
    this.immediateState = {
      raw,
      caret: Number.isFinite(this.props.caret) ? this.props.caret : raw.length,
    }
  
    //request styling
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
      this.caret.setPosition(newCaretPosition >= 0 ? newCaretPosition : -1)
    }
  }
  
  handleInput = () => {
    const raw = this.getRaw()
  }
  
  handleCaretMove = e => {
    const caret = this.caret.getPosition()
    
    if ((caret !== this.immediateState.caret)) {
      this.immediateState.caret = caret
      this.requestData()
    }
  }
  
  requestData = () => {
    const raw = this.getRaw()
    RecipeApi.recognizeElement(raw).then( recognized => {
        // transform DOM style here
        this.transformStyle(recognized)
      }
    )
  }
  
  transformStyle(recognized) {
    const { raw, ranges } = recognized;
    
    // console.log( raw.substring(nr.start, nr.end))
    //   return this.setState({
    //     raw,
    //     q: qr && raw.substring(qr.start, qr.end),
    //     u: ur && raw.substring(ur.start, ur.end),
    //     n: nr && raw.substring(nr.start, nr.end),
    //     p: recog.ranges.reduce(
    //       (
    //         p,
    //         r,
    //       ) => p.replace(recog.raw.substring(r.start, r.end), ''),
    //       recog.raw,
    //     ),
    //   })
    //
    // const styledraw =  Array.from(raw).map((letter, index) => {
    //   const className = "thing"
    //
    //   return (
    //     <span
    //       // eslint-disable-next-line react/no-array-index-key
    //       key={index + letter}
    //       className={className}
    //     >{letter === ' ' ? '\u00a0' : letter}</span>
    //   )
    // })
    this.setState({ raw: raw })
  
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
    const props = {raw: this.getRaw()}
    
    if (this.immediateState.raw === props.raw) {
      this.handleCaretMove(e)
      return
    }
    
    this.immediateState = props
    this.requestData()
  }
  
  render() {
    const {showSuggestions, raw} = this.state
    console.log(this.props);
    
    return (
      <div>
        <ContentEditable
          ref={this.inputRef}
          onInput={this.handleInput}
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
