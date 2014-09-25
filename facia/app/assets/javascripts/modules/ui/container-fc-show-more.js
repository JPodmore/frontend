define([
    'common/utils/$',
    'bonzo',
    'bean'
], function (
    $,
    bonzo,
    bean
) {
    return function (container) {
        var $container = bonzo(container),
            className = 'fc-show-more--hidden',
            $button = $.create(
            '<button class="collection__show-more tone-background tone-news" data-test-id="show-more" data-link-name="Show more | 1">' +
            '<span class="collection__show-more__icon">' +
            '<span class="i i-plus-white-mask"></span>' +
            '<span class="i i-plus-white"></span>' +
            '</span>' +
            '<span class="u-h">Show more</span>' +
            '</button>'
        );

        function showMore() {
            /**
             * Do not remove: it should retain context for the click stream module, which recurses upwards through
             * DOM nodes.
             */
            $button.hide();
            $container.removeClass(className);
        }

        $button.appendTo($container);
        bean.on($button[0], 'click', showMore);
        $container.removeClass('js-container--fc-show-more');
    };
});
