/*jshint -W024 */

/*
    Swallows (and reports) exceptions. Designed to wrap around modules at the "bootstrap" level.
    For example "comments throwing an exception should not stop auto refresh"
 */
import raven from 'raven';

function Robust(name, block, reporter) {

    if (!reporter) {
        reporter = raven.captureException;
    }

    try {
        block();
    } catch (e) {
        reporter(e, {
            tags: {
                module: name
            }
        });
    }
}

export default Robust;
