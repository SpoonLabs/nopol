package fr.inria.lille.commons.synthesis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class OperatorTest {

	@SuppressWarnings({ "unchecked", "rawtypes"})
	@Test
	public void arguments() {
		Variable aString = Variable.from("aString", "zxcy");
		Variable anInt = Variable.from("anInt", 1039);
		Variable aDouble = Variable.from("aDouble", 10.39);
		Variable aChar = Variable.from("aChar", "A".charAt(0));
		
		Parameter stringParameter = new Parameter(String.class);
		Parameter integerParameter = new Parameter(Integer.class);
		Parameter numberParameter = new Parameter(Number.class);
		Parameter charParameter = new Parameter(Character.class);
		
		assertTrue(stringParameter.admitsAsArgument(aString));
		assertFalse(stringParameter.admitsAsArgument(anInt));
		assertFalse(stringParameter.admitsAsArgument(aDouble));
		assertFalse(stringParameter.admitsAsArgument(aChar));

		assertTrue(integerParameter.admitsAsArgument(anInt));
		assertFalse(integerParameter.admitsAsArgument(aDouble));
		assertFalse(integerParameter.admitsAsArgument(aString));
		assertFalse(integerParameter.admitsAsArgument(aChar));
		
		assertTrue(numberParameter.admitsAsArgument(anInt));
		assertTrue(numberParameter.admitsAsArgument(aDouble));
		assertFalse(numberParameter.admitsAsArgument(aString));
		assertFalse(numberParameter.admitsAsArgument(aChar));
		
		assertTrue(charParameter.admitsAsArgument(aChar));
		assertFalse(charParameter.admitsAsArgument(anInt));
		assertFalse(charParameter.admitsAsArgument(aDouble));
		assertFalse(charParameter.admitsAsArgument(aString));
		
		Operator booleanFromIntAndString = new Operator(Boolean.class, Arrays.asList(stringParameter, integerParameter));
		assertEquals(2, booleanFromIntAndString.numberOfParameters());
		assertFalse(booleanFromIntAndString.admitsAsArguments(Arrays.asList()));
		assertFalse(booleanFromIntAndString.admitsAsArguments(Arrays.asList(anInt, aString, aString)));
		assertFalse(booleanFromIntAndString.admitsAsArguments(Arrays.asList(anInt, aString)));
		assertTrue(booleanFromIntAndString.admitsAsArguments(Arrays.asList(aString, anInt)));
		assertFalse(booleanFromIntAndString.admitsAsArguments(Arrays.asList(aDouble, aString)));
		
		Operator doubleFromStringAndNumber = new Operator(Double.class, Arrays.asList(stringParameter, numberParameter));
		assertEquals(2, doubleFromStringAndNumber.numberOfParameters());
		assertFalse(doubleFromStringAndNumber.admitsAsArguments(Arrays.asList()));
		assertFalse(doubleFromStringAndNumber.admitsAsArguments(Arrays.asList(anInt, aString, aString)));
		assertTrue(doubleFromStringAndNumber.admitsAsArguments(Arrays.asList(aString, anInt)));
		assertTrue(doubleFromStringAndNumber.admitsAsArguments(Arrays.asList(aString, aDouble)));
	}
}
