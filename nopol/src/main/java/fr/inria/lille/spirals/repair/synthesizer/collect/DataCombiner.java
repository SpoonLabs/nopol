package fr.inria.lille.spirals.repair.synthesizer.collect;

import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.spirals.repair.expression.*;
import fr.inria.lille.spirals.repair.expression.operator.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Thomas Durieux on 12/03/15.
 */
public class DataCombiner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final int maxDepth = Config.INSTANCE.getSynthesisDepth();

    private final List<CombineListener> listeners = new ArrayList<>();
    private boolean stop = false;
    private long startTime;
    private long maxTime;
    private long executionTime;

    public Candidates combine(Candidates candidates, Object angelicValue, long maxTime) {
        this.maxTime = maxTime;
        this.startTime = System.currentTimeMillis();
        executionTime = System.currentTimeMillis() - startTime;
        logger.debug("[combine] start on " + candidates.size() + " elements");
        Candidates result = new Candidates();
        result.addAll(candidates);
        List<Expression> lastTurn = new ArrayList<>();
        lastTurn.addAll(candidates);
        int previousSize = 0;
        executionTime = System.currentTimeMillis() - startTime;
        for (int i = 0; i < maxDepth - 1 && !stop && executionTime <= maxTime; i++) {
            lastTurn.addAll(combinePrimitives(lastTurn, previousSize, i == maxDepth - 2 ? angelicValue : null));
            if(stop)  {
                return result;
            }
            lastTurn.addAll(combineComplex(lastTurn, previousSize, i == maxDepth - 2 ? angelicValue : null));
            previousSize = candidates.size();
            executionTime = System.currentTimeMillis() - startTime;
        }
        result.addAll(lastTurn);
        logger.debug("[combine] end " + result.getCacheValues().keySet().size() + " evaluated elements");
        return result;
    }

    private List<Expression> combinePrimitives(List<Expression> toCombine, int previousSize, Object value) {
        logger.debug("[combine] primitive start on " + toCombine.size() + " elements");
        List<Expression> result = new ArrayList<>();

        if (Config.INSTANCE.isSortExpressions()) {
            Collections.sort(toCombine, Collections.reverseOrder());
        }
        executionTime = System.currentTimeMillis() - startTime;
        for (int i = 0; i < toCombine.size() && executionTime <= maxTime; i++) {
            Expression expression = toCombine.get(i);
            if (expression.getType() == null || (!Number.class.isAssignableFrom(expression.getType()) && !Boolean.class.isAssignableFrom(expression.getType()))) {
                continue;
            }
            if (expression.getType() == null) {
                continue;
            }
            if(expression instanceof ComplexTypeExpression) {
                continue;
            }
            executionTime = System.currentTimeMillis() - startTime;
            for (int j = Math.max(i, previousSize); j < toCombine.size() && executionTime <= maxTime; j++) {
                if (i == j) {
                    continue;
                }
                Expression expression1 = toCombine.get(j);

                if (expression1.getType() == null || (!Number.class.isAssignableFrom(expression1.getType()) && !Boolean.class.isAssignableFrom(expression1.getType()))) {
                    continue;
                }
                if (expression instanceof Constant && expression1 instanceof Constant) {
                    continue;
                }
                if(expression1 instanceof ComplexTypeExpression) {
                    continue;
                }
                executionTime = System.currentTimeMillis() - startTime;
                for (int k = 0; k < Operator.values().length && executionTime <= maxTime; k++) {
                    Operator operator = Operator.values()[k];
                    if (value != null && operator.getReturnType() != value.getClass()) {
                        continue;
                    }
                    if (!operator.getParam1().isAssignableFrom(expression.getType()) || !operator.getParam2().isAssignableFrom(expression1.getType())) {
                        continue;
                    }
                    List returnValue = combineExpressionOperator(expression, expression1, operator, value, result);
                    if(returnValue != null) {
                        return returnValue;
                    }
                    executionTime = System.currentTimeMillis() - startTime;
                }
                executionTime = System.currentTimeMillis() - startTime;
            }
            executionTime = System.currentTimeMillis() - startTime;
        }
        return result;
    }

    private List<Expression> combineExpressionOperator(Expression expression, Expression expression1, Operator operator, Object value, List<Expression> result) {
        BinaryExpression binaryExpression = new PrimitiveBinaryExpressionImpl(operator, expression, expression1);
        if (addExpressionIn(binaryExpression, result, value != null)) {
            expression.getInExpressions().add(binaryExpression);
            if (!expression.sameExpression(expression1)) {
                expression1.getInExpressions().add(binaryExpression);
                if (callListerner(binaryExpression) && Config.INSTANCE.isOnlyOneSynthesisResult()) {
                    return result;
                }
            }
        }

        if (!operator.isCommutative()) {
            binaryExpression = new PrimitiveBinaryExpressionImpl(operator, expression1, expression);

            if (addExpressionIn(binaryExpression, result, value != null)) {
                expression.getInExpressions().add(binaryExpression);
                if (!expression.sameExpression(expression1)) {
                    expression1.getInExpressions().add(binaryExpression);
                    if (callListerner(binaryExpression) && Config.INSTANCE.isOnlyOneSynthesisResult()) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    private List<Expression> combineComplex(List<Expression> toCombine, int previousSize, Object value) {
        Expression nullExpression = new ComplexConstantImpl("null", null);
        logger.debug("[combine] complex start on " + toCombine.size() + " elements");
        List<Expression> result = new ArrayList<>();
        if (value != null && value.getClass() != Boolean.class) {
            return result;
        }
        if (Config.INSTANCE.isSortExpressions()) {
            Collections.sort(toCombine, Collections.reverseOrder());
        }
        executionTime = System.currentTimeMillis() - startTime;
        for (int i = 0; i < toCombine.size() && executionTime <= maxTime; i++) {
            Expression expression = toCombine.get(i);

            if (expression.getType() != null && (Number.class.isAssignableFrom(expression.getType()) || Boolean.class.isAssignableFrom(expression.getType()))) {
                continue;
            }
            if(expression instanceof PrimitiveTypeExpression) {
                continue;
            }

            BinaryExpression binaryExpression = new ComplexBinaryExpressionImpl(Operator.EQ, expression, nullExpression);
            if (addExpressionIn(binaryExpression, result, value != null)) {
                expression.getInExpressions().add(binaryExpression);
                if (!expression.sameExpression(nullExpression)) {
                    nullExpression.getInExpressions().add(binaryExpression);
                    if (callListerner(binaryExpression)) {
                        return result;
                    }
                }
            }

            binaryExpression = new ComplexBinaryExpressionImpl(Operator.NEQ, expression, nullExpression);
            if (addExpressionIn(binaryExpression, result, value != null)) {
                expression.getInExpressions().add(binaryExpression);
                if (!expression.sameExpression(nullExpression)) {
                    nullExpression.getInExpressions().add(binaryExpression);
                    if (callListerner(binaryExpression)) {
                        return result;
                    }
                }
            }
            executionTime = System.currentTimeMillis() - startTime;
        }
        return result;
    }

    private boolean addExpressionIn(Expression expression, List<Expression> results, boolean toAdd) {
        if (expression.getValue() == null) {
            return false;
        }
        logger.debug("[data] " + expression);
        return results.add(expression);
    }

    public void addCombineListener(CombineListener combineListener) {
        this.listeners.add(combineListener);
    }

    private boolean callListerner(Expression expression) {
        for (int i = 0; i < listeners.size(); i++) {
            CombineListener combineListener = listeners.get(i);
            if (combineListener.check(expression)) {
                stop = true;
                return stop;
            }
        }
        return stop;
    }

    public interface CombineListener {
        boolean check(Expression expression);
    }
}
