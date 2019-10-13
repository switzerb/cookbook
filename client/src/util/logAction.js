// If this is non-empty, only action types containing one of the listed strings
// will be logged.
const whitelist = [
]

// If whitelist is empty, action types containing any of the listed strings will
// NOT be logged.
const blacklist = [
    "route/",
    "temporal/",
    "window/",
]

const logAction = action => {
    if (whitelist && whitelist.length > 0) {
        if (whitelist.every(it => action.type.indexOf(it) < 0)) return
    } else if (blacklist && blacklist.length > 0) {
        if (blacklist.some(it => action.type.indexOf(it) >= 0)) return
    }
    const temp = {...action}
    delete temp.type
    const keys = Object.keys(temp)
    const args = ["FLUX>", typeof action.type === "string"
        ? action.type
        : action.type.toString()]
    if (keys.length === 1) {
        args.push(keys[0], action[keys[0]])
    } else {
        args.push(temp)
    }
    // eslint-disable-next-line no-console
    console.log(...args)
}

export default logAction