#!/bin/bash
# script for Travis

echo "Compiling & testing Nopol"
cd test-projects
mvn clean package -DskipTests
cd ..
cd nopol
mvn clean install jacoco:report coveralls:report
if [[ $? != 0 ]]
then
    exit 1
fi
cd ..

# automatic deployment to tdurieux's repo
if [ $TRAVIS_BRANCH == "master" ] && [ $TRAVIS_JDK_VERSION == "openjdk8" ]; then
	cd nopol;
        mvn versions:set -DnewVersion=`git rev-parse HEAD`
	mvn deploy --settings ../.travis-settings.xml -DskipTests=true -B;
fi