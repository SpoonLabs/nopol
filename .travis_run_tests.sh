#!/bin/bash
# test script for Travis

echo "Running test suite... "
cd nopol
mvn clean install jacoco:report coveralls:report
if [[ $? != 0 ]]
then
    exit 1
fi

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
else
    echo "Running nopol server"
    cd nopol-server
    mvn clean install
    if [[ $? != 0 ]]
    then
        exit 1
    fi

    cd ..

    echo "Running nopol-ui-intellij"
    cd nopol-ui-intellij

    ./gradlew buildPlugin
    if [[ $? != 0 ]]
    then
        exit 1
    fi
fi