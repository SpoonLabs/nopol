#!/bin/bash
# script for Travis

# refer to:
# https://github.com/michaelklishin/jdk_switcher
# https://docs.travis-ci.com/user/languages/java/#stq=&stp=0
echo "Swicth to jdk 1.7"
jdk_switcher use openjdk7

echo "Compiling & testing Nopol"
cd test-projects
mvn clean package -DskipTests
cd ..
cd nopol
mvn clean test
if [[ $? != 0 ]]
then
    exit 1
fi
cd ..

# automatic deployment to tdurieux's repo if pushed on branch "release"
if [[ $TRAVIS_BRANCH == "release" ]]; then
	cd nopol;
        mvn versions:set -DnewVersion=`git rev-parse HEAD`
	mvn deploy --settings ../.travis-settings.xml -DskipTests=true -B;
fi
