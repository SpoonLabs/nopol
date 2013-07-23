package fr.inria.lille.jefix.synth.smt.constraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.IQualifiedIdentifier;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISort;
import org.smtlib.SMT.Configuration;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import fr.inria.lille.jefix.synth.smt.model.Component;
import fr.inria.lille.jefix.synth.smt.model.InputModel;
import fr.inria.lille.jefix.synth.smt.model.Type;

final class WellFormedProgram {

	/**
	 * XXX FIXME TODO should be {@code static}...
	 * 
	 * @author Favio D. DeMarco
	 */
	private final class WellFormedProgramConstraint {

		final IQualifiedIdentifier equals;
		final Multimap<Type, IExpr> typeToSymbols;

		WellFormedProgramConstraint(@Nonnull final InputModel model) {
			Multimap<Type, IExpr> sets = Multimaps.newSetMultimap(Maps.<Type, Collection<IExpr>> newHashMap(),
					new Supplier<Set<IExpr>>() {
				@Override
				public Set<IExpr> get() {
					return new HashSet<>();
				}
			});
			long i = 0L;
			for (Type type : model.getInputTypes()) {
				sets.put(type, WellFormedProgram.this.efactory.numeral(i++));
			}
			int j = 0;
			for (Component component : model.getComponents()) {
				sets.put(component.getOutputType(), WellFormedProgram.this.efactory.symbol(OUTPUT_LINE_PREFIX + j++));
			}

			this.typeToSymbols = sets;
			this.equals = WellFormedProgram.this.efactory.symbol("=");
		}

		IExpr createFor(@Nonnull final ISymbol symbol, @Nonnull final Type type) {
			Collection<IExpr> symbols = this.typeToSymbols.get(type);
			List<IExpr> comparisons = new ArrayList<>(symbols.size());
			for (IExpr other : symbols) {
				comparisons.add(WellFormedProgram.this.efactory.fcn(this.equals, Arrays.<IExpr> asList(symbol, other)));
			}
			return new Simplifier(WellFormedProgram.this.efactory).simplifyOr(comparisons);
		}
	}

	static final String FUNCTION_NAME = "wfp";
	private static final String INPUT_LINE_FORMAT = "L_I%d_%d";
	private static final String OUTPUT_LINE = "LO";
	private static final String OUTPUT_LINE_PREFIX = "LO_";

	private final ICommand.IFactory commandFactory;
	private final IExpr.IFactory efactory;
	private final ISort intSort;
	private final ISort.IFactory sortfactory;
	private final IQualifiedIdentifier lessOrEqualThan;
	private final IQualifiedIdentifier lessThan;
	private final IQualifiedIdentifier and;

	WellFormedProgram(final Configuration smtConfig) {
		this.efactory = smtConfig.exprFactory;
		this.sortfactory = smtConfig.sortFactory;
		this.commandFactory = smtConfig.commandFactory;
		this.intSort = this.sortfactory.createSortExpression(this.efactory.symbol("Int"));
		this.lessOrEqualThan = this.efactory.symbol("<=");
		this.lessThan = this.efactory.symbol("<");
		this.and = this.efactory.symbol("and");
	}

	private IExpr createAcyclicityConstraintCall(final List<Component> components) {
		List<IExpr> parameters = new ArrayList<>(components.size() * 3);
		int componentIndex = 0;
		for (Component component : components) {
			for (int parameterIndex = 0; parameterIndex < component.getParameterTypes().size(); parameterIndex++) {
				parameters.add(this.efactory.symbol(String.format(INPUT_LINE_FORMAT, componentIndex, parameterIndex)));
			}
			parameters.add(this.efactory.symbol(OUTPUT_LINE_PREFIX + componentIndex++));
		}
		return this.efactory.fcn(this.efactory.symbol(Acyclicity.FUNCTION_NAME), parameters);
	}

	private IExpr createConsistencyConstraintCall(final int size) {
		List<IExpr> parameters = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			parameters.add(this.efactory.symbol(OUTPUT_LINE_PREFIX + i));
		}
		return this.efactory.fcn(this.efactory.symbol(Consistency.FUNCTION_NAME), parameters);
	}

	private IExpr createConstraint(final InputModel model) {
		List<IExpr> constraints = new ArrayList<>();
		constraints.add(this.createConsistencyConstraintCall(model.getComponents().size()));
		constraints.add(this.createAcyclicityConstraintCall(model.getComponents()));
		WellFormedProgramConstraint wfpConstraint = new WellFormedProgramConstraint(model);
		long inputsCount = model.getInputTypes().size();
		long componentsCount = inputsCount + model.getComponents().size();
		int componentIndex = 0;
		for (Component component : model.getComponents()) {
			int parameterIndex = 0;
			for (Type type : component.getParameterTypes()) {
				ISymbol symbol = this.efactory.symbol(String.format(INPUT_LINE_FORMAT, componentIndex, parameterIndex));
				constraints.add(wfpConstraint.createFor(symbol, type));
				parameterIndex++;
			}
			IExpr outputRange = this.createRangeExpression(this.efactory.symbol(OUTPUT_LINE_PREFIX + componentIndex),
					inputsCount, componentsCount);
			constraints.add(outputRange);

			componentIndex++;
		}
		constraints.add(wfpConstraint.createFor(this.efactory.symbol(OUTPUT_LINE), model.getOutputType()));
		return this.efactory.fcn(this.efactory.symbol("and"), constraints);
	}

	private IExpr createRangeExpression(final IQualifiedIdentifier identifier, final long from, final long to) {
		IExpr leftInput = this.efactory.fcn(this.lessOrEqualThan, this.efactory.numeral(from), identifier);
		IExpr rightInput = this.efactory.fcn(this.lessThan, identifier, this.efactory.numeral(to));
		return this.efactory.fcn(this.and, leftInput, rightInput);
	}

	ICommand createFunctionDefinitionFor(@Nonnull final InputModel model) {
		List<Component> components = model.getComponents();
		List<IDeclaration> parameters = new ArrayList<>(components.size() * 3);
		int componentIndex = 0;
		for (Component component : components) {
			for (int parameterIndex = 0; parameterIndex < component.getParameterTypes().size(); parameterIndex++) {
				ISymbol symbol = this.efactory.symbol(String.format(INPUT_LINE_FORMAT, componentIndex, parameterIndex));
				parameters.add(this.efactory.declaration(symbol, this.intSort));
			}
			ISymbol symbol = this.efactory.symbol(OUTPUT_LINE_PREFIX + componentIndex++);
			parameters.add(this.efactory.declaration(symbol, this.intSort));
		}
		ISymbol symbol = this.efactory.symbol(OUTPUT_LINE);
		parameters.add(this.efactory.declaration(symbol, this.intSort));
		return this.commandFactory.define_fun(this.efactory.symbol(FUNCTION_NAME), parameters, this.sortfactory.Bool(),
				this.createConstraint(model));
	}
}
