#!/usr/bin/env bash
# executing all tests of Commons Math in Defects4J

# fails if any command fails
set -e

cd nopol

NOPOL_EVAL_DEFECTS4J=1 mvn test -Dtest="fr.inria.lille.repair.nopol.Defects4jEvaluationMathTest#test_Math42"

NOPOL_EVAL_DEFECTS4J=1 mvn test -Dtest="fr.inria.lille.repair.nopol.Defects4jEvaluationMathTest#test_Math49"

NOPOL_EVAL_DEFECTS4J=1 mvn test -Dtest="fr.inria.lille.repair.nopol.Defects4jEvaluationMathTest#test_Math69"

# NOPOL_EVAL_DEFECTS4J=1 mvn test -Dtest="fr.inria.lille.repair.nopol.Defects4jEvaluationMathTest#test_Math32"

NOPOL_EVAL_DEFECTS4J=1 mvn test -Dtest="fr.inria.lille.repair.nopol.Defects4jEvaluationMathTest#test_Math33"

