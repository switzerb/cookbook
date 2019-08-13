import React from "react"
import PropTypes from "prop-types"
import {
    Icon,
    Input,
    Spin,
} from "antd"
import RecipeApi from "../data/RecipeApi"
import debounce from "../util/debounce"
import RingUI from "../util/ring-ui"

import styles from "./ElementBuilder.module.scss"

const UNITS = "cup,tsp,Tbsp,teaspoon,Tablespoon,ounce,oz".split(",")
const ITEMS = "bouillon cube,flour,eggs,flip-flops,flippers,salt,sugar,water,milk,cucumber".split(",")
const NUM_RE = /^[0-9]+(\.[0-9]*)?$/

const styleFromType = type => {
    switch (type) {
        case "AMOUNT": return "field_name"
        case "UNIT": return "operator"
        case "ITEM": return "field_value"
        default: return "text"
    }
}

class ElEdit extends React.PureComponent {

    constructor(...args) {
        super(...args)
        this.state = {
            recog: null,
        }
        this.recognizeDebounced = debounce(this.recognize.bind(this))
        this.onPaste = this.onPaste.bind(this)
    }

    componentDidMount() {
        this.recognize()
    }

    componentDidUpdate(prevProps) {
        if (this.props.value.raw === prevProps.value.raw) return
        this.recognizeDebounced()
    }

    recognize() {
        const {
            name,
            value,
            onChange,
        } = this.props
        if (value.raw == null || value.raw.trim().length === 0) return
        RecipeApi.recognizeElement(value.raw)
            .then(recog => {
                if (recog.raw !== this.props.value.raw) return
                const qr = recog.ranges.find(r =>
                    r.type === "AMOUNT")
                const ur = recog.ranges.find(r =>
                    r.type === "UNIT" || r.type === "NEW_UNIT")
                const nr = recog.ranges.find(r =>
                    r.type === "ITEM" || r.type === "NEW_ITEM")
                const textFromRange = r =>
                    r && recog.raw.substring(r.start, r.end)
                const stripMarkers = s => {
                    if (s == null) return s
                    if (s.length < 3) return s
                    const c = s.charAt(0).toLowerCase()
                    if (c !== s.charAt(s.length - 1)) return s
                    if (c >= 'a' && c <= 'z') return s
                    if (c >= '0' && c <= '9') return s
                    return s.substring(1, s.length - 1)
                }
                const q = textFromRange(qr)
                const qv = qr && qr.value
                const u = textFromRange(ur)
                const uv = ur && ur.value
                const n = textFromRange(nr)
                const nv = nr && nr.value
                const p = recog.ranges.reduce(
                    (p, r) =>
                        p.replace(recog.raw.substring(r.start, r.end), ''),
                    recog.raw,
                ).trim().replace(/\s+/g, ' ')
                onChange({
                    target: {
                        name,
                        value: {
                            raw: recog.raw,
                            quantity: qv,
                            uomId: uv,
                            units: stripMarkers(u),
                            ingredientId: nv,
                            ingredient: stripMarkers(n),
                            preparation: p,
                        },
                    },
                })
                return this.setState({
                    recog,
                    q, qv,
                    u, uv,
                    n, nv,
                    p,
                })
            })
    }

    onPaste(e) {
        const {
            onMultilinePaste,
        } = this.props
        if (onMultilinePaste == null) return // don't care!
        let text = e.clipboardData.getData("text")
        if (text == null) return
        text = text.trim()
        if (text.indexOf("\n") < 0) return // default behaviour
        e.preventDefault() // no default
        onMultilinePaste(text)
    }
    
    shitParser = (query, caret) => {
        if (query == null || query.trim().length === 0) return Promise.reject()
        if (query.trim() !== query) return Promise.reject()
        
        // incremental things here
        return RecipeApi.recognizeElement(query).then( response => {
            return Promise.resolve({
                    query,
                    caret,
                    ranges: response.ranges,
                    suggestions: response.suggestions
            })
    
        })
        
    }
    
    dataSource = ({query, caret}) => {
        const { name } = this.props
        if (this.props.value.raw !== query) {
            this.props.onChange({
                target: {
                    name: `${name}.raw`,
                    value: query,
                }
            })
        }
        
        return this.shitParser(query, caret).then( recog => {
            console.log("shitParser: ", recog)
            return {
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
                        completionStart: it.target.start,
                        completionEnd: it.target.end,
                        caret: caret,
                        description: `from ${it.target.start}-${it.target.end} (caret: ${caret})`
                    }
                    if (typeof it.name === "string") {
                        sug.option = it.name
                    } else {
                        sug.option = it.name
                        sug.matchingStart = it.target.start
                        sug.matchingEnd = it.target.end
                    }
                    return sug
                }),
            }
        })
    }
    
    render() {
        const { value } = this.props
        const { raw } = value
        const {
            recog,
            q, u, uv, n, nv, p,
        } = this.state
        
        return <React.Fragment>
            <div className={styles.root}>
            <RingUI.QueryAssist
                placeholder="placeholder"
                onApply={console.log}
                clear
                glass
                query={raw}
                dataSource={this.dataSource}
            />
            {recog == null
                ? <Hunk><Spin /></Hunk>
                : <Hunk>
                    {q && <Hunk style={{backgroundColor: "#fde"}}>{q}</Hunk>}
                    {u && <Hunk style={{backgroundColor: uv ? "#efd" : "#dfe"}}>{u}</Hunk>}
                    {n && <Hunk style={{backgroundColor: nv ? "#def" : "#edf"}}>{n}</Hunk>}
                    {p && <span>{n ? ", " : null}{p}</span>}
                </Hunk>
            }
            </div>
        </React.Fragment>
    }
}

const Hunk = ({children, style}) => <span style={{
    ...style,
    marginLeft: "0.4em",
    padding: "0 0.25em"
}}>{children}</span>

ElEdit.propTypes = {
    name: PropTypes.string.isRequired,
    value: PropTypes.shape({
        raw: PropTypes.string.isRequired,
        quantity: PropTypes.number,
        units: PropTypes.string,
        ingredientId: PropTypes.number,
        preparation: PropTypes.string,
    }).isRequired,
    onChange: PropTypes.func.isRequired,
    onMultilinePaste: PropTypes.func,
}

export default ElEdit
