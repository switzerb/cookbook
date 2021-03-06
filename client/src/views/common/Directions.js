import PropTypes from "prop-types";
import React from "react";
import Markdown from "./Markdown";

const Directions = ({text}) => {
    if (text == null) return null;
    text = text.trim();
    if (text.length === 0) return null;
    // if there appears to be a markdown list, assume it's markdown
    if (/(^|\n) *(1[.)]|[-*]) +\S.*\n/.test(text)) {
        return <Markdown text={text} />;
    }
    // ok, just split it on line breaks and call it good.
    return text.split("\n").map((line, i) =>
        <p key={i}>{line}</p>);
};

Directions.propTypes = {
    text: PropTypes.string.isRequired,
};

export default Directions;