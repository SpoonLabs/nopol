package fr.inria.lille.commons.synthesis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.operator.BinaryOperator;
import fr.inria.lille.commons.synthesis.operator.Operator;
import fr.inria.lille.commons.synthesis.operator.Parameter;

public class OperatorTest {

	@SuppressWarnings({ "unchecked", "rawtypes"})
	@Test
	public void arguments() {
		Expression aString = Expression.from("aString", "zxcy");
		Expression anInt = Expression.from("anInt", 1039);
		Expression aDouble = Expression.from("aDouble", 10.39);
		Expression aChar = Expression.from("aChar", "A".charAt(0));
		
		Parameter stringParameter = Parameter.aString();
		Parameter integerParameter = Parameter.anInteger();
		Parameter numberParameter = Parameter.aNumber();
		Parameter charParameter = Parameter.aCharacter();
		
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
		
		Operator lessOrEqualThan = BinaryOperator.lessOrEqualThan();
		assertEquals(2, lessOrEqualThan.arity());
		assertFalse(lessOrEqualThan.admitsAsArguments(Arrays.asList()));
		assertFalse(lessOrEqualThan.admitsAsArguments(Arrays.asList(anInt, aString, aString)));
		assertFalse(lessOrEqualThan.admitsAsArguments(Arrays.asList(aString, anInt)));
		assertTrue(lessOrEqualThan.admitsAsArguments(Arrays.asList(anInt, anInt)));
		assertTrue(lessOrEqualThan.admitsAsArguments(Arrays.asList(aDouble, aDouble)));
		assertTrue(lessOrEqualThan.admitsAsArguments(Arrays.asList(aDouble, anInt)));
	}
}
