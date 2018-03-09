#!/bin/bash
# script for Travis

source /opt/jdk_switcher/jdk_switcher.sh
jdk_switcher use oraclejdk8

echo "Compiling & testing Nopol"
cd test-projects
mvn clean install -DskipTests
cd ..
cd nopol
mvn clean install -DskipTests
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

