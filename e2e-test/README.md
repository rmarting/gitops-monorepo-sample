# End to End Regression Tests

This folder includes a set of Newman collections to test and verify the application
deployed in each environment. 

Newman is a command-line Collection Runner for [Postman](https://www.postman.com/).
It enables you to run and test a Postman Collection directly from the command line.

The collection is integrated in the CICD pipeline to verify the right behavior of the
application after each deployment.

To run the collections:

```shell
newman run collection.json -e <ENV>/env.json
```

For example to run locally the tests:

```shell
newman run collection.json -e local/env.json
```

The environment variables are defined to setup the tests for each environment, using
the right endpoints of the application.

References:

* [Running collections on the command line with Newman](https://learning.postman.com/docs/running-collections/using-newman-cli/command-line-integration-with-newman/)
