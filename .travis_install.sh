#!/bin/bash
# compile test projects
cd "test-projects"
mvn clean package -DskipTests

cd ..
git clone https://github.com/SpoonLabs/CoCoSpoon.git
cd CoCoSpoon
mvn clean install

cd ..
# compile Nopol
cd "nopol"
mvn clean package -DskipTests
