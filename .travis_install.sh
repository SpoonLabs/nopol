#!/bin/bash

cd "test-projects"
# compile test projects
mvn clean package -DskipTests
cd ..
cd "nopol"
# compile Nopol
mvn clean package -DskipTests