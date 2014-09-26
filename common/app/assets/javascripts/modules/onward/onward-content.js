define([
    'raven',
    'common/modules/analytics/register',
    'common/modules/commercial/badges',
    'common/modules/component',
    'common/modules/ui/images'
], function (
    raven,
    register,
    badges,
    Component,
    images
) {
    function OnwardContent(config, context) {
        register.begin('series-content');
        this.config = config;
        this.context = context;
        this.endpoint = '/series/' + this.config.page.seriesId + '.json?shortUrl=' + encodeURIComponent(this.config.page.shortUrl);
        this.fetch(this.context, 'html');
    }

    Component.define(OnwardContent);

    OnwardContent.prototype.ready = function (container) {
        badges.add(container);
        images.upgrade(this.context);
        register.end('series-content');
    };

    OnwardContent.prototype.error = function () {
        register.error('series-content');
    };

    return OnwardContent;
});
