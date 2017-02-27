#!/usr/bin/env bash

git clone https://github.com/danglotb/CoCoSpoon.git
cd CoCoSpoon
mvn clean install
cd ..

git clone http://github.com/SpoonLabs/nopol.git
cd nopol/nopol
mvn install package -DskipTests
cd ../nopol-server/
mvn install package -DskipTests

cd ../..
mkdir lib
mv nopol/nopol/target/nopol-0.2-SNAPSHOT.jar lib/nopol-0.2-SNAPSHOT.jar
mv nopol/nopol-server/target/nopol-server-0.2-SNAPSHOT.jar lib/nopol-server-0.2-SNAPSHOT.jar