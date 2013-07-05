package fr.inria.lille.jsemfix.synth.constraint;

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

import com.google.common.base.Function;

import fr.inria.lille.jsemfix.synth.model.Component;
import fr.inria.lille.jsemfix.synth.model.Type;

final class Library {

	static final String FUNCTION_NAME = "lib";
	private static final String OUTPUT_PREFIX = "O_";
	private static final String INPUT_FORMAT = "I%d_%d";

	private final ICommand.IFactory commandFactory;
	private final IExpr.IFactory efactory;
	private final IQualifiedIdentifier equals;
	private final ISort.IFactory sortfactory;
	private final Function<Type, ISort> typeToSort;

	Library(final Configuration smtConfig) {
		this.efactory = smtConfig.exprFactory;
		this.sortfactory = smtConfig.sortFactory;
		this.commandFactory = smtConfig.commandFactory;
		this.equals = this.efactory.symbol("=");
		this.typeToSort = new TypeToSort(this.sortfactory, this.efactory);
	}

	ICommand createFunctionDefinitionFor(@Nonnull final Iterable<Component> components) {
		List<IDeclaration> parameters = new ArrayList<>();
		int componentIndex = 0;
		for (Component component : components) {
			int parameterIndex = 0;
			for (Type type : component.getParameterTypes()) {
				ISymbol symbol = this.efactory.symbol(String.format(INPUT_FORMAT, componentIndex, parameterIndex));
				parameters.add(this.efactory.declaration(symbol, this.typeToSort.apply(type)));
				parameterIndex++;
			}
			ISymbol symbol = this.efactory.symbol(OUTPUT_PREFIX + componentIndex);
			parameters.add(this.efactory.declaration(symbol, this.typeToSort.apply(component.getOutputType())));
			componentIndex++;
		}
		return this.commandFactory.define_fun(this.efactory.symbol(FUNCTION_NAME), parameters, this.sortfactory.Bool(),
				this.createLibConstraint(components));
	}

	private IExpr createLibConstraint(@Nonnull final Iterable<Component> components) {
		List<IExpr> constraints = new ArrayList<>();
		int componentIndex = 0;
		for (Component component : components) {
			List<IExpr> parameters = new ArrayList<>();
			for (int parameterIndex = 0; parameterIndex < component.getParameterTypes().size(); parameterIndex++) {
				parameters.add(this.efactory.symbol(String.format(INPUT_FORMAT, componentIndex, parameterIndex)));
			}
			IExpr term = this.efactory.fcn(this.efactory.symbol(component.getName()), parameters);
			IExpr output = this.efactory.fcn(this.equals, this.efactory.symbol(OUTPUT_PREFIX + componentIndex), term);
			constraints.add(output);

			componentIndex++;
		}
		return new Simplifier(this.efactory).simplifyAnd(constraints);
	}
}
