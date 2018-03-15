#!/usr/bin/env bash
# executing all tests in Defects4jEvaluationTest
cd nopol
mvn -q versions:set -DnewVersion=TRAVIS
# creating target/nopol-TRAVIS-jar-with-dependencies.jar
mvn -q clean package -DskipTests

env NOPOL_EVAL_DEFECTS4J=1 mvn -q test -Dtest="Defects4jEvaluationMathTest"



