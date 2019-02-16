#!/bin/bash
# compile test projects
cd "test-projects"
mvn clean package -DskipTests

cd ..
git clone -b compat-java7 https://github.com/SpoonLabs/CoCoSpoon.git
cd CoCoSpoon
mvn clean install -DskipTests

cd ..
# compile Nopol
cd "nopol"
mvn clean package -DskipTests
