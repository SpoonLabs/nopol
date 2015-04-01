package fr.inria.lille.spirals.repair.synthesizer.collect;

import fr.inria.lille.spirals.repair.commons.Candidates;
import fr.inria.lille.repair.common.config.Config;
import fr.inria.lille.spirals.repair.expression.*;
import fr.inria.lille.spirals.repair.expression.operator.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Thomas Durieux on 12/03/15.
 */
public class DataCombiner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final int maxDepth = Config.INSTANCE.getSynthesisDepth();

    private final List<CombineListener> listeners = new ArrayList<>();
    private boolean stop = false;

    public Candidates combine(Candidates candidates, Object value) {
        logger.debug("[combine] start on " + candidates.size() + " elements");
        Candidates result = new Candidates();
        result.addAll(candidates);
        List<Expression> lastTurn = new ArrayList<>();
        lastTurn.addAll(candidates);
        int previousSize = 0;
        for (int i = 0; i < maxDepth - 1 && !stop; i++) {
            lastTurn.addAll(combinePrimitives(lastTurn, previousSize, i == maxDepth - 2 ? value : null));
            lastTurn.addAll(combineComplex(lastTurn, previousSize, i == maxDepth - 2 ? value : null));
            previousSize = candidates.size();
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
        for (int i = 0; i < toCombine.size(); i++) {
            Expression expression = toCombine.get(i);
            if (expression.getType() == null || (!Number.class.isAssignableFrom(expression.getType()) && !Boolean.class.isAssignableFrom(expression.getType()))) {
                continue;
            }
            if (expression.getType() == null) {
                continue;
            }
            for (int j = Math.max(i, previousSize); j < toCombine.size(); j++) {
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
                for (int k = 0; k < Operator.values().length; k++) {
                    Operator operator = Operator.values()[k];
                    if (value != null && operator.getReturnType() != value.getClass()) {
                        continue;
                    }
                    if (!operator.getParam1().isAssignableFrom(expression.getType()) || !operator.getParam2().isAssignableFrom(expression1.getType())) {
                        continue;
                    }
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
                }

            }
        }
        return result;
    }

    private List<Expression> combineComplex(List<Expression> toCombine, int previousSize, Object value) {
        logger.debug("[combine] complex start on " + toCombine.size() + " elements");
        List<Expression> result = new ArrayList<>();
        if (value != null && value.getClass() != Boolean.class) {
            return result;
        }
        if (Config.INSTANCE.isSortExpressions()) {
            Collections.sort(toCombine, Collections.reverseOrder());
        }
        for (int i = 0; i < toCombine.size(); i++) {
            Expression expression = toCombine.get(i);

            if (expression.getType() != null && (Number.class.isAssignableFrom(expression.getType()) || Boolean.class.isAssignableFrom(expression.getType()))) {
                continue;
            }
            for (int j = Math.max(i, previousSize); j < toCombine.size(); j++) {
                Expression expression1 = toCombine.get(j);
                if (expression1.getType() != null && (Number.class.isAssignableFrom(expression1.getType()) || Boolean.class.isAssignableFrom(expression1.getType()))) {
                    continue;
                }
                BinaryExpression binaryExpression = new ComplexBinaryExpressionImpl(Operator.EQ, expression, expression1);
                if (addExpressionIn(binaryExpression, result, value != null)) {
                    expression.getInExpressions().add(binaryExpression);
                    if (!expression.sameExpression(expression1)) {
                        expression1.getInExpressions().add(binaryExpression);
                        if (callListerner(binaryExpression)) {
                            return result;
                        }
                    }
                }

                binaryExpression = new ComplexBinaryExpressionImpl(Operator.NEQ, expression, expression1);
                if (addExpressionIn(binaryExpression, result, value != null)) {
                    expression.getInExpressions().add(binaryExpression);
                    if (!expression.sameExpression(expression1)) {
                        expression1.getInExpressions().add(binaryExpression);
                        if (callListerner(binaryExpression)) {
                            return result;
                        }
                    }
                }

            }
        }
        return result;
    }

    private boolean addExpressionIn(Expression expression, List<Expression> results, boolean toAdd) {
        if (expression.getValue() == null) {
            return false;
        }
        logger.debug("[data] " + expression + "=" + expression.getValue());
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
