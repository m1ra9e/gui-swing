[![Apache 2.0](https://img.shields.io/github/license/m1ra9e/gui-swing.svg)](http://www.apache.org/licenses/LICENSE-2.0)

# Gui-swing

GUI application for working with the vehicle database and the ability to import and export data to files.

* Supported databases: SQLite, PostgreSQL.

* Supported import and export formats: xml, yaml, jsom, csv, bser, ser.

## Build

Build requires Java (JDK) 17+ and Apache Maven 3.8+.

```sh
git clone https://github.com/m1ra9e/gui-swing.git gui-swing
cd gui-swing
mvn clean package
```

## Run

For run the application, execute [run.bat](tools/run.bat) on Windows or [run.sh](tools/run.sh) on Linux.

## Test

For test with local PostgreSQL fill [pg_test_settings.properties](src/test/resources/home/db/pg_test_settings.properties) by own data.

For run tests, execute [test.bat](tools/test.bat) on Windows or [test.sh](tools/test.sh) on Linux.

## Changelog

[Changelog information](CHANGELOG.md)


### Short version description

| version | description |
| ------- | ----------- |
| 6.0.0 | added PostgreSQL, removed choose data store on start, date and sorting in properties-file, added tools-scripts, refactoring, added license |
| 5.0.0 | java_17, import and export |
| 4.0.0 | improve and refactoring |
| 3.0.0 | save data after push button |
| 2.0.0 | db chooser |
| 1.0.0 | work with one internal db |

## Plans

- fix bugs
- add new functions
