#!/usr/bin/env bash
# run on the TSE bugs

#   -e  Exit immediately if a command exits with a non-zero status.
set -e 

cd nopol
mvn -q versions:set -DnewVersion=TRAVIS
# creating target/nopol-TRAVIS-jar-with-dependencies.jar
mvn -q clean package -DskipTests
cd ..

# the directory is cached for performance
git clone https://github.com/SpoonLabs/nopol-experiments || true
cd nopol-experiments
git pull

# setting up the correct call
echo "#!/bin/bash" > call_nopol.sh
echo "set -e" >> call_nopol.sh
echo "# does nothing, only compile" >> call_nopol.sh
# echo "if [[ -z \$4 ]]; then test=""; else test=\"-t \$4\";fi" >> call_nopol.sh
# echo "java -jar ../nopol/target/nopol-TRAVIS-jar-with-dependencies.jar -s \$1 -c \$2 -p \$3 \$test" >> call_nopol.sh
chmod 755 call_nopol.sh

# only compiling

# commons math
python src/reproduce.py -bug cm1  || true
python src/reproduce.py -bug cm2  || true
python src/reproduce.py -bug cm3  || true
python src/reproduce.py -bug cm4  || true
python src/reproduce.py -bug cm5  || true
python src/reproduce.py -bug cm6  || true
python src/reproduce.py -bug cm7  || true
python src/reproduce.py -bug cm10  || true
python src/reproduce.py -bug pm1  || true
python src/reproduce.py -bug pm2  || true

# Commons lang
python src/reproduce.py -bug cl1  || true
python src/reproduce.py -bug cl2  || true
python src/reproduce.py -bug cl3  || true
python src/reproduce.py -bug cl4  || true
python src/reproduce.py -bug cl5  || true
python src/reproduce.py -bug cl6  || true
python src/reproduce.py -bug pl1  || true
python src/reproduce.py -bug pl2  || true
python src/reproduce.py -bug pl3  || true
python src/reproduce.py -bug pl4  || true

cd ../nopol && env NOPOL_EVAL_TSE=1 mvn -q test -Dtest="fr.inria.lille.repair.nopol.TseEvaluationTest" && cd ..

#python src/reproduce.py -bug all
