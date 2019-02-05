package nopol_examples.nopol_example_12;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NopolExampleTest {


    @Test
	public void test_1() {
		assertTrue(new NopolExample().isEmpty(Collections.EMPTY_LIST));
	}

    @Test
    public void test_2() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        assertFalse(new NopolExample().isEmpty(list));
    }

    @Test
    public void test_3() {
        assertTrue(new NopolExample().isEmpty(null));
    }

    @Test
    public void test_4() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        assertFalse(new NopolExample().isEmpty(list));
    }
}
