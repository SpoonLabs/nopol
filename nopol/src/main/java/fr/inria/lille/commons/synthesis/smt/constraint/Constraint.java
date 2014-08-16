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

import xxl.java.extensions.collection.CollectionLibrary;
import xxl.java.extensions.collection.ListLibrary;
import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.smt.SMTLib;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariable;
import fr.inria.lille.commons.synthesis.smt.locationVariables.LocationVariableContainer;

public abstract class Constraint {

	protected abstract Collection<IExpr> definitionExpressions(LocationVariableContainer container);
	
	public List<LocationVariable<?>> variablesForExpression(LocationVariableContainer container) {
		return ListLibrary.newArrayList();
	}
	
	public List<LocationVariable<?>> variablesForSubexpression(LocationVariableContainer container) {
		return ListLibrary.newArrayList();
	}
	
	protected List<IExpr> instantiatedArguments(LocationVariableContainer container, Map<String, Object> values) {
		throw new UnsupportedOperationException("Constraint.instantiatedArguments should be overridden by subclasses");		
	}
	
	public Constraint(String name, SMTLib smtlib) {
		this.smtlib = smtlib;
		nameSymbol = smtlib().symbolFor(name);
	}

	public boolean isCompound() {
		return false;
	}
	
	public ICommand definition(LocationVariableContainer container) {
		List<IDeclaration> parameters = parameters(container);
		Collection<IExpr> expressions = definitionExpressions(container);
		IExpr finalExpression = conjunctionOf(expressions);
		return smtlib().functionDefinition(nameSymbol(), parameters, boolSort(), finalExpression);
	}
	
	public List<IDeclaration> parameters(LocationVariableContainer container) {
		List<LocationVariable<?>> variablesForExpression = variablesForExpression(container);
		List<LocationVariable<?>> variablesForSubexpression = variablesForSubexpression(container);
		List<IDeclaration> declarations = declarationsFromExpression(variablesForExpression);
		declarations.addAll(declarationsFromSubexpression(variablesForSubexpression));
		return declarations;
	}
	
	public IExpr invocation(LocationVariableContainer container) {
		List<IExpr> arguments = invocationArguments(container);
		return invocationWith(arguments);
	}
	
	public List<IExpr> invocationArguments(LocationVariableContainer container) {
		List<LocationVariable<?>> variablesForExpression = variablesForExpression(container);
		List<LocationVariable<?>> variablesForSubexpression = variablesForSubexpression(container);
		List<ISymbol> arguments = expressionSymbolsOf(variablesForExpression);
		arguments.addAll(subexpressionSymbolsOf(variablesForSubexpression));
		return (List) arguments;
	}
	
	public IExpr invocationWithValues(LocationVariableContainer container, Map<String, Object> values) {
		return invocationWith(instantiatedArguments(container, values));
	}
	
	protected List<IDeclaration> declarationsFromExpression(Collection<LocationVariable<?>> locationVariables) {
		List<IDeclaration> declarations = ListLibrary.newLinkedList();
		for (LocationVariable<?> locationVariable : locationVariables) {
			declarations.add(declarationFromExpression(locationVariable));
		}
		return declarations;
	}

	protected IDeclaration declarationFromExpression(LocationVariable<?> locationVariable) {
		return smtlib().declaration(locationVariable.expression(), intSort());
	}
	
	protected List<IDeclaration> declarationsFromSubexpression(Collection<LocationVariable<?>> locationVariables) {
		List<IDeclaration> declarations = ListLibrary.newLinkedList();
		for (LocationVariable<?> locationVariable : locationVariables) {
			declarations.add(declarationFromSubexpression(locationVariable));
		}
		return declarations;
	}
	
	protected IDeclaration declarationFromSubexpression(LocationVariable<?> locationVariable) {
		return smtlib().declaration(locationVariable.subexpression(), locationVariable.smtSort());
	}
	
	protected List<ISymbol> expressionSymbolsOf(Collection<LocationVariable<?>> locationVariables) {
		List<String> expressions = Expression.expressionsOf(locationVariables);
		return asSymbols(expressions);
	}
	
	protected ISymbol expressionSymbolOf(LocationVariable<?> locationVariable) {
		return asSymbol(locationVariable.expression());
	}
	 
	protected List<ISymbol> subexpressionSymbolsOf(Collection<LocationVariable<?>> locationVariables) {
		List<String> subexpressions = LocationVariable.subexpressionsOf(locationVariables);
		return asSymbols(subexpressions);
	}
	
	protected ISymbol subexpressionSymbolOf(LocationVariable<?> locationVariable) {
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
	
	protected IExpr binaryOperation(IExpr firstOperand, ISymbol operation, IExpr secondOperand) {
		return smtlib().expression(operation, firstOperand, secondOperand);
	}
	
	protected IExpr disjunctionOf(Collection<IExpr> expressions) {
		return chained(expressions, or());
	}
	
	protected IExpr conjunctionOf(Collection<IExpr> expressions) {
		return chained(expressions, and());
	}
	
	protected SMTLib smtlib() {
		return smtlib;
	}
	
	protected ISymbol nameSymbol() {
		return nameSymbol;
	}
	
	private IExpr invocationWith(List<IExpr> arguments) {
		if (arguments.isEmpty()) {
			return nameSymbol();
		}
		return smtlib().expression(nameSymbol(), arguments);
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
	
	private SMTLib smtlib;
	private ISymbol nameSymbol;
}
