package fr.inria.lille.spirals.repair.expression;

import fr.inria.lille.spirals.repair.expression.access.Array;
import fr.inria.lille.spirals.repair.expression.combination.binary.BinaryExpression;
import fr.inria.lille.spirals.repair.expression.combination.binary.BinaryOperator;
import fr.inria.lille.spirals.repair.expression.factory.AccessFactory;
import fr.inria.lille.spirals.repair.expression.factory.CombinationFactory;
import fr.inria.lille.spirals.repair.expression.factory.ValueFactory;
import fr.inria.lille.spirals.repair.expression.value.Value;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExpressionTest {
    @Test
    public void primitiveBinaryFactoryTest() {
        BinaryExpression binaryExpression = CombinationFactory.create(BinaryOperator.ADD, AccessFactory.variable("a", 3.1), AccessFactory.literal(1));
        Object realValue = binaryExpression.getValue().getRealValue();
        assertEquals("a + 1", binaryExpression.toString());
        assertEquals(4.1, realValue);
    }

    @Test
    public void arrayFactoryTest() {
        Value value = ValueFactory.create(new int[]{1, 2, 3});
        Array test = AccessFactory.array(AccessFactory.variable("test", value), AccessFactory.literal(0));
        assertEquals("test[0]", test.toString());
        assertEquals(1, test.getValue().getRealValue());
    }
}