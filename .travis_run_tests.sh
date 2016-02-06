#!/bin/bash
# test script for Travis
cd nopol
mvn clean verify jacoco:report coveralls:report