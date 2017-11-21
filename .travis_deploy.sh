#!/bin/bash

if [ $TRAVIS_BRANCH == "master" ] && [ $TRAVIS_JDK_VERSION == "openjdk8" ]; then
	cd nopol;
	mvn deploy --settings ../.travis-settings.xml -DskipTests=true -B;
fi