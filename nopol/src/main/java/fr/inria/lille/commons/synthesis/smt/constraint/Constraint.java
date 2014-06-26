package fr.inria.lille.commons.synthesis.smt.constraint;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.and;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.boolSort;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.booleanTrue;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.intSort;
import static fr.inria.lille.commons.synthesis.smt.SMTLib.or;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISort;

import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;

public abstract class Constraint {

	public abstract List<LocationVariable<?>> usedLocationVariables(LocationVariableContainer locationVariableContainer);
	
	protected abstract List<IExpr> invocationArguments(LocationVariableContainer locationVariableContainer);
	
	protected abstract List<IDeclaration> parameters(LocationVariableContainer locationVariableContainer);
	
	protected abstract Collection<IExpr> definitionExpressions(LocationVariableContainer locationVariableContainer);
	
	public Constraint(String name, SMTLib smtlib) {
		this.smtlib = smtlib;
		nameSymbol = smtlib().symbolFor(name);
	}
	
	public ISort outputType() {
		return boolSort();
	}
	
	public IExpr invocation(LocationVariableContainer locationVariableContainer) {
		return invocationWith(invocationArguments(locationVariableContainer));
	}
	
	public IExpr invocationWithValues(LocationVariableContainer locationVariableContainer, Map<String, Object> values) {
		return invocationWith(instantiatedArguments(locationVariableContainer, values));
	}
	
	public IExpr invocationWith(List<IExpr> arguments) {
		if (arguments.isEmpty()) {
			return name();
		}
		return smtlib().expression(name(), arguments);
	}
	
	public ICommand definition(LocationVariableContainer locationVariableContainer) {
		List<IDeclaration> parameters = parameters(locationVariableContainer);
		Collection<IExpr> expressions = definitionExpressions(locationVariableContainer);
		return definition(parameters, expressions);
	}
	
	public ICommand definition(List<IDeclaration> parameters, Collection<IExpr> expressions) {
		IExpr finalExpression = finalExpression(expressions);
		return smtlib().functionDefinition(name(), parameters, outputType(), finalExpression);
	}
	
	protected List<IExpr> instantiatedArguments(LocationVariableContainer locationVariableContainer, Map<String, Object> values) {
		throw new UnsupportedOperationException("Constraint.invocationWithValues should be overridden by subclasses");		
	}
	
	public boolean isCompound() {
		return false;
	}
	
	protected IExpr finalExpression(Collection<IExpr> definitionExpressions) {
		return conjunctionOf(definitionExpressions);
	}
	
	protected List<IDeclaration> declarationsFromExpressions(Collection<LocationVariable<?>> locationVariables) {
		List<IDeclaration> declarations = ListLibrary.newLinkedList();
		for (LocationVariable<?> locationVariable : locationVariables) {
			declarations.add(declarationFromExpression(locationVariable));
		}
		return declarations;
	}

	protected IDeclaration declarationFromExpression(LocationVariable<?> locationVariable) {
		return smtlib().declaration(locationVariable.expression(), intSort());
	}
	
	protected List<IDeclaration> declarationsFromSubexpressions(Collection<LocationVariable<?>> locationVariables) {
		List<IDeclaration> declarations = ListLibrary.newLinkedList();
		for (LocationVariable<?> locationVariable : locationVariables) {
			declarations.add(declarationFromSubexpression(locationVariable));
		}
		return declarations;
	}
	
	protected IDeclaration declarationFromSubexpression(LocationVariable<?> locationVariable) {
		return smtlib().declaration(locationVariable.subexpression(), locationVariable.smtSort());
	}
	 
	protected List<ISymbol> collectExpressions(Collection<LocationVariable<?>> locationVariables) {
		List<String> expressions = Expression.expressionsOf(locationVariables);
		return asSymbols(expressions);
	}
	 
	protected List<ISymbol> collectSubexpressions(Collection<LocationVariable<?>> locationVariables) {
		List<String> subexpressions = LocationVariable.subexpressionsOf(locationVariables);
		return asSymbols(subexpressions);
	}
	
	protected ISymbol symbolFromExpressionOf(LocationVariable<?> locationVariable) {
		return asSymbol(locationVariable.expression());
	}
	 
	protected ISymbol symbolFromSubexpressionOf(LocationVariable<?> locationVariable) {
		return asSymbol(locationVariable.subexpression());
	}
	
	protected List<ISymbol> asSymbols(Collection<String> symbols) {
		return smtlib().symbolsFor(symbols);
	}
	 
	protected ISymbol asSymbol(String string) {
		return smtlib().symbolFor(string);
	}

	protected List<IExpr> asSMTExpressions(Collection<Object> obejcts) {
		return smtlib().asIExprs(obejcts);
	}
	
	protected IExpr asNumeral(int number) {
		return smtlib().numeral(Integer.toString(number));
	}
	
	protected IExpr binaryOperationWithExpression(ISymbol operation, IExpr operand, LocationVariable<?> variable) {
		return binaryOperation(operation, operand, symbolFromExpressionOf(variable));
	}
	
	protected IExpr binaryOperationWithExpression(ISymbol operation, LocationVariable<?> variable, IExpr operand) {
		return binaryOperation(operation, symbolFromExpressionOf(variable), operand);
	}
	
	protected IExpr binaryOperationWithExpression(ISymbol operation, LocationVariable<?> variable, LocationVariable<?> otherVariable) {
		return binaryOperation(operation, symbolFromExpressionOf(variable), symbolFromExpressionOf(otherVariable));
	}
	
	protected IExpr binaryOperationWithSubexpression(ISymbol operation, LocationVariable<?> variable, IExpr operand) {
		return binaryOperation(operation, symbolFromSubexpressionOf(variable), operand);
	}

	protected IExpr binaryOperationWithSubexpression(ISymbol operation, IExpr operand, LocationVariable<?> variable) {
		return binaryOperation(operation, operand, symbolFromSubexpressionOf(variable));
	}
	
	protected IExpr binaryOperationWithSubexpression(ISymbol operation, LocationVariable<?> variable, LocationVariable<?> otherVariable) {
		return binaryOperation(operation, symbolFromSubexpressionOf(variable), symbolFromSubexpressionOf(otherVariable));
	}
	
	protected IExpr binaryOperation(ISymbol operation, IExpr firstOperand, IExpr secondOperand) {
		return smtlib().expression(operation, firstOperand, secondOperand);
	}
	
	protected IExpr disjunctionOf(Collection<IExpr> expressions) {
		return chained(expressions, or());
	}
	
	protected IExpr conjunctionOf(Collection<IExpr> expressions) {
		return chained(expressions, and());
	}
	
	private IExpr chained(Collection<IExpr> expressions, ISymbol symbol) {
		if (expressions.isEmpty()) {
			return booleanTrue();
		}
		if (expressions.size() == 1) {
			return CollectionLibrary.any(expressions);
		}
		return smtlib().expression(symbol, expressions.toArray(new IExpr[expressions.size()]));
	}
	
	protected SMTLib smtlib() {
		if (smtlib == null) {
			smtlib = new SMTLib();
		}
		return smtlib;
	}
	
	protected ISymbol name() {
		return nameSymbol;
	}
	
	private SMTLib smtlib;
	private ISymbol nameSymbol;
}
