
To run the tests:

* install a JDK7, and ensure that maven uses it
* check that `z3` works well on the command line (`$ lib/z3/z3_for_linux``) (see TestUtility.java:    private String solverPath =  "lib/z3/z3_for_linux";)`
* first run `mvn test -DskipTests` in `../test-projects/`. This creates `../test-projects/target/test-classes` (but doesn(t run the tests since the examples are buggy)
* then `mvn test`