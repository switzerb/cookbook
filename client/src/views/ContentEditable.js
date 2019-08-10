import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { render } from 'react-dom'

// eslint-disable-next-line react/no-deprecated
class ContentEditable extends Component {
  
  constructor(props) {
   super(props)
   this.state = {
     __html: ''
   };
  }
  
  componentWillMount() {
    this.renderStatic(this.props);
  }
  
  componentWillReceiveProps(nextProps) {
    this.renderStatic(nextProps);
  }
  
  shouldComponentUpdate(nextProps, nextState) {
    return nextProps.disabled !== this.props.disabled ||
      nextState.__html !== this.state.__html;
  }
  
  componentDidUpdate(prevProps, prevState) {
    this.props.onComponentUpdate(prevProps, prevState);
  }
  
  onRender = node => {
    this.setState({__html: node ? node.innerHTML : ''});
  };
  
  renderStatic(nextProps) {
    if (!nextProps.children) {
      this.setState({__html: ''});
    }
    
    render(<i ref={this.onRender}>{nextProps.children}</i>, document.createElement('i'));
  }
  
  render() {
    const {children, onComponentUpdate, ...props} = this.props; // eslint-disable-line no-unused-vars
    
    return (
      <div
        {...props}
        contentEditable={!this.props.disabled}
        dangerouslySetInnerHTML={this.state}
      />
    );
  }
}

ContentEditable.defaultProps = {
  disabled: false,
  tabIndex: 0,
  onInput: () => {},
  onComponentUpdate: () => {}
}

ContentEditable.propTypes = {
  disabled: PropTypes.bool,
  componentDidUpdate: PropTypes.func,
  onComponentUpdate: PropTypes.func,
  children: PropTypes.node
}

export default ContentEditable
