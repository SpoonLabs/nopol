package fr.inria.lille.commons.synthesis.operator;

public interface OperatorVisitor<T> {

    public T visitUnaryOperator(UnaryOperator<?, ?> operator);

    public T visitBinaryOperator(BinaryOperator<?, ?, ?> operator);

    public T visitTernaryOperator(TernaryOperator<?, ?, ?, ?> operator);

}
