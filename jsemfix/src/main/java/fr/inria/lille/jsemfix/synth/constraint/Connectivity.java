package fr.inria.lille.jsemfix.synth.constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.IQualifiedIdentifier;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISort;
import org.smtlib.SMT.Configuration;

import com.google.common.base.Function;

import fr.inria.lille.jsemfix.synth.model.Component;
import fr.inria.lille.jsemfix.synth.model.InputModel;
import fr.inria.lille.jsemfix.synth.model.Type;

final class Connectivity {

	private static final String FUNCTION_NAME = "conn";
	private static final String INPUT_FORMAT = "I%d_%d";
	private static final String INPUT_LINE_FORMAT = "L_I%d_%d";
	private static final String OUTPUT = "O";
	private static final String OUTPUT_LINE = "LO";
	private static final String OUTPUT_LINE_PREFIX = "LO_";
	private static final String OUTPUT_PREFIX = "O_";
	private static final String INPUT_PREFIX = "I_";

	private final ISymbol and;
	private final ICommand.IFactory commandFactory;
	private final IExpr.IFactory efactory;
	private final IQualifiedIdentifier equals;
	private final ISort intSort;
	private final ISort.IFactory sortfactory;
	private final Function<Type, ISort> typeToSort;

	Connectivity(@Nonnull final Configuration smtConfig) {
		this.efactory = smtConfig.exprFactory;
		this.sortfactory = smtConfig.sortFactory;
		this.commandFactory = smtConfig.commandFactory;
		this.intSort = this.sortfactory.createSortExpression(this.efactory.symbol("Int"));
		this.and = this.efactory.symbol("and");
		this.equals = this.efactory.symbol("=");
		this.typeToSort = new TypeToSort(this.sortfactory, this.efactory);
	}

	private void addConnectivityConstraintFor(final Type type, final ISymbol inputLine, final ISymbol input,
			final InputModel model, @Nonnull final Collection<IExpr> constraints) {
		long line = 0L;
		for (Type inputType : model.getInputTypes()) {
			if (type == inputType) {
				constraints.add(this.createConnectivityConstraintFor(inputLine, this.efactory.numeral(line), input,
						this.efactory.symbol(INPUT_PREFIX + line)));
			}
			line++;
		}
		int componentIndex = 0;
		for (Component component : model.getComponents()) {
			if (type == component.getOutputType()) {
				constraints.add(this.createConnectivityConstraintFor(inputLine,
						this.efactory.symbol(OUTPUT_LINE_PREFIX + componentIndex), input,
						this.efactory.symbol(OUTPUT_PREFIX + componentIndex)));
			}
			componentIndex++;
		}
	}

	/**
	 * XXX FIXME TODO cyclomatic complexity
	 * 
	 * @param model
	 * @return
	 */
	private IExpr createConnectivityConstraint(@Nonnull final InputModel model) {
		List<IExpr> constraints = new ArrayList<>();
		Type outputType = model.getOutputType();
		int componentIndex = 0;
		for (Component component : model.getComponents()) {
			int parameterIndex = 0;
			for (Type type : component.getParameterTypes()) {
				this.addConnectivityConstraintFor(type,
						this.efactory.symbol(String.format(INPUT_LINE_FORMAT, componentIndex, parameterIndex)),
						this.efactory.symbol(String.format(INPUT_FORMAT, componentIndex, parameterIndex)), model,
						constraints);
				parameterIndex++;
			}
			if (outputType == component.getOutputType()) {
				constraints.add(this.createConnectivityConstraintFor(this.efactory.symbol(OUTPUT_LINE),
						this.efactory.symbol(OUTPUT_LINE_PREFIX + componentIndex), this.efactory.symbol(OUTPUT),
						this.efactory.symbol(OUTPUT_PREFIX + componentIndex)));
			}
			componentIndex++;
		}
		return this.efactory.fcn(this.and, constraints);
	}

	private IExpr createConnectivityConstraintFor(final IExpr line, final IExpr lineNumber, final IExpr input,
			final IExpr value) {
		IExpr lines = this.efactory.fcn(this.equals, line, lineNumber);
		IExpr vars = this.efactory.fcn(this.equals, input, value);
		return this.efactory.fcn(this.efactory.symbol("=>"), lines, vars);
	}

	ICommand createFunctionDefinitionFor(@Nonnull final InputModel model) {
		List<Type> inputTypes = model.getInputTypes();
		List<Component> components = model.getComponents();
		List<IDeclaration> parameters = new ArrayList<>(6 * components.size() + inputTypes.size() + 2);
		int inputIndex = 0;
		for (Type type : inputTypes) {
			ISymbol input = this.efactory.symbol(String.format(INPUT_PREFIX + inputIndex));
			parameters.add(this.efactory.declaration(input, this.typeToSort.apply(type)));
			inputIndex++;
		}
		int componentIndex = 0;
		for (Component component : components) {
			int parameterIndex = 0;
			for (Type type : component.getParameterTypes()) {
				ISymbol input = this.efactory.symbol(String.format(INPUT_FORMAT, componentIndex, parameterIndex));
				parameters.add(this.efactory.declaration(input, this.typeToSort.apply(type)));
				ISymbol line = this.efactory.symbol(String.format(INPUT_LINE_FORMAT, componentIndex, parameterIndex));
				parameters.add(this.efactory.declaration(line, this.intSort));
				parameterIndex++;
			}
			ISymbol output = this.efactory.symbol(OUTPUT_PREFIX + componentIndex);
			parameters.add(this.efactory.declaration(output, this.typeToSort.apply(component.getOutputType())));
			ISymbol outputLine = this.efactory.symbol(OUTPUT_LINE_PREFIX + componentIndex);
			parameters.add(this.efactory.declaration(outputLine, this.intSort));
			componentIndex++;
		}
		parameters.add(this.efactory.declaration(this.efactory.symbol(OUTPUT),
				this.typeToSort.apply(model.getOutputType())));
		parameters.add(this.efactory.declaration(this.efactory.symbol(OUTPUT_LINE), this.intSort));
		return this.commandFactory.define_fun(this.efactory.symbol(FUNCTION_NAME), parameters, this.sortfactory.Bool(),
				this.createConnectivityConstraint(model));
	}
}
