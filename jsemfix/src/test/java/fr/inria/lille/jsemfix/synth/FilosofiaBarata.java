package fr.inria.lille.jsemfix.synth;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.smtlib.Utils.PRODUCE_MODELS;
import static org.smtlib.Utils.TRUE;
import static org.smtlib.impl.Response.SAT;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.smtlib.ICommand;
import org.smtlib.ICommand.IScript;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.IQualifiedIdentifier;
import org.smtlib.IExpr.IStringLiteral;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.IResponse;
import org.smtlib.ISolver;
import org.smtlib.ISort;
import org.smtlib.SMT;
import org.smtlib.SMT.Configuration;
import org.smtlib.impl.Response;
import org.smtlib.logic.AUFLIA;
import org.smtlib.solvers.Solver_cvc4;
import org.smtlib.solvers.Solver_test;

public final class FilosofiaBarata {

	private static final String CVC4_BINARY_PATH = "/usr/bin/cvc4";

	private final Configuration smtConfig = new SMT().smtConfig;

	private final IExpr.IFactory efactory = this.smtConfig.exprFactory;
	private final ISort.IFactory sortfactory = this.smtConfig.sortFactory;
	private final ICommand.IFactory commandFactory = this.smtConfig.commandFactory;

	private final ISort intSort = this.sortfactory.createSortExpression(this.efactory.symbol("Int"));

	private final IQualifiedIdentifier and = this.efactory.symbol("and");
	private final IQualifiedIdentifier distinct = this.efactory.symbol("distinct");
	private final IQualifiedIdentifier equals = this.efactory.symbol("=");
	private final IQualifiedIdentifier lessOrEqualThan = this.efactory.symbol("<=");
	private final IQualifiedIdentifier lessThan = this.efactory.symbol("<");

	private final ISymbol output = this.efactory.symbol("O");

	private final List<Long> constants = Arrays.asList(0L, 1L);
	private final List<String> operators = Arrays.asList("=", "<", "distinct", "<=");
	private final List<String> variables = Arrays.asList("a", "b");
	private final List<List<Long>> parameters = asList(asList(1L, 6L), asList(22L, 7L), asList(8L, 8L));
	private final List<Boolean> results = asList(true, false, false);

	@Test
	public void yZapatosDeGoma() {
		assertEquals(this.parameters.size(), this.results.size());

		List<ICommand> commands = new ArrayList<>();

		// initialize solver
		commands.add(this.commandFactory.set_option(this.efactory.keyword(PRODUCE_MODELS), TRUE));
		commands.add(this.commandFactory.set_logic(this.efactory.symbol(AUFLIA.class.getSimpleName())));

		List<BinaryOperator> binaryOperators = new ArrayList<>(this.operators.size());
		for (int order = 0; order < this.operators.size(); order++) {
			binaryOperators.add(BinaryOperator.createForLine(order, this.efactory));
		}

		ISymbol outputLine = this.efactory.symbol("LO");

		commands.add(this.createWellFormedProgramFunction(binaryOperators, outputLine));
		commands.add(this.createLibFunction(binaryOperators));

		List<IDeclaration> iot = this.createInputOutputPAndRDeclarations(binaryOperators, outputLine);
		IExpr exists = this.efactory.exists(iot, this.createConstraint(binaryOperators, outputLine));
		commands.add(this.commandFactory.assertCommand(exists));

		this.print(commands);

		// WHEN
		IScript script = this.commandFactory.script((IStringLiteral) null, commands);

		ISolver solver = new Solver_test(this.smtConfig, (String) null);
		// THEN
		assertTrue(solver.start().isOK());
		IResponse response = script.execute(solver);
		assertTrue(response.toString(), response.isOK());
		// sat
		IResponse sat = solver.check_sat();
		assertEquals(Response.UNKNOWN, sat);
		assertTrue(solver.exit().isOK());

		if (new File(CVC4_BINARY_PATH).exists()) {
			this.solve(script, binaryOperators, outputLine);
		}
	}

	private ICommand createLibFunction(final List<BinaryOperator> binaryOperators) {
		List<IDeclaration> parameters = new ArrayList<>();
		for (BinaryOperator operator : binaryOperators) {
			parameters.add(this.efactory.declaration(operator.getLeftInput(), this.intSort));
			parameters.add(this.efactory.declaration(operator.getRightInput(), this.intSort));
			parameters.add(this.efactory.declaration(operator.getOutput(), this.sortfactory.Bool()));
		}
		return this.commandFactory.define_fun(this.efactory.symbol("lib"), parameters, this.sortfactory.Bool(),
				this.createLibConstraint(binaryOperators));
	}

	private ICommand createWellFormedProgramFunction(final List<BinaryOperator> binaryOperators,
			final ISymbol outputLine) {
		List<IDeclaration> parameters = new ArrayList<>();
		for (BinaryOperator operator : binaryOperators) {
			parameters.add(this.efactory.declaration(operator.getLeftInputLine(), this.intSort));
			parameters.add(this.efactory.declaration(operator.getRightInputLine(), this.intSort));
			parameters.add(this.efactory.declaration(operator.getOutputLine(), this.intSort));
		}
		parameters.add(this.efactory.declaration(outputLine, this.intSort));
		return this.commandFactory.define_fun(this.efactory.symbol("wfp"), parameters, this.sortfactory.Bool(),
				this.createWellFormedProgramConstraint(binaryOperators, outputLine));
	}

	private IExpr createConstraint(final List<BinaryOperator> binaryOperators, final ISymbol outputLine) {
		List<IExpr> constraints = new ArrayList<>();
		constraints.add(this.callWellFormedProgramConstraint(binaryOperators, outputLine));

		for (int i = 0; i < this.results.size(); i++) {
			constraints.add(this.callLibConstraint(binaryOperators, i));
			// constraints.add(this.createConnectivityConstraint(binaryOperators, outputLine, values));
			// constraints.add(this.createSpecificationConstraint(values));
		}
		return this.efactory.fcn(this.and, constraints);
	}

	private IExpr callLibConstraint(final List<BinaryOperator> binaryOperators, final int i) {
		List<IExpr> parameters = new ArrayList<>();
		for (BinaryOperator operator : binaryOperators) {
			parameters.add(this.efactory.symbol(operator.getLeftInput().value() + '_' + i));
			parameters.add(this.efactory.symbol(operator.getRightInput().value() + '_' + i));
			parameters.add(this.efactory.symbol(operator.getOutput().value() + '_' + i));
		}
		return this.efactory.fcn(this.efactory.symbol("lib"), parameters);
	}

	private IExpr callWellFormedProgramConstraint(final List<BinaryOperator> binaryOperators, final ISymbol outputLine) {
		List<IExpr> parameters = new ArrayList<>();
		for (BinaryOperator operator : binaryOperators) {
			parameters.add(operator.getLeftInputLine());
			parameters.add(operator.getRightInputLine());
			parameters.add(operator.getOutputLine());
		}
		parameters.add(outputLine);
		return this.efactory.fcn(this.efactory.symbol("wfp"), parameters);
	}

	private IExpr createSpecificationConstraint(final Iterable<ISymbol> variables) {
		List<IExpr> constraints = new ArrayList<>();
		constraints.add(this.createSpecificationConstraintFor(variables, asList(1L, 6L), true));
		constraints.add(this.createSpecificationConstraintFor(variables, asList(22L, 7L), false));
		constraints.add(this.createSpecificationConstraintFor(variables, asList(8L, 8L), false));
		return this.efactory.fcn(this.and, constraints);
	}

	private IExpr createSpecificationConstraintFor(final Iterable<ISymbol> variables, final Iterable<Long> values,
			final boolean result) {
		IExpr and = this.createValuesSpecificationConstraintFor(variables, values);
		IExpr out = result ? this.output : this.efactory.fcn(this.efactory.symbol("not"), this.output);
		return this.efactory.fcn(this.efactory.symbol("=>"), and, out);
	}

	private IExpr createValuesSpecificationConstraintFor(final Iterable<ISymbol> variables, final Iterable<Long> values) {
		Iterator<ISymbol> variablesIter = variables.iterator();
		Iterator<Long> valuesIter = values.iterator();

		IExpr varA = this.efactory.fcn(this.equals, variablesIter.next(), this.efactory.numeral(valuesIter.next()));
		IExpr varB = this.efactory.fcn(this.equals, variablesIter.next(), this.efactory.numeral(valuesIter.next()));
		return this.efactory.fcn(this.and, varA, varB);
	}

	private IExpr createConnectivityConstraint(final List<BinaryOperator> binaryOperators, final ISymbol outputLine,
			final Iterable<ISymbol> values) {
		List<IExpr> constraints = new ArrayList<>();
		for (BinaryOperator operator : binaryOperators) {
			this.addConnectivityConstraintFor(operator.getLeftInputLine(), operator.getLeftInput(), values, constraints);
			this.addConnectivityConstraintFor(operator.getRightInputLine(), operator.getRightInput(), values,
					constraints);

			constraints.add(this.createConnectivityConstraintFor(outputLine, operator.getOutputLine(), this.output,
					operator.getOutput()));
		}
		return this.efactory.fcn(this.and, constraints);
	}

	private void addConnectivityConstraintFor(final ISymbol inputLine, final ISymbol input,
			final Iterable<ISymbol> values, final Collection<IExpr> constraints) {
		long line = 0L;
		for (ISymbol value : values) {
			constraints
			.add(this.createConnectivityConstraintFor(inputLine, this.efactory.numeral(line++), input, value));
		}
		for (Long constant : this.constants) {
			constraints.add(this.createConnectivityConstraintFor(inputLine, this.efactory.numeral(line++), input,
					this.efactory.numeral(constant)));
		}
	}

	private IExpr createConnectivityConstraintFor(final IExpr line, final IExpr lineNumber, final IExpr input,
			final IExpr value) {
		IExpr lines = this.efactory.fcn(this.equals, line, lineNumber);
		IExpr vars = this.efactory.fcn(this.equals, input, value);
		return this.efactory.fcn(this.efactory.symbol("=>"), lines, vars);
	}

	/**
	 * XXX FIXME TODO Does List mean fat interface?
	 * 
	 * @param binaryOperators
	 */
	private IExpr createLibConstraint(final List<BinaryOperator> binaryOperators) {
		List<IExpr> constraints = new ArrayList<>();
		for (int index = 0; index < this.operators.size(); index++) {
			String symbol = this.operators.get(index);
			BinaryOperator operator = binaryOperators.get(index);
			IExpr term = this.efactory.fcn(this.efactory.symbol(symbol), operator.getLeftInput(),
					operator.getRightInput());
			IExpr output = this.efactory.fcn(this.equals, operator.getOutput(), term);
			constraints.add(output);
		}
		return this.simplify(constraints);
	}

	private IExpr createWellFormedProgramConstraint(final List<BinaryOperator> binaryOperators, final ISymbol outputLine) {
		List<IExpr> constraints = new ArrayList<>();
		constraints.add(this.createConsistencyConstraint(binaryOperators));
		constraints.add(this.createAcyclicityConstraint(binaryOperators));

		long numberOfInputs = this.constants.size() + this.variables.size();
		long numberOfComponents = this.operators.size() + numberOfInputs;

		for (BinaryOperator operator : binaryOperators) {
			IExpr leftInputRange = this.createRangeExpression(operator.getLeftInputLine(), 0L, numberOfInputs - 1L);
			constraints.add(leftInputRange);

			IExpr rightInputRange = this.createRangeExpression(operator.getRightInputLine(), 0L, numberOfInputs - 1L);
			constraints.add(rightInputRange);

			IExpr outputRange = this
					.createRangeExpression(operator.getOutputLine(), numberOfInputs, numberOfComponents);
			constraints.add(outputRange);
		}
		IExpr outputRange = this.createRangeExpression(outputLine, numberOfInputs, numberOfComponents);
		constraints.add(outputRange);

		return this.efactory.fcn(this.and, constraints);
	}

	private IExpr createRangeExpression(final IQualifiedIdentifier identifier, final long from, final long to) {
		IExpr leftInput = this.efactory.fcn(this.lessOrEqualThan, this.efactory.numeral(from), identifier);
		IExpr rightInput = this.efactory.fcn(this.lessThan, identifier, this.efactory.numeral(to));
		return this.efactory.fcn(this.and, leftInput, rightInput);
	}

	private IExpr createAcyclicityConstraint(final Iterable<BinaryOperator> binaryOperators) {
		List<IExpr> constraints = new ArrayList<>();
		for (BinaryOperator operator : binaryOperators) {
			IExpr leftInput = this.efactory.fcn(this.lessThan, operator.getLeftInputLine(), operator.getOutputLine());
			IExpr rightInput = this.efactory.fcn(this.lessThan, operator.getRightInputLine(), operator.getOutputLine());
			IExpr constraint = this.efactory.fcn(this.and, leftInput, rightInput);
			constraints.add(constraint);
		}
		return this.simplify(constraints);
	}

