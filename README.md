# ch-inofix-newsletter
A newsletter plugin for Liferay.

## How To Build
1. Install blade: `curl https://raw.githubusercontent.com/liferay/liferay-blade-cli/master/installers/local | sh`
1. Create a liferay workspace: `blade init [WORKSPACE_NAME]`
1. Setup tomcat-bundle for testing: `gradle initBundle`
1. Checkout newsletter sources to [WORKSPACE_NAME]: `cd [WORKSPACE_NAME]/modules; git clone https://github.com/inofix/ch-inofix-newsletter.git`
1. Run ServiceBuilder: `gradle buildService`
1. Build and test integration: `gradle build`
1. Check local test-results: `firefox ch-inofix-newsletter/newsletter-test/build/reports/tests/testIntegration/index.html`

## Testing
* Travis-results for ch-inofix-newsletter can be found at https://travis-ci.org/inofix/ch-inofix-newsletter/builds
