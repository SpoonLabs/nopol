package fr.inria.lille.jsemfix.synth.constraint;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.ISort;
import org.smtlib.SMT.Configuration;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import fr.inria.lille.jsemfix.synth.model.Component;
import fr.inria.lille.jsemfix.synth.model.InputModel;
import fr.inria.lille.jsemfix.synth.model.Type;

final class WellFormedProgram {

	/**
	 * XXX FIXME TODO should be {@code static}...
	 * 
	 * @author Favio D. DeMarco
	 */
	private final class WellFormedProgramConstraint {

		final Multimap<Type, ISymbol> typeToSymbols;

		WellFormedProgramConstraint(final InputModel model) {
			this.typeToSymbols = Multimaps.newSetMultimap(Maps.<Type, Collection<ISymbol>> newHashMap(),
					new Supplier<Set<ISymbol>>() {
				@Override
				public Set<ISymbol> get() {
					return new HashSet<>();
				}
			});
		}

		public IExpr createFor(final ISymbol symbol, final Type type) {
			// TODO Auto-generated method stub
			// return null;
			throw new UnsupportedOperationException("Undefined method WellFormedProgramConstraint.createFor");
		}

	}

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

	private IExpr createConstraint(final InputModel model) {
		List<IExpr> constraints = new ArrayList<>();
		constraints.add(this.createConsistencyConstraintCall(model.getComponents().size()));
		constraints.add(this.createAcyclicityConstraintCall(model.getComponents()));
		WellFormedProgramConstraint wfpConstraint = new WellFormedProgramConstraint(model);
		int componentIndex = 0;
		for (Component component : model.getComponents()) {
			int parameterIndex = 0;
			for (Type type : component.getParameterTypes()) {
				ISymbol symbol = this.efactory.symbol(String.format(INPUT_LINE_FORMAT, componentIndex, parameterIndex));
				constraints.add(wfpConstraint.createFor(symbol, type));
				parameterIndex++;
			}
			componentIndex++;
		}
		constraints.add(wfpConstraint.createFor(this.efactory.symbol(OUTPUT_LINE), model.getOutputType()));
		return this.efactory.fcn(this.efactory.symbol("and"), constraints);
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
}
