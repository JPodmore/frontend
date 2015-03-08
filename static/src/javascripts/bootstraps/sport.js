import $ from 'common/utils/$';
import config from 'common/utils/config';
import Component from 'common/modules/component';

function init() {

    var cricketScore, parentEl,
        matchIdentifier = config.page.cricketMatch;

    if (matchIdentifier) {
        cricketScore = new Component();
        parentEl = $('.js-cricket-score')[0];

        cricketScore.endpoint = '/sport/cricket/match/' + matchIdentifier + '.json';
        cricketScore.fetch(parentEl, 'summary');
    }
}

export default {
    init: init
};
