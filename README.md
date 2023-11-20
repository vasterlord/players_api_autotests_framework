<h1 align="center">Players API testing framework</h1>

---

## 📝 Table of Contents

- [About](#about)
- [Getting Started](#getting_started)
    - [Required tools](#required_tools)
- [Tests running](#tests_running)

## 🧐 About <a name="about"></a>

The Players API testing framework is designed to cover Players API with automation tests:

## 🏁 Getting Started <a name="getting_started"></a>

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
These instructions contain information on how to fully set up the environment in order to contribute and run tests.

### Required tools <a name="tools_plugins"></a>

**Tools to be installed:**

- Java Development Kit 11 (JDK11)
- Maven 3.x should be present in the environment
- Allure Commandline (***https://www.npmjs.com/package/allure-commandline***)

Verify your installations with the following commands
```
java --version
mvn -v
allure --version
```

## 🔧 Tests running <a name="tests_running"></a>

Tests run command example:
```
mvn clean test
```
In addition, for running tests is possible to customize specific parameters:
* base.api.url. Default value - http://3.68.165.45/
* threads.count. Default value - 3
* requests.api.log.level. Valid values: ALL,HEADERS, COOKIES, BODY, STATUS, PARAMS, METHOD, URI. Default value - ALL.
```
mvn clean test -Dbase.api.url=<base.api.url> -Dthreads.count=<threads.count> -Drequests.api.log.level=<requests.api.log.level>
```

Tests which cover defects are marked with Allure annotations *@Tag("Defect - description")*