#!/bin/bash
if [ ! -d "z3" ]
then
	git clone https://github.com/Z3Prover/z3.git
	cd z3
	python scripts/mk_make.py --java
	cd build; make
	cd ../..
fi
cp z3/build/* nopol/lib/z3
mv nopol/lib/z3/z3 nopol/lib/z3/z3_for_linux

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
