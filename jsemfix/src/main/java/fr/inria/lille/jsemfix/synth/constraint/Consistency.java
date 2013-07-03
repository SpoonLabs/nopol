package fr.inria.lille.jsemfix.synth.constraint;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.IQualifiedIdentifier;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISort;
import org.smtlib.SMT.Configuration;

import fr.inria.lille.jsemfix.synth.component.Function;
import fr.inria.lille.jsemfix.synth.component.Type;

final class Consistency {

	private static final String FUNCTION_NAME = "cons";
	private static final String OUTPUT_LINE_PREFIX = "LO_";

	private final ICommand.IFactory commandFactory;
	private final IQualifiedIdentifier distinct;
	private final IExpr.IFactory efactory;
	private final ISort.IFactory sortfactory;
	private final com.google.common.base.Function<Type, ISort> typeToSort;

	Consistency(@Nonnull final Configuration smtConfig) {
		this.efactory = smtConfig.exprFactory;
		this.sortfactory = smtConfig.sortFactory;
		this.commandFactory = smtConfig.commandFactory;
		this.typeToSort = new TypeToSort(this.sortfactory, this.efactory);
		this.distinct = this.efactory.symbol("distinct");
	}

	private IExpr createConstraint(final List<ISymbol> variables) {
		List<IExpr> constraints = new ArrayList<>();
		int i = 1;
		int size = variables.size();
		for (ISymbol leftOperand : variables) {
			Iterable<ISymbol> subList = variables.subList(i++, size);
			for (ISymbol rightOperand : subList) {
				constraints.add(this.efactory.fcn(this.distinct, leftOperand, rightOperand));
			}
		}
		return new Simplifier(this.efactory).simplify(constraints);
	}

	ICommand createFunctionDefinitionFor(@Nonnull final List<Function> operators) {
		checkArgument(!operators.isEmpty(), "The number of operators should be greater than 0.");
		List<IDeclaration> parameters = new ArrayList<>(2 * operators.size());
		List<ISymbol> variables = new ArrayList<>(2 * operators.size());
		int i = 0;
		for (Function operator : operators) {
			ISymbol symbol = this.efactory.symbol(OUTPUT_LINE_PREFIX + i++);
			variables.add(symbol);
			parameters.add(this.efactory.declaration(symbol, this.typeToSort.apply(operator.getOutputType())));
		}
		return this.commandFactory.define_fun(this.efactory.symbol(FUNCTION_NAME), parameters, this.sortfactory.Bool(),
				this.createConstraint(variables));
	}

	IExpr createFunctionCallFor(final List<IExpr> parameters) {
		return this.efactory.fcn(this.efactory.symbol(FUNCTION_NAME), parameters);
	}
}
