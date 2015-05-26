#!/bin/bash

cd "nopol"
java -cp target/nopol-0.0.3-SNAPSHOT-jar-with-dependencies.jar:target/test-classes/:misc/nopol-example/junit-4.11.jar org.junit.runner.JUnitCore fr.inria.lille.TravisTestSuite