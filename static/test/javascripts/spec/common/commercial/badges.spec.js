define([
    'fastdom',
    'qwery',
    'common/utils/$',
    'common/utils/template',
    'helpers/fixtures',
    'helpers/injector'
], function (
    fastdom,
    qwery,
    $,
    template,
    fixtures,
    Injector
) {
    return new Injector()
        .store(['common/utils/config'])
        .require(['common/modules/commercial/badges', 'mocks'], function (badges, mocks) {

            describe('Badges', function () {

                var fixturesConfig = {
                        id: 'badges',
                        fixtures: [
                            '<div class="facia-container">\
                                <div class="container">\
                                    <div class="js-container__header"></div>\
                                </div>\
                                <div class="container">\
                                    <div class="js-container__header"></div>\
                                </div>\
                            </div>'
                        ]
                    },
                    preBadges = function (sponsorship, sponsor) {
                        var header;
                        switch (sponsorship) {
                            case 'sponsoredfeatures':
                                header = 'Sponsored by:';
                                break;

                            case 'advertisement-features':
                                header = 'Brought to you by:';
                                break;

                            default:
                                header = 'Supported by:';
                        }
                        return template(
                            '<div class="ad-slot--paid-for-badge__inner ad-slot__content--placeholder">\n' +
                            '    <h3 class="ad-slot--paid-for-badge__header">{{header}}</h3>\n' +
                            '    <p class="ad-slot--paid-for-badge__header">{{sponsor}}</p>\n' +
                            '</div>',
                            {
                                header: header,
                                sponsor: sponsor
                            }
                        );
                    },
                    $fixtureContainer;

                beforeEach(function () {
                    mocks.store['common/utils/config'].images = {
                        commercial: {}
                    };
                    mocks.store['common/utils/config'].page = {
                        section: 'news'
                    };
                    mocks.store['common/utils/config'].switches = {
                        sponsored: true
                    };

                    $fixtureContainer = fixtures.render(fixturesConfig);
                });

                afterEach(function () {
                    badges.reset();
                    fixtures.clean(fixturesConfig.id);
                });

                it('should exist', function () {
                    expect(badges).toBeDefined();
                });

                it('should not display ad slot if sponsored switch is off', function () {
                    mocks.store['common/utils/config'].switches.sponsored = false;
                    expect(badges.init()).toBe(false);
                    expect(qwery('.ad-slot', $fixtureContainer).length).toBe(0);
                });

                describe('sponsored pages', function () {

                    [
                        {
                            type: 'sponsoredfeatures',
                            name: 'spbadge1'
                        },
                        {
                            type: 'advertisement-features',
                            name: 'adbadge1'
                        },
                        {
                            type: 'foundation-features',
                            name: 'fobadge1'
                        }
                    ].forEach(function (badge) {

                            it(
                                'should add "' + badge.name + '" badge to first container if page is ' + badge.type,
                                function (done) {
                                    $('.facia-container', $fixtureContainer)
                                        .addClass('js-sponsored-front')
                                        .attr('data-sponsorship', badge.type);
                                    badges.init().then(function () {
                                        var $adSlot = $('.container:first-child .ad-slot', $fixtureContainer)
                                            .first();

                                        expect($adSlot.data('name')).toBe(badge.name);
                                        expect($adSlot.hasClass('ad-slot--paid-for-badge--front')).toBeTruthy();

                                        done();
                                    });
                                }
                            );

                            it('should add pre-badge if sponsor\'s name available', function (done) {
                                var sponsor = 'Unilever',
                                    container = $('.facia-container', $fixtureContainer).first()
                                        .addClass('js-sponsored-front')
                                        .attr({
                                            'data-sponsor': sponsor,
                                            'data-sponsorship': badge.type
                                        })[0];
                                badges.init().then(function () {
                                    expect($('.ad-slot', container).html()).toBe(preBadges(badge.type, sponsor));
                                    done();
                                });
                            });
                        });
                });

                describe('sponsored containers', function () {

                    var configs = [
                        {
                            type: 'sponsoredfeatures',
                            name: 'spbadge1'
                        },
                        {
                            type: 'advertisement-features',
                            name: 'adbadge1'
                        },
                        {
                            type: 'foundation-features',
                            name: 'fobadge1'
                        }
                    ];

                    configs.forEach(function (badge) {
                        it('should add "' + badge.name + '" badge to ' + badge.type + ' container', function (done) {
                            $('.container', $fixtureContainer).first()
                                .addClass('js-sponsored-container')
                                .attr('data-sponsorship', badge.type);
                            badges.init().then(function () {
                                var $adSlot = $('.facia-container .container:first-child .ad-slot', $fixtureContainer).first();
                                expect($adSlot.data('name')).toBe(badge.name);
                                expect($adSlot.hasClass('ad-slot--paid-for-badge--front')).toBeTruthy();
                                done();
                            });
                        });
                    });

                    configs.forEach(function (badge) {
                        it('should not add more than one of the same badge', function (done) {
                            $('.container', $fixtureContainer)
                                .addClass('js-sponsored-container')
                                .attr('data-sponsorship', badge.type);
                            badges.init().then(function () {
                                expect(qwery('.facia-container .ad-slot[data-name="' + badge.name + '"]').length).toBe(1);
                                done();
                            });
                        });
                    });

                    configs.forEach(function (badge) {
                        it('should add pre-badge if sponsor\'s name available', function (done) {
                            var sponsor = 'Unilever',
                                container = $('.container', $fixtureContainer).first()
                                    .addClass('js-sponsored-container')
                                    .attr({
                                        'data-sponsor': sponsor,
                                        'data-sponsorship': badge.type
                                    })[0];
                            badges.init().then(function () {
                                expect($('.ad-slot', container).html()).toBe(preBadges(badge.type, sponsor));
                                done();
                            });
                        });
                    });

                    it('should not add a badge if one already exists', function (done) {
                        $('.container__header', $fixtureContainer).first()
                            .after('<div class="ad-slot--paid-for-badge"></div>');
                        badges.init();
                        fastdom.defer(function () {
                            expect(qwery('.facia-container .ad-slot', $fixtureContainer).length).toBe(0);
                            done();
                        });
                    });

                    it('should add container\'s keywords to ad', function (done) {
                        $('.container', $fixtureContainer).first()
                            .addClass('js-sponsored-container')
                            .attr({
                                'data-keywords': 'russia,ukraine',
                                'data-sponsorship': 'sponsoredfeatures'
                            });
                        badges.init().then(function () {
                            expect($('.facia-container .ad-slot', $fixtureContainer).data('keywords')).toBe('russia,ukraine');
                            done();
                        });
                    });

                    it('should add container\'s keywords to ad', function (done) {
                        $('.facia-container .container', $fixtureContainer)
                            .first()
                            .addClass('js-sponsored-container')
                            .attr({
                                'data-keywords': 'russia,ukraine',
                                'data-sponsorship': 'sponsoredfeatures'
                            });
                        badges.init().then(function () {
                            expect($('.facia-container .ad-slot', $fixtureContainer).data('keywords')).toBe('russia,ukraine');
                            done();
                        });
                    });

                    it('should increment badge id if multiple badges added', function (done) {
                        var $containers = $('.container', $fixtureContainer)
                            .addClass('js-sponsored-container')
                            .attr('data-sponsorship', 'sponsoredfeatures');
                        badges.init().then(function () {
                            expect(qwery('#dfp-ad--spbadge1', $containers[0]).length).toBe(1);
                            expect(qwery('#dfp-ad--spbadge2', $containers[1]).length).toBe(1);
                            done();
                        });
                    });

                    it('should be able to add badge to a container', function (done) {
                        var $container = $('.container', $fixtureContainer).first()
                            .addClass('js-sponsored-container')
                            .attr('data-sponsorship', 'sponsoredfeatures');
                        badges.add($container).then(function () {
                            expect(qwery('#dfp-ad--spbadge1', $container[0]).length).toBe(1);
                            done();
                        });
                    });
                });
            });
        });
});
