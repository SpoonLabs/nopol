package evo_examples.evo_example_1;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class EvoExampleGeneratedTest {

	//generated tests
	@Test
	public void test_evo_example_generated_0(){
		EvoExample example = new EvoExample();
		example.setValue(10);
		assertEquals(10,example.getValue());
		assertEquals(6,example.minZero(6));
	}

	//generated test anchoring the bug
	@Test
	public void test_evo_example_generated_1(){
		EvoExample example = new EvoExample();
		assertEquals(0,example.minZero(1));	
	}


}
