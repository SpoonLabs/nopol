#!/bin/bash
# compile test projects
cd "test-projects"
mvn clean package -DskipTests

cd ..
git clone https://github.com/SpoonLabs/CoCoSpoon.git
cd CoCoSpoon
git checkout -b 975b9b07e04fc8a689f4b4ca2746ccb49b4c380e 975b9b07e04fc8a689f4b4ca2746ccb49b4c380e
mvn clean install

cd ..
# compile Nopol
cd "nopol"
mvn clean package -DskipTests
