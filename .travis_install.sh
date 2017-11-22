#!/bin/bash
# compile test projects
cd "test-projects"
mvn clean package -DskipTests

cd ..
# compile Nopol
cd "nopol"
mvn clean package -DskipTests
