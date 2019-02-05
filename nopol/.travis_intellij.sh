#!/bin/bash
# script for Travis

source /opt/jdk_switcher/jdk_switcher.sh
jdk_switcher use oraclejdk8


cd test-projects
mvn clean install -DskipTests
cd ..
cd nopol
mvn clean install -DskipTests
cd ..
cd nopol-server
mvn clean install  -DskipTests
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

