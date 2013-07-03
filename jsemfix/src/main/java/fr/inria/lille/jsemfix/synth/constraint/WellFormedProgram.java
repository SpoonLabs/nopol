package fr.inria.lille.jsemfix.synth.constraint;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISort;
import org.smtlib.SMT.Configuration;

import fr.inria.lille.jsemfix.synth.BinaryOperator;
import fr.inria.lille.jsemfix.synth.model.Component;
import fr.inria.lille.jsemfix.synth.model.InputModel;
import fr.inria.lille.jsemfix.synth.model.Type;

final class WellFormedProgram {

	private static final String FUNCTION_NAME = "wfp";
	private static final String OUTPUT_LINE_PREFIX = "LO_";
	private static final String INPUT_LINE_FORMAT = "L_I%d_%d";
	private static final String OUTPUT_LINE = "LO";

	private final ICommand.IFactory commandFactory;
	private final IExpr.IFactory efactory;
	private final ISort intSort;
	private final ISort.IFactory sortfactory;

	WellFormedProgram(final Configuration smtConfig) {
		this.efactory = smtConfig.exprFactory;
		this.sortfactory = smtConfig.sortFactory;
		this.commandFactory = smtConfig.commandFactory;
		this.intSort = this.sortfactory.createSortExpression(this.efactory.symbol("Int"));
	}

	ICommand createFunctionDefinitionFor(@Nonnull final InputModel model) {
		List<Component> components = model.getComponents();
		checkArgument(!components.isEmpty(), "The number of operators should be greater than 0.");
		List<IDeclaration> parameters = new ArrayList<>(components.size() * 3);
		int i = 0;
		for (Component operator : components) {
			int j = 0;
			for (Type type : operator.getParameterTypes()) {
				ISymbol symbol = this.efactory.symbol(String.format(INPUT_LINE_FORMAT, i, j++));
				parameters.add(this.efactory.declaration(symbol, this.intSort));
			}
			ISymbol symbol = this.efactory.symbol(OUTPUT_LINE_PREFIX + i++);
			parameters.add(this.efactory.declaration(symbol, this.intSort));
		}
		ISymbol symbol = this.efactory.symbol(OUTPUT_LINE);
		parameters.add(this.efactory.declaration(symbol, this.intSort));
		return this.commandFactory.define_fun(this.efactory.symbol(FUNCTION_NAME), parameters, this.sortfactory.Bool(),
				this.createConstraint(model));
	}

	private IExpr createConstraint(final InputModel model) {
		List<IExpr> constraints = new ArrayList<>();


		return new Simplifier(this.efactory).simplify(constraints);
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
}
