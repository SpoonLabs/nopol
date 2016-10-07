# Nopol [![Build Status](https://travis-ci.org/SpoonLabs/nopol.svg?branch=master)](https://travis-ci.org/SpoonLabs/nopol) [![Coverage Status](https://coveralls.io/repos/github/SpoonLabs/nopol/badge.svg?branch=master)](https://coveralls.io/github/SpoonLabs/nopol?branch=master)

Nopol is an automatic software repair tool for Java. This code is research code, released under the GPL licence.

If you use this code for academic research, please cite:
[Nopol: Automatic Repair of Conditional Statement Bugs in Java Programs](https://hal.archives-ouvertes.fr/hal-01285008/document) (Jifeng Xuan, Matias Martinez, Favio Demarco, Maxime Clément, Sebastian Lamelas, Thomas Durieux, Daniel Le Berre, Daniel Le Berre, Martin Monperrus). IEEE Transactions on Software Engineering, 2016.
```Bibtex
@article{xuan:hal-01285008,
 title = {Nopol: Automatic Repair of Conditional Statement Bugs in Java Programs},
 author = {Xuan, Jifeng and Martinez, Matias and Demarco, Favio and Clément, Maxime and Lamelas, Sebastian and Durieux, Thomas and Le Berre, Daniel and Monperrus, Martin},
 journal = {IEEE Transactions on Software Engineering},
 year = {2016},
}
```

You can alternatively cite the previous paper ["Automatic Repair of Buggy If Conditions and Missing Preconditions with SMT"](http://hal.inria.fr/hal-00977798/PDF/NOPOL-Automatic-Repair-of-Buggy-If-Conditions-and-Missing-Preconditions-with-SMT.pdf) (Favio DeMarco, Jifeng Xuan, Daniel Le Berre, Martin Monperrus), In Proceedings of the 6th International Workshop on Constraints in Software Testing, Verification, and Analysis (CSTVA 2014) [(Bibtex)](http://www.monperrus.net/martin/bibtexbrowser.php?key=DeMarco2014&bib=monperrus.bib)

The dynamic synthesis part of Nopol is described in [DynaMoth: Dynamic Code Synthesis for Automatic Program Repair](https://hal.archives-ouvertes.fr/hal-01279233/document) (Thomas Durieux, Martin Monperrus), In Proceedings of the 11th International Workshop in Automation of Software Test, 2016. [(Bibtex)](http://www.monperrus.net/martin/bibtexbrowser.php?key=durieux%3Ahal-01279233&bib=monperrus.bib)


## Getting started

Nopol requires Java and an SMT solver installed on the machine (e.g. Z3)

1) CoCoSpoon:

```
git clone https://github.com/danglotb/CoCoSpoon.git
cd CoCoSpoon
git checkout java7
mvn clean install
```

2) Compile NoPol:

```
cd ../nopol/nopol
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
mvn package -DskipTests
```

3) Compile the test-projects

```
cd ../test-projects/
# compiling app (in target/classes) and tests (in target/test-classes), but don't run the tests (they obviously fail, because the goal is to repair them)
mvn test -DskipTests 
```

4) Execute Nopol (parameters explained below)

```
cd ../test-projects/
java -jar nopol.jar \
-s src/main/java/ \
-c target/classes:target/test-classes:/home/martin/.m2/repository/junit/junit/4.11/junit-4.11.jar:/home/martin/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar \
-t symbolic_examples.symbolic_example_1.NopolExampleTest \
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
  [(-z|--flocal) < ochiai|dumb|gzoltar>]
        Define the fault localizer to be used. (default: ochiai)

```

## Contact

For questions and feedback , please contact martin.monperrus@univ-lille1.fr

