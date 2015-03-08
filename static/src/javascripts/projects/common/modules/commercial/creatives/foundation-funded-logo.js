import $ from 'common/utils/$';
import config from 'common/utils/config';
import template from 'common/utils/template';
import 'text!common/views/commercial/creatives/logo-foundation-funded.html';
import 'text!common/views/commercial/creatives/logo-foundation-funded-partners.html';

/**
 * Create the foundation logo
 *
 * * https://www.google.com/dfp/59666047#delivery/CreateCreativeTemplate/creativeTemplateId=10022127
 * * https://www.google.com/dfp/59666047#delivery/CreateCreativeTemplate/creativeTemplateId=10026327
 */
var Template = function($adSlot, params) {
    this.$adSlot = $adSlot;
    this.params = params;
    this.params.header = (!config.page.isFront && config.page.sponsorshipTag) ?
        'Guardian ' + config.page.sponsorshipTag + ' is supported by:' : 'Supported by:';
};

Template.prototype.create = function() {
    var templateName = 'logo-foundation-funded' + (this.params.hasPartners ? '-partners' : '');

    require(['text!common/views/commercial/creatives/' + templateName + '.html'], function(creativeTpl) {
        var creativeHtml = template(creativeTpl, this.params);

        $.create(creativeHtml)
            .appendTo(this.$adSlot);
    }.bind(this));
};

export default Template;