	private IExpr createConsistencyConstraint(final List<BinaryOperator> binaryOperators) {
		List<IExpr> constraints = new ArrayList<>();
		int i = 1;
		int size = binaryOperators.size();
		for (BinaryOperator leftOperator : binaryOperators) {
			Iterable<BinaryOperator> subList = binaryOperators.subList(i++, size);
			for (BinaryOperator rightOperator : subList) {
				IExpr comparison = this.efactory.fcn(this.distinct, leftOperator.getOutputLine(),
						rightOperator.getOutputLine());
				constraints.add(comparison);
			}
		}
		return this.simplify(constraints);
	}

	private IExpr simplify(final List<IExpr> constraints) {
		if (constraints.isEmpty()) {
			return this.efactory.symbol("true");
		} else if (constraints.size() == 1) {
			return constraints.get(0);
		} else {
			return this.efactory.fcn(this.and, constraints);
		}
	}

	private List<IDeclaration> createInputOutputPAndRDeclarations(final Iterable<BinaryOperator> binaryOperators,
			final ISymbol outputLine) {
		List<IDeclaration> declarations = new ArrayList<>();
		for (BinaryOperator operator : binaryOperators) {
			declarations.add(this.efactory.declaration(operator.getLeftInputLine(), this.intSort));
			declarations.add(this.efactory.declaration(operator.getRightInputLine(), this.intSort));
			declarations.add(this.efactory.declaration(operator.getOutputLine(), this.intSort));
		}
		for (int i = 0; i < this.parameters.size(); i++) {
			for (BinaryOperator operator : binaryOperators) {
				declarations.add(this.efactory.declaration(
						this.efactory.symbol(operator.getLeftInput().value() + '_' + i), this.intSort));
				declarations.add(this.efactory.declaration(
						this.efactory.symbol(operator.getRightInput().value() + '_' + i), this.intSort));
				declarations.add(this.efactory.declaration(
						this.efactory.symbol(operator.getOutput().value() + '_' + i), this.sortfactory.Bool()));
			}
		}
		declarations.add(this.efactory.declaration(outputLine, this.intSort));
		return declarations;
	}

	private void print(final Iterable<ICommand> commands) {
		int i = 0;
		for (String variable : this.variables) {
			System.out.printf("%d:\t%s%n", i++, variable);
		}
		for (Long value : this.constants) {
			System.out.printf("%d:\t%d%n", i++, value);
		}
		int j = 0;
		for (String operator : this.operators) {
			System.out.printf("O%d:\t... %s ...%n", j++, operator);
		}
		System.out.println();
		for (ICommand command : commands) {
			System.out.println(this.pretty(command.toString()));
		}
	}

	private CharSequence pretty(final String string) {
		StringBuilder builder = new StringBuilder(string.length() * 2);
		String tabs = "";
		for (char c : string.toCharArray()) {
			if (c == '(') {
				if (!tabs.isEmpty()) {
					builder.append(System.lineSeparator());
					builder.append(tabs);
				}
				tabs += '\t';
			} else if (c == ')') {
				tabs = tabs.substring(1);
			}
			builder.append(c);
		}
		return builder;
	}

	private void solve(final IScript script, final List<BinaryOperator> binaryOperators, final ISymbol outputLine) {
		ISolver solver = new Solver_cvc4(this.smtConfig, CVC4_BINARY_PATH);

		// THEN
		assertTrue(solver.start().isOK());
		assertTrue(script.execute(solver).isOK());

		// sat
		assertEquals(SAT, solver.check_sat());

		System.out.println();
		System.out.println("Model:");
		for (BinaryOperator op : binaryOperators) {
			System.out.println(solver.get_value(op.getOutputLine(), op.getLeftInputLine(), op.getRightInputLine()));
		}
		System.out.println(solver.get_value(outputLine));
		assertTrue(solver.exit().isOK());
	}

	private void addIntegerFunctionTo(final ISymbol symbol, final Collection<ICommand> commands) {
		commands.add(this.commandFactory.declare_fun(symbol, Collections.<ISort> emptyList(), this.intSort));
	}
}
