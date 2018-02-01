#!/bin/bash
# script for Travis

echo "Compiling & testing Nopol"
cd nopol
mvn clean install jacoco:report coveralls:report
if [[ $? != 0 ]]
then
    exit 1
fi
cd ..

echo ${JAVA_HOME}

echo "Compiling & testing nopol server"
cd nopol-server
mvn clean install
if [[ $? != 0 ]]
then
    exit 1
fi
cd ..

# IDE plugin
echo "Compiling & testing nopol-ui-intellij"
cd nopol-ui-intellij
./gradlew buildPlugin
if [[ $? != 0 ]]
then
    exit 1
fi
cd ..

## executing one defects4J example on Jdk7
if [[ ${JAVA_HOME} == *"7"* ]]
then
    echo "Compiling & testing defects4j example... "
    chmod +x ./.travis_run_defects4j.sh
    ./.travis_run_defects4j.sh
    if [[ $? != 0 ]]
    then
        exit 1
    fi
fi
