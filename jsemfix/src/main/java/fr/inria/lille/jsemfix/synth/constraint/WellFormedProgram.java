package fr.inria.lille.jsemfix.synth.constraint;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISort;
import org.smtlib.SMT.Configuration;

import fr.inria.lille.jsemfix.synth.BinaryOperator;

final class WellFormedProgram {

	private static final String FUNCTION_NAME = "wfp";
	private static final String LEFT_INPUT_LINE_PREFIX = "L_Il";
	private static final String RIGHT_INPUT_LINE_PREFIX = "L_Ir";

	private final ISymbol and;
	private final ICommand.IFactory commandFactory;
	private final IExpr.IFactory efactory;
	private final ISort intSort;
	private final ISymbol output;
	private final ISort.IFactory sortfactory;

	WellFormedProgram(final Configuration smtConfig) {
		this.efactory = smtConfig.exprFactory;
		this.sortfactory = smtConfig.sortFactory;
		this.commandFactory = smtConfig.commandFactory;
		this.intSort = this.sortfactory.createSortExpression(this.efactory.symbol("Int"));
		this.output = this.efactory.symbol("LO");
		this.and = this.efactory.symbol("and");
	}

	IExpr createFunctionCallFor(final List<BinaryOperator> binaryOperators, final ISymbol outputLine) {
		List<IExpr> parameters = new ArrayList<>();
		for (BinaryOperator operator : binaryOperators) {
			parameters.add(operator.getLeftInputLine());
			parameters.add(operator.getRightInputLine());
		}
		parameters.add(outputLine);
		return this.efactory.fcn(this.efactory.symbol(FUNCTION_NAME), parameters);
	}

	ICommand createFunctionDefinitionFor(final int numberOfInputs, final int numberOperators) {
		checkArgument(numberOfInputs > 0, "The number of inputs should be greater than 0: %s", numberOfInputs);
		checkArgument(numberOperators > 0, "The number of operators should be greater than 0: %s", numberOperators);
		List<IDeclaration> parameters = new ArrayList<>(2 * numberOperators + 1);
		for (int i = 0; i < numberOperators; i++) {
			parameters.add(this.efactory.declaration(this.efactory.symbol(LEFT_INPUT_LINE_PREFIX + i), this.intSort));
			parameters.add(this.efactory.declaration(this.efactory.symbol(RIGHT_INPUT_LINE_PREFIX + i), this.intSort));
		}
		parameters.add(this.efactory.declaration(this.output, this.intSort));
		return this.commandFactory.define_fun(this.efactory.symbol(FUNCTION_NAME), parameters, this.sortfactory.Bool(),
				this.createWellFormedProgramConstraint(numberOfInputs, numberOperators));
	}

	private IExpr createRangeExpression(final ISymbol identifier, final long from, final long to) {
		IExpr lowerBoud = this.efactory.fcn(this.efactory.symbol("<="), this.efactory.numeral(from), identifier);
		IExpr upperBound = this.efactory.fcn(this.efactory.symbol("<"), identifier, this.efactory.numeral(to));
		return this.efactory.fcn(this.and, lowerBoud, upperBound);
	}

	private IExpr createWellFormedProgramConstraint(final int numberOfInputs, final int numberOperators) {
		List<IExpr> constraints = new ArrayList<>();

		long numberOfComponents = numberOperators + numberOfInputs;

		for (int i = 0; i < numberOperators; i++) {
			constraints.add(this.createRangeExpression(this.efactory.symbol(LEFT_INPUT_LINE_PREFIX + i), 0L,
					numberOfInputs - 1L));
			constraints.add(this.createRangeExpression(this.efactory.symbol(RIGHT_INPUT_LINE_PREFIX + i), 0L,
					numberOfInputs - 1L));
		}
		constraints.add(this.createRangeExpression(this.output, numberOfInputs, numberOfComponents));

		return this.efactory.fcn(this.and, constraints);
	}
}
