# Nopol [![Build Status](https://travis-ci.org/SpoonLabs/nopol.svg?branch=master)](https://travis-ci.org/SpoonLabs/nopol) [![Coverage Status](https://coveralls.io/repos/github/SpoonLabs/nopol/badge.svg?branch=master)](https://coveralls.io/github/SpoonLabs/nopol?branch=master)

This is the repository of Nopol.

Nopol is an automatic software repair tool developed at Inria Lille.

This code is research code, released under the GPL licence.

If you use this code for academic research, please cite:
[Automatic Repair of Buggy If Conditions and Missing Preconditions with SMT](http://hal.inria.fr/hal-00977798/PDF/NOPOL-Automatic-Repair-of-Buggy-If-Conditions-and-Missing-Preconditions-with-SMT.pdf) (Favio DeMarco, Jifeng Xuan, Daniel Le Berre, Martin Monperrus), In Proceedings of the 6th International Workshop on Constraints in Software Testing, Verification, and Analysis (CSTVA 2014)
```Bibtex
@InProceedings{DeMarco2014,
  Title                    = {{Automatic Repair of Buggy If Conditions and Missing Preconditions with {SMT}}},
  Author                   = {Favio DeMarco and Jifeng Xuan and Daniel Le Berre and Martin Monperrus},
  Booktitle                = {Proceedings of the 6th International Workshop on Constraints in Software Testing, Verification, and Analysis (CSTVA 2014)},
  url                      = {http://hal.inria.fr/hal-00977798/PDF/NOPOL-Automatic-Repair-of-Buggy-If-Conditions-and-Missing-Preconditions-with-SMT.pdf},
  Year                     = {2014}
}
```
A compiled version of Nopol is at nopol-0.0.3-SNAPSHOT-jar-with-dependencies.jar
It requires an SMT solver installed on the machine (e.g. Z3)

## Getting started

1) First compile:

```
cd nopol

export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64

mvn package -DskipTests
```

2) Compile the test-projects

```
cd ../test-projects/
mvn compile
```

3) Execute Nopol (parameters explained below)

```
java -jar ../nopol/target/nopol-0.0.3-SNAPSHOT-jar-with-dependencies.jar \
-s src/main/java/ \
-c target/classes:target/test-classes:/home/martin/.m2/repository/junit/junit/4.11/junit-4.11.jar:/home/martin/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar\
-t symbolic_examples.symbolic_example_1.NopolExampleTest\
-p ../nopol/lib/z3/z3_for_linux

```

It should output somehting like:
```
----INFORMATION----
Nb classes : 34
Nb methods : 53
Nb statements: 5
Nb statement executed by the passing tests of the patched line: 0
Nb statement executed by the failing tests of the patched line: 0
Nb unit tests : 9
Nb Statements Analyzed : 3
Nb Statements with Angelic Value Found : 1
Nb inputs in SMT : 8
Nb SMT level: 2
Nb SMT components: [4] [== of arity: 2, != of arity: 2, < of arity: 2, <= of arity: 2]
                  class java.lang.Boolean: 4
Nb variables in SMT : 13
Nb run failing test  : [2, 1]
Nb run passing test : [4, 18]
NoPol Execution time : 3262ms
----PATCH FOUND----
symbolic_examples.symbolic_example_1.NopolExample:12: CONDITIONAL index < 1
```
Parameter `-c` can be found with `mvn dependency:build-classpath`.

## Minimal Usage

4 parameters are required
```
Usage: java -jar nopol.jar

  (-s|--source) source1:source2:...:sourceN 
        Define the path to the source code of the project. For instance `src/main/java`

  (-c|--classpath) <classpath>
        Define the classpath of the project separated by a path separator (`:` on Linux). 
        Must contain the application binary classes (`target/classes`)
        Must contain the application test classes (`target/test-classes`)
        Must contain the library classes (`lib/junit.jar` for instance)
        
  [(-t|--test) test1:test2:...:testN ]
        Define the tests of the project. For instance `symbolic_examples.symbolic_example_1.NopolExampleTest`

  [(-p|--solver-path) <solverPath>]
        Define the solver binary path (only used with smt synthesis). For instance `../nopol/lib/z3/z3_for_linux`

```


## Advanced Usage

```
Usage: java -jar nopol.jar

  [(-m|--mode) <repair|ranking>]
        Define the mode of execution. (default: repair)

  [(-e|--type) <loop|condition|precondition>]
        The type of statement to analyze (only used with repair mode). (default:
        condition)

  [(-o|--oracle) <angelic|symbolic>]
        Define the oracle (only used with repair mode). (default: angelic)

  [(-y|--synthesis) <smt|brutpol>]
        Define the patch synthesis. (default: smt)

  [(-l|--solver) <z3|cvc4>]
        Define the solver (only used with smt synthesis). (default: z3)

  [--complianceLevel <complianceLevel>]
        The Java version of the project. (default: 7)

  [--maxTime <maxTime>]
        The maximum time execution in minute. (default: 60)
```

## Contact

For questions and feedback , please contact martin.monperrus@univ-lille1.fr

