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
cd "gzoltar-wrapper"
mvn clean install
cd ..
cd "test-projects"
# compile test projects
mvn clean package -DskipTests
cd ..
cd "nopol"
# compile Nopol
mvn clean package -DskipTests