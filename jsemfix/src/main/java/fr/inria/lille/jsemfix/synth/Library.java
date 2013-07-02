package fr.inria.lille.jsemfix.synth;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.IQualifiedIdentifier;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISort;
import org.smtlib.SMT.Configuration;

final class Library {

	private static final String FUNCTION_NAME = "lib";
	private static final String LEFT_INPUT_PREFIX = "Il_";
	private static final String OUTPUT_PREFIX = "O_";
	private static final String RIGHT_INPUT_PREFIX = "Ir_";

	private final ISymbol and;
	private final ICommand.IFactory commandFactory;
	private final IExpr.IFactory efactory;
	private final IQualifiedIdentifier equals;
	private final ISort intSort;
	private final ISort.IFactory sortfactory;

	Library(final Configuration smtConfig) {
		this.efactory = smtConfig.exprFactory;
		this.sortfactory = smtConfig.sortFactory;
		this.commandFactory = smtConfig.commandFactory;
		this.intSort = this.sortfactory.createSortExpression(this.efactory.symbol("Int"));
		this.and = this.efactory.symbol("and");
		this.equals = this.efactory.symbol("=");
	}

	IExpr createFunctionCallFor(final List<BinaryOperator> binaryOperators) {
		List<IExpr> parameters = new ArrayList<>();
		for (BinaryOperator operator : binaryOperators) {
			parameters.add(operator.getLeftInput());
			parameters.add(operator.getRightInput());
			parameters.add(operator.getOutput());
		}
		return this.efactory.fcn(this.efactory.symbol(FUNCTION_NAME), parameters);
	}

	ICommand createFunctionDefinitionFor(final Iterable<String> binaryOperators) {
		List<IDeclaration> parameters = new ArrayList<>();
		Iterator<String> iterator = checkNotNull(binaryOperators).iterator();
		int i = 0;
		while (iterator.hasNext()) {
			parameters.add(this.efactory.declaration(this.efactory.symbol(LEFT_INPUT_PREFIX + i), this.intSort));
			parameters.add(this.efactory.declaration(this.efactory.symbol(RIGHT_INPUT_PREFIX + i), this.intSort));
			parameters.add(this.efactory.declaration(this.efactory.symbol(OUTPUT_PREFIX + i), this.sortfactory.Bool()));
			iterator.next();
			i++;
		}
		return this.commandFactory.define_fun(this.efactory.symbol(FUNCTION_NAME), parameters, this.sortfactory.Bool(),
				this.createLibConstraint(binaryOperators));
	}

	private IExpr createLibConstraint(final Iterable<String> binaryOperators) {
		List<IExpr> constraints = new ArrayList<>();
		int i = 0;
		for (String symbol : binaryOperators) {
			IExpr term = this.efactory.fcn(this.efactory.symbol(symbol), this.efactory.symbol(LEFT_INPUT_PREFIX + i),
					this.efactory.symbol(RIGHT_INPUT_PREFIX + i));
			IExpr output = this.efactory.fcn(this.equals, this.efactory.symbol(OUTPUT_PREFIX + i), term);
			constraints.add(output);
			i++;
		}
		return this.simplify(constraints);
	}

	private IExpr simplify(final List<IExpr> constraints) {
		if (constraints.isEmpty()) {
			return this.efactory.symbol("true");
		} else if (constraints.size() == 1) {
			return constraints.get(0);
		}
		return this.efactory.fcn(this.and, constraints);
	}
}
