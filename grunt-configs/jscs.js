module.exports = function(grunt, options) {
    return {
        options: {
            config: 'resources/jscs_conf.json'
        },
        common: {
            files: [{
                expand: true,
                cwd: 'common/app/assets/javascripts/',
                src: [
                    '**/*.js',
                    '!components/**',
                    '!bower_components/**',
                    '!bootstraps/app.js',
                    '!bootstraps/article.js',
                    '!bootstraps/commercial.js',
                    '!bootstraps/common.js',
                    '!bootstraps/dev.js',
                    '!bootstraps/football.js',
                    '!bootstraps/gallery.js',
                    '!bootstraps/identity.js',
                    '!bootstraps/liveblog.js',
                    '!bootstraps/media.js',
                    '!bootstraps/ophan.js',
                    '!bootstraps/profile.js',
                    '!bootstraps/section.js',
                    '!bootstraps/sport.js',
                    '!bootstraps/tag.js',
                    '!bootstraps/video-player.js',
                    '!modules/analytics/beacon.js',
                    '!modules/analytics/clickstream.js',
                    '!modules/analytics/discussion.js',
                    '!modules/analytics/foresee-survey.js',
                    '!modules/analytics/livestats.js',
                    '!modules/analytics/mvt-cookie.js',
                    '!modules/analytics/omniture.js',
                    '!modules/analytics/omnitureMedia.js',
                    '!modules/analytics/register.js',
                    '!modules/analytics/scrollDepth.js',
                    '!modules/article/spacefinder.js',
                    '!modules/article/truncate.js',
                    '!modules/article/twitter.js',
                    '!modules/asyncCallMerger.js',
                    '!modules/charts/doughnut.js',
                    '!modules/charts/table-doughnut.js',
                    '!modules/commercial/article-aside-adverts.js',
                    '!modules/commercial/article-body-adverts.js',
                    '!modules/commercial/front-commercial-components.js',
                    '!modules/commercial/keywords.js',
                    '!modules/commercial/liveblog-adverts.js',
                    '!modules/commercial/loader.js',
                    '!modules/commercial/outbrain.js',
                    '!modules/commercial/slice-adverts.js',
                    '!modules/commercial/tags/amaa.js',
                    '!modules/commercial/tags/audience-science-gateway.js',
                    '!modules/commercial/tags/audience-science.js',
                    '!modules/commercial/tags/remarketing.js',
                    '!modules/component.js',
                    '!modules/discussion/activity-stream.js',
                    '!modules/discussion/api.js',
                    '!modules/discussion/comment-box.js',
                    '!modules/discussion/comment-count.js',
                    '!modules/discussion/comments.js',
                    '!modules/discussion/loader.js',
                    '!modules/discussion/recommend-comments.js',
                    '!modules/discussion/top-comments.js',
                    '!modules/discussion/user-avatars.js',
                    '!modules/experiments/ab.js',
                    '!modules/experiments/affix.js',
                    '!modules/experiments/content-websocket.js',
                    '!modules/gallery/lightbox.js',
                    '!modules/identity/account-profile.js',
                    '!modules/identity/api.js',
                    '!modules/identity/autosignin.js',
                    '!modules/identity/facebook-authorizer.js',
                    '!modules/identity/forms.js',
                    '!modules/identity/formstack-iframe-embed.js',
                    '!modules/identity/formstack-iframe.js',
                    '!modules/identity/formstack.js',
                    '!modules/identity/password-strength.js',
                    '!modules/identity/public-profile.js',
                    '!modules/identity/validation-email.js',
                    '!modules/lazyload.js',
                    '!modules/live/filter.js',
                    '!modules/live/notification-bar.js',
                    '!modules/navigation/navigation.js',
                    '!modules/navigation/profile.js',
                    '!modules/navigation/search.js',
                    '!modules/onward/breaking-news.js',
                    '!modules/onward/geo-most-popular.js',
                    '!modules/onward/history.js',
                    '!modules/onward/more-tags.js',
                    '!modules/onward/most-popular-factory.js',
                    '!modules/onward/popular-fronts.js',
                    '!modules/onward/popular.js',
                    '!modules/onward/related.js',
                    '!modules/onward/socially-referred-burners.js',
                    '!modules/onward/tonal.js',
                    '!modules/open/cta.js',
                    '!modules/router.js',
                    '!modules/sport/football/football.js',
                    '!modules/sport/football/match-info.js',
                    '!modules/sport/football/match-list-live.js',
                    '!modules/sport/football/score-board.js',
                    '!modules/ui/autoupdate.js',
                    '!modules/ui/dropdowns.js',
                    '!modules/ui/expandable.js',
                    '!modules/ui/faux-block-link.js',
                    '!modules/ui/fonts.js',
                    '!modules/ui/images.js',
                    '!modules/ui/message.js',
                    '!modules/ui/notification-counter.js',
                    '!modules/ui/overlay.js',
                    '!modules/ui/relativedates.js',
                    '!modules/ui/rhc.js'
                ]
            }]
        },
        facia: {
            files: [{
                expand: true,
                cwd: 'facia/app/assets/javascripts/',
                src: [
                    '**/*.js',
                    '!modules/ui/container-show-more.js',
                    '!modules/ui/container-toggle.js'
                ]
            }]
        },
        faciaTool: {
            files: [{
                expand: true,
                cwd: 'facia-tool/public/javascripts/',
                src: [
                    '**/*.js',
                    '!components/**',
                    '!**/*.js',
                ]
            }]
        },
        membership: {
            files: [{
                expand: true,
                cwd: 'identity/app/assets/javascripts/',
                src: [
                    '**/*.js',
                    '!membership/payment-form.js',
                    '!membership/stripe-error-messages.js'
                ]
            }]
        }
    };
};
