import React from 'react'
import RingUI from "../util/ring-ui"

// the "database"
const UNITS = "cup,tsp,Tbsp,teaspoon,Tablespoon,ounce,oz".split(",")
const ITEMS = "bouillon cube,flour,eggs,flip-flops,flippers,salt,sugar,water,milk,cucumber".split(",")
const NUM_RE = /^[0-9]+(\.[0-9]*)?$/

// the parser "API"
const parseThatShit = (query, caret, callback) => {
    if (query == null || query.trim().length === 0) return callback()
    if (query.trim() !== query) return callback()
    const ranges = []
    const suggestions = []
    const words = query.split(" ")
    let wIdx = 0
    let amount = null
    let unit = null
    if (NUM_RE.test(words[wIdx])) {
        amount = parseFloat(words[wIdx])
        if (!isNaN(amount)) {
            ranges.push({
                start: 0,
                end: words[wIdx].length,
                type: "amount",
            })
        }
        wIdx += 1
    }
    for (let i = wIdx; i < words.length; i++) {
        const w = words[i]
        const sow = words.slice(0, i)
            .reduce((n, w) => n + w.length, i)
        if (amount != null && unit == null) {
            // see about a unit
            let u = UNITS.find(u => u === w)
            if (u != null) {
                // exact match!
                unit = u
                ranges.push({
                    start: sow,
                    end: sow + u.length,
                    type: "unit",
                })
                continue
            }
            const partials = UNITS.filter(u => u.indexOf(w) === 0)
            partials.forEach(u => { // eslint-disable-line no-loop-func
                unit = u // not really true, but whatever
                suggestions.push({
                    option: u,
                    type: "unit",
                    accept: {
                        start: sow,
                        end: sow + w.length,
                        caret: sow + u.length,
                    },
                })
            })
        }
        let it = ITEMS.find(it => it === w)
        if (it != null) {
            // exact match!
            ranges.push({
                start: sow,
                end: sow + it.length,
                type: "item",
            })
            continue
        }
        ITEMS.filter(it => it.indexOf(w) === 0)
            .forEach(it => {
                suggestions.push({
                    option: it,
                    type: "item",
                    accept: {
                        start: sow,
                        end: sow + w.length,
                        caret: sow + it.length,
                    },
                })
            })
        ITEMS.filter(it => it.indexOf(w) !== 0 && it.indexOf(w) > 0)
            .forEach(it => {
                const idx = it.indexOf(w)
                suggestions.push({
                    option: {
                        name: it,
                        start: idx,
                        end: idx + w.length,
                    },
                    type: "item",
                    accept: {
                        start: sow,
                        end: sow + w.length,
                        caret: sow + it.length,
                    },
                })
            })
    }
    const response = {
        query,
        caret,
        ranges,
        suggestions,
    }
    setTimeout(() => callback(response), 150 + Math.random() * 300)
}


const styleFromType = type => {
    switch (type) {
        case "amount": return "field_name"
        case "unit": return "operator"
        case "item": return "field_value"
        default: return "text"
    }
}

const dataSource = ({query, caret}) =>
    new Promise(resolve =>
        parseThatShit(query, caret, recog => {
            if (recog == null) resolve(null)
            resolve({
                query: recog.query,
                caret: recog.caret,
                styleRanges: recog.ranges.map(it => ({
                    start: it.start,
                    length: it.end - it.start,
                    style: styleFromType(it.type),
                })),
                suggestions: recog.suggestions.map(it => {
                    const sug = {
                        group: it.type,
                        completionStart: it.accept.start,
                        completionEnd: it.accept.end,
                        caret: it.accept.caret,
                        description: `from ${it.accept.start}-${it.accept.end} (caret: ${it.accept.caret})`
                    }
                    if (typeof it.option === "string") {
                        sug.option = it.option
                    } else {
                        sug.option = it.option.name
                        sug.matchingStart = it.option.start
                        sug.matchingEnd = it.option.end
                    }
                    return sug
                }),
            })
        }))

const ElementBuilder = () => {
    return (
        <div>
            <RingUI.QueryAssist
                placeholder="placeholder"
                onApply={console.log}
                clear={true}
                query="1 cu fl"
                dataSource={dataSource}
            />
        </div>
    )
}

export default ElementBuilder
