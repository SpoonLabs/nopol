#!/usr/bin/env bash

./gradlew buildPlugin

cd build/distributions
unzip "nopol-ui-intellij.zip"
cd nopol-ui-intellij/lib
count=$(ls | wc -l)

[[ "$count" == 9 ]]