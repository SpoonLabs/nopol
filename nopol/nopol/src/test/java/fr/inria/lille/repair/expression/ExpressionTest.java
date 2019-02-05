package fr.inria.lille.repair.expression;

import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.expression.access.Array;
import fr.inria.lille.repair.expression.combination.binary.BinaryExpression;
import fr.inria.lille.repair.expression.combination.binary.BinaryOperator;
import fr.inria.lille.repair.expression.factory.AccessFactory;
import fr.inria.lille.repair.expression.factory.CombinationFactory;
import fr.inria.lille.repair.expression.factory.ValueFactory;
import fr.inria.lille.repair.expression.value.Value;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExpressionTest {

    @Test
    public void primitiveBinaryFactoryTest() {
        NopolContext nopolContext = new NopolContext();
        BinaryExpression binaryExpression = CombinationFactory.create(BinaryOperator.ADD, AccessFactory.variable("a", 3.1, nopolContext), AccessFactory.literal(1, nopolContext), nopolContext);
        Object realValue = binaryExpression.getValue().getRealValue();
        assertEquals("a + 1", binaryExpression.toString());
        assertEquals(4.1, realValue);
    }

    @Test
    public void arrayFactoryTest() {
        NopolContext nopolContext = new NopolContext();
        Value value = ValueFactory.create(new int[]{1, 2, 3});
        Array test = AccessFactory.array(AccessFactory.variable("test", value, nopolContext), AccessFactory.literal(0, nopolContext), nopolContext);
        assertEquals("test[0]", test.toString());
        assertEquals(1, test.getValue().getRealValue());
    }
}