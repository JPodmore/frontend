import React from 'react';
export default React.createClass({
    render: function() {
        return React.DOM.div({
            className: 'crossword__focussed-clue',
            dangerouslySetInnerHTML: {
                '__html': this.props.clueText
            }
        });
    }
});
