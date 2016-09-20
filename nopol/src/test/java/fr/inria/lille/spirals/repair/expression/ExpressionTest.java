package fr.inria.lille.spirals.repair.expression;

import fr.inria.lille.repair.common.config.Config;
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
        Config config = new Config();
        BinaryExpression binaryExpression = CombinationFactory.create(BinaryOperator.ADD, AccessFactory.variable("a", 3.1, config), AccessFactory.literal(1, config), config);
        Object realValue = binaryExpression.getValue().getRealValue();
        assertEquals("a + 1", binaryExpression.toString());
        assertEquals(4.1, realValue);
    }

    @Test
    public void arrayFactoryTest() {
        Config config = new Config();
        Value value = ValueFactory.create(new int[]{1, 2, 3});
        Array test = AccessFactory.array(AccessFactory.variable("test", value, config), AccessFactory.literal(0, config), config);
        assertEquals("test[0]", test.toString());
        assertEquals(1, test.getValue().getRealValue());
    }
}