package nopol_examples.nopol_example_12;
public class NopolExample {
    public boolean isEmpty(java.util.List list) {
        java.util.ArrayList list2 = new java.util.ArrayList();
        int x = 3; // 3 should also be added to the list of constants
        if (list.isEmpty())
            return true;
        foo(null);
        int y = x + list2.size(); // required for preventing the compiler to remive the variable (collection is done at ru  qntime)
        return false;
    }
    int foo(java.util.List l) { return 0; }
}