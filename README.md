# ch-inofix-newsletter
A newsletter plugin for Liferay.

## How To Build
1. Install blade: `curl https://raw.githubusercontent.com/liferay/liferay-blade-cli/master/installers/local | sh`
1. Create a liferay workspace: `blade init [WORKSPACE_NAME]`
1. Checkout newsletter sources to [WORKSPACE_NAME]: `cd [WORKSPACE_NAME]/modules; git clone https://github.com/inofix/ch-inofix-newsletter.git`
1. Setup Tomcat for testing: `gradle initBundle`
1. Run ServiceBuilder: `gradle buildService`
1. Build and test integration: `gradle build`

## Testing
* Testresults can be found at https://travis-ci.org/inofix/ch-inofix-newsletter/builds
