#!/bin/bash
# test script for Travis

cd nopol-server
mvn clean package
cd ..

cd nopol

cd ..

echo ${JAVA_HOME}

if [[ ${JAVA_HOME} == *"7"* ]]
then
    echo "Running defects4j example... "
    chmod +x ./.travis_run_defects4j.sh
    ./.travis_run_defects4j.sh
    if [[ $? != 0 ]]
    then
        exit 1
    fi
    echo "Running test suite... "
    cd nopol
    mvn clean verify jacoco:report coveralls:report
    if [[ $? != 0 ]]
    then
        exit 1
    fi
else
    echo "Running test suite... "
    cd nopol
    mvn clean verify jacoco:report coveralls:report
    if [[ $? != 0 ]]
    then
        exit 1
    fi
fi
