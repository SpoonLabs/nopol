package fr.inria.lille.jsemfix.synth.constraint;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.IQualifiedIdentifier;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISort;
import org.smtlib.SMT.Configuration;

final class Connectivity {

	private static final String OUTPUT_LINE = "LO";
	private static final String OUTPUT = "O";
	private static final String FUNCTION_NAME = "conn";
	private static final String INPUT_PREFIX = "I_";
	private static final String LEFT_INPUT_LINE_PREFIX = "L_Il";
	private static final String LEFT_INPUT_PREFIX = "Il_";
	private static final String OUTPUT_LINE_PREFIX = "L_O";
	private static final String OUTPUT_PREFIX = "O_";
	private static final String RIGHT_INPUT_LINE_PREFIX = "L_Ir";
	private static final String RIGHT_INPUT_PREFIX = "Ir_";

	private final ISymbol and;
	private final ICommand.IFactory commandFactory;
	private final IExpr.IFactory efactory;
	private final IQualifiedIdentifier equals;
	private final ISort intSort;
	private final ISort.IFactory sortfactory;

	Connectivity(final Configuration smtConfig) {
		this.efactory = smtConfig.exprFactory;
		this.sortfactory = smtConfig.sortFactory;
		this.commandFactory = smtConfig.commandFactory;
		this.intSort = this.sortfactory.createSortExpression(this.efactory.symbol("Int"));
		this.and = this.efactory.symbol("and");
		this.equals = this.efactory.symbol("=");
	}

	private void addConnectivityConstraintFor(final ISymbol inputLine, final ISymbol input, final int numberOfInputs,
			final Collection<IExpr> constraints) {
		long line = 0L;
		for (int i = 0; i < numberOfInputs; i++) {
			constraints.add(this.createConnectivityConstraintFor(inputLine, this.efactory.numeral(line++), input,
					this.efactory.symbol(INPUT_PREFIX + i)));
		}
	}

	private IExpr createConnectivityConstraint(final int numberOfInputs, final int numberOperators) {
		List<IExpr> constraints = new ArrayList<>();
		for (int i = 0; i < numberOperators; i++) {
			this.addConnectivityConstraintFor(this.efactory.symbol(LEFT_INPUT_LINE_PREFIX + i),
					this.efactory.symbol(LEFT_INPUT_PREFIX + i), numberOfInputs, constraints);
			this.addConnectivityConstraintFor(this.efactory.symbol(RIGHT_INPUT_LINE_PREFIX + i),
					this.efactory.symbol(RIGHT_INPUT_PREFIX + i), numberOfInputs, constraints);

			constraints.add(this.createConnectivityConstraintFor(this.efactory.symbol(OUTPUT_LINE),
					this.efactory.symbol(OUTPUT_LINE_PREFIX + i), this.efactory.symbol(OUTPUT),
					this.efactory.symbol(OUTPUT_PREFIX + i)));
		}
		return this.efactory.fcn(this.and, constraints);
	}

	private IExpr createConnectivityConstraintFor(final IExpr line, final IExpr lineNumber, final IExpr input,
			final IExpr value) {
		IExpr lines = this.efactory.fcn(this.equals, line, lineNumber);
		IExpr vars = this.efactory.fcn(this.equals, input, value);
		return this.efactory.fcn(this.efactory.symbol("=>"), lines, vars);
	}

	ICommand createFunctionDefinitionFor(final int numberOfInputs, final int numberOperators) {
		checkArgument(numberOfInputs > 0, "The number of inputs should be greater than 0: %s", numberOfInputs);
		checkArgument(numberOperators > 0, "The number of operators should be greater than 0: %s", numberOperators);
		List<IDeclaration> parameters = new ArrayList<>(6 * numberOperators + numberOfInputs + 2);
		for (int i = 0; i < numberOperators; i++) {
			parameters.add(this.efactory.declaration(this.efactory.symbol(LEFT_INPUT_PREFIX + i), this.intSort));
			parameters.add(this.efactory.declaration(this.efactory.symbol(LEFT_INPUT_LINE_PREFIX + i), this.intSort));
			parameters.add(this.efactory.declaration(this.efactory.symbol(RIGHT_INPUT_PREFIX + i), this.intSort));
			parameters.add(this.efactory.declaration(this.efactory.symbol(RIGHT_INPUT_LINE_PREFIX + i), this.intSort));
			parameters.add(this.efactory.declaration(this.efactory.symbol(OUTPUT_PREFIX + i), this.sortfactory.Bool()));
			parameters.add(this.efactory.declaration(this.efactory.symbol(OUTPUT_LINE_PREFIX + i), this.intSort));
		}
		for (int i = 0; i < numberOfInputs; i++) {
			parameters.add(this.efactory.declaration(this.efactory.symbol(INPUT_PREFIX + i), this.intSort));
		}
		parameters.add(this.efactory.declaration(this.efactory.symbol(OUTPUT), this.sortfactory.Bool()));
		parameters.add(this.efactory.declaration(this.efactory.symbol(OUTPUT_LINE), this.intSort));
		return this.commandFactory.define_fun(this.efactory.symbol(FUNCTION_NAME), parameters, this.sortfactory.Bool(),
				this.createConnectivityConstraint(numberOfInputs, numberOperators));
	}
}
