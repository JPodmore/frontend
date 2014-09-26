define([
    'bonzo',
    'qwery'
], function (
    bonzo,
    qwery
) {

    function $(selector, context) {
        return bonzo(qwery(selector, context));
    }
    $.create = function (s) {
        return bonzo(bonzo.create(s));
    };
    $.ancestor = function (el, c) {
        if (el.tagName.toLowerCase() === 'html') {
            return false;
        }
        if (!el.parentNode || bonzo(el.parentNode).hasClass(c)) {
            return el.parentNode;
        } else {
            return $.ancestor(el.parentNode, c);
        }
    };

    return $;

}); // define
