import React from 'react'
import QueryAssist from "../vendor/ringui/components/query-assist/query-assist"


const dataSource = ({query, caret}) => ({
  query,
  caret,
  styleRanges: [
    {start: 0, length: 1, style: 'text'},
    {start: 1, length: 1, style: 'field_value'},
    {start: 2, length: 1, style: 'field_name'},
    {start: 3, length: 1, style: 'operator'}
  ],
  suggestions: [{
    option: 'test',
    suffix: ' ',
    description: '1',
    matchingStart: 0,
    matchingEnd: query.length,
    caret: 2,
    completionStart: 0,
    completionEnd: query.length,
    group: 'Recipes'
  }, {
    option: 'test.1',
    suffix: ' ',
    description: '2',
    matchingStart: 0,
    matchingEnd: query.length,
    caret: 2,
    completionStart: 0,
    completionEnd: query.length,
    group: 'Ingredients'
  }]
});

const ElementBuilder = () => {
  
  return (
    <div>
      <QueryAssist
        placeholder="placeholder"
        onApply={console.log}
        dataSource={dataSource}
      />
    </div>
  )
}

export default ElementBuilder
