#!/usr/bin/env bash

project=Chart
id=5
wd=${project}_${id}

cd nopol

git clone https://github.com/rjust/defects4j.git
cd defects4j
./init.sh
cd ../
defects4j/framework/bin/defects4j checkout -p ${project} -v ${id}b -w ${wd}
cd ${wd}
../defects4j/framework/bin/defects4j compile
../defects4j/framework/bin/defects4j test | tee > out.txt

fullQualifiedNameFailingTestCase=$(cat out.txt | grep - | cut -d ":" -f1 | cut -d "-" -f2)
echo ${fullQualifiedNameFailingTestCase}

cd ../
echo "java -cp ${JAVA_HOME}/lib/tools.jar:target/nopol-0.2-SNAPSHOT-jar-with-dependencies.jar fr.inria.lille.repair.Main -s ${wd}/source/ -c ${wd}/build/:${wd}/build-tests:${wd}/lib/:defects4j/framework/projects/lib/cobertura-lib/servlet-api-2.5-6.1.14.jar -p lib/z3/z3_for_linux -t ${fullQualifiedNameFailingTestCase} -z gzoltar | tee out.txt"
java -cp ${JAVA_HOME}/lib/tools.jar:target/nopol-0.2-SNAPSHOT-jar-with-dependencies.jar fr.inria.lille.repair.Main -s ${wd}/source/ -c ${wd}/build/:${wd}/build-tests:${wd}/lib/:defects4j/framework/projects/lib/cobertura-lib/servlet-api-2.5-6.1.14.jar -p lib/z3/z3_for_linux -t ${fullQualifiedNameFailingTestCase} -z gzoltar | tee out.txt
if [[ $? != 0 ]]
then
    exit 1
fi
# A patch has been found, compare to it the known patch.
cat out.txt | grep -A1 "PATCH FOUND" | grep -e "!(org.jfree.data.xy.XYSeries.this.allowDuplicateXValues)" -e "overwritten!=null"
if [[ $? != 0 ]]
then
    exit 1
fi
