package fr.inria.lille.commons.synthesis.smt;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.smtlib.ICommand;
import org.smtlib.ICommand.IScript;
import org.smtlib.ICommand.Iassert;
import org.smtlib.ICommand.Ideclare_fun;
import org.smtlib.ICommand.Idefine_fun;
import org.smtlib.IExpr;
import org.smtlib.IExpr.IBinaryLiteral;
import org.smtlib.IExpr.IDecimal;
import org.smtlib.IExpr.IDeclaration;
import org.smtlib.IExpr.IExists;
import org.smtlib.IExpr.IFcnExpr;
import org.smtlib.IExpr.IForall;
import org.smtlib.IExpr.IHexLiteral;
import org.smtlib.IExpr.IKeyword;
import org.smtlib.IExpr.INumeral;
import org.smtlib.IExpr.ISymbol;
import org.smtlib.IParser;
import org.smtlib.IResponse;
import org.smtlib.ISolver;
import org.smtlib.ISort;
import org.smtlib.ISource;
import org.smtlib.SMT.Configuration;
import org.smtlib.logic.AUFLIA;
import org.smtlib.logic.AUFLIRA;
import org.smtlib.logic.AUFNIRA;
import org.smtlib.logic.LRA;
import org.smtlib.logic.QF_LIA;
import org.smtlib.logic.QF_LRA;
import org.smtlib.logic.QF_NIA;
import org.smtlib.logic.QF_UF;
import org.smtlib.sexpr.Parser;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.MapLibrary;

public class SMTLib {

	/* Refer to: http://smtlib.cs.uiowa.edu/papers/smt-lib-reference-v2.0-r10.12.21.pdf */
	
	public static SMTLib smtlib() {
		if (smtlib == null) {
			smtlib = new SMTLib();
		}
		return smtlib;
	}
	
	public SMTLib() {
		solver().start();
	}
	
	public List<ISymbol> symbolsFor(Collection<String> symbols) {
		List<ISymbol> smtSymbols = ListLibrary.newLinkedList();
		for (String symbol : symbols) {
			smtSymbols.add(symbolFor(symbol));
		}
		return smtSymbols;
	}
	
	public IBinaryLiteral binary(String binary) {
		// format:	#b[01]+
		return expressionFactory().binary(binary);
	}
	
	public IHexLiteral hex(String hex) {
		// format:	#x[0-9a-fA-F]+
		return expressionFactory().hex(hex);
	}
	
	public INumeral numeral(String numeral) {
		// format:	0|([1-9][0-9]*)
		return expressionFactory().numeral(numeral);
	}
	
	public IDecimal decimal(String decimal) {
		// format:	<numeral>[.]([0-9]+)
		return expressionFactory().decimal(decimal);
	}
	
	public IKeyword keyword(String keyword) {
		// format:	:[0-9a-zA-Z~!@%$&*_+=<>.?/-]+
		return expressionFactory().keyword(keyword);
	}
	
	public ISymbol symbolFor(String symbol) {
		// format:	[a-zA-Z~!@%$&^*_+=<>.?/-][0-9a-zA-Z~!@%$&^*_+=<>.?/-]*
		return expressionFactory().symbol(symbol);
	}
	
	public ISymbol not() {
		return symbolFor("not");
	}
	
	public ISymbol and() {
		return symbolFor("and");
	}
	
	public ISymbol or() {
		return symbolFor("or");
	}
	
	public ISymbol implies() {
		return symbolFor("=>");
	}
	
	public ISymbol ifThenElse() {
		return symbolFor("ite");
	}
	
	public ISymbol equals() {
		return symbolFor("=");
	}
	
	public ISymbol lessThan() {
		return symbolFor("<");
	}
	
	public ISymbol lessOrEqualThan() {
		return symbolFor("<=");
	}
	
	public ISymbol distinct() {
		return symbolFor("distinct");
	}
	
	public ISymbol addition() {
		return symbolFor("+");
	}
	
	public ISymbol substraction() {
		return symbolFor("-");
	}
	
	public ISymbol multiplication() {
		return symbolFor("*");
	}

	public ISymbol logicAuflia() {
		return symbolFor(AUFLIA.class.getSimpleName());
	}
	
	public ISymbol logicAuflira() {
		return symbolFor(AUFLIRA.class.getSimpleName());
	}
	
	public ISymbol logicAufnira() {
		return symbolFor(AUFNIRA.class.getSimpleName());
	}
	
	public ISymbol logicLra() {
		return symbolFor(LRA.class.getSimpleName());
	}
	
	public ISymbol logicQfUf() {
		return symbolFor(QF_UF.class.getSimpleName());
	}
	
	public ISymbol logicQfLia() {
		return symbolFor(QF_LIA.class.getSimpleName());
	}
	
	public ISymbol logicQfLra() {
		return symbolFor(QF_LRA.class.getSimpleName());
	}
	
	public ISymbol logicQfNia() {
		return symbolFor(QF_NIA.class.getSimpleName());
	}
	
	public ISymbol booleanTrue() {
		return org.smtlib.Utils.TRUE;
	}
	
	public ISymbol booleanFalse() {
		return org.smtlib.Utils.FALSE;
	}
	
	public ISort boolSort() {
		return sortFactory().Bool();
	}
	
	public ISort intSort() {
		return sortFor("Int");
	}
	
	public ISort numberSort() {
		return sortFor("Real");
	}
	
	public ISort sortFor(Class<?> aClass) {
		return ObjectToExpr.sortFor(aClass);
	}
	
	public IExpr asIExpr(Object object) {
		return ObjectToExpr.asIExpr(object);
	}
	
	public IFcnExpr expression(ISymbol identifier, IExpr... arguments) {
		return expression(identifier, Arrays.asList(arguments));
	}
	
	public IFcnExpr expression(ISymbol identifier, List<IExpr> arguments) {
		return expressionFactory().fcn(identifier, arguments);
	}
	
	public IExists exists(List<IDeclaration> declarations, IExpr predicate) {
		return expressionFactory().exists(declarations, predicate);
	}
	
	public IForall forall(List<IDeclaration> declarations, IExpr predicate) {
		return expressionFactory().forall(declarations, predicate);
	}
	
	public IDeclaration declaration(String name, Class<?> aClass) {
		return declaration(name, sortFor(aClass));
	}
	
	public IDeclaration declaration(String name, ISort type) {
		return declaration(symbolFor(name), type);
	}
	
	public IDeclaration declaration(ISymbol symbol, Class<?> aClass) {
		return declaration(symbol, sortFor(aClass));
	}
	
	public IDeclaration declaration(ISymbol symbol, ISort type) {
		return expressionFactory().declaration(symbol, type);
	}
	
	public Ideclare_fun constant(String name, ISort type) {
		return constant(symbolFor(name), type);
	}
	
	public Ideclare_fun constant(ISymbol symbol, ISort type) {
		List<ISort> zeroParameters = ListLibrary.newLinkedList();
		return functionDeclaration(symbol, zeroParameters, type);
	}
	
	public Ideclare_fun functionDeclaration(ISymbol symbol, List<ISort> parameters, ISort outputType) {
		return commandFactory().declare_fun(symbol, parameters, outputType);
	}
	
	public Idefine_fun functionDefinition(ISymbol identifier, List<IDeclaration> parameters, ISort outputType, IExpr definition) {
		return commandFactory().define_fun(identifier, parameters, outputType, definition);
	}
	
	public Iassert assertion(IExpr expression) {
		return commandFactory().assertCommand(expression);
	}
	
	public ICommand produceModelOption() {
		return commandFactory().set_option(keyword(org.smtlib.Utils.PRODUCE_MODELS), booleanTrue());
	}
	
	public ICommand setLogicCommand(ISymbol logicSymbol) {
		return commandFactory().set_logic(logicSymbol);
	}
	
	public IScript scriptFrom(ISymbol logic, Collection<ICommand> functionDeclarations, Collection<ICommand> functionDefinitions, Collection<ICommand> assertions) {
		List<ICommand> commands = ListLibrary.newArrayList();
		commands.add(setLogicCommand(logic));
		commands.add(produceModelOption());
		commands.addAll(functionDeclarations);
		commands.addAll(functionDefinitions);
		commands.addAll(assertions);
		return commandFactory().script(null, commands);
	}
	
	public Map<String, String> satisfyingValuesFor(List<IExpr> expressions, IScript script) {
		IResponse response = script.execute(solver());
		if (response.isOK() && isSatisfitable(solver())) {
			IExpr[] expressionArray = expressions.toArray(new IExpr[expressions.size()]);
			response = solver().get_value(expressionArray);
			return SexprSolutionVisitor.solutionsFrom(response);
		}
		return MapLibrary.newHashMap();
	}

	public boolean isSatisfitable(ISolver solver) {
		return solver.check_sat().equals(org.smtlib.impl.Response.SAT);
	}

	public Parser parserFor(String response) {
		ISource source = smtFactory().createSource(response, null);
		return (Parser) smtFactory().createParser(configuration(), source);
	}
	
	private ISort sortFor(String sort) {
		if (createdSorts().containsKey(sort)) {
			return createdSorts().get(sort);
		}
		ISort createdSort = sortFactory().createSortParameter(symbolFor(sort));
		createdSorts().put(sort, createdSort);
		return createdSort;
	}
	
	private ISort.IFactory sortFactory() {
		return configuration().sortFactory;
	}
	
	private ICommand.IFactory commandFactory() {
		return configuration().commandFactory;
	}
	
	private IExpr.IFactory expressionFactory() {
		return configuration().exprFactory;
	}
	
	private IParser.IFactory smtFactory() {
		return configuration().smtFactory;
	}
	
	private Configuration configuration() {
		return solver().smt();
	}
	
	private ISolver solver() {
		if (solver == null) {
			solver = SolverFactory.newSolver();
		}
		return solver;
	}
	
	private Map<String, ISort> createdSorts() {
		if (createdSorts == null) {
			createdSorts = MapLibrary.newHashMap();
		}
		return createdSorts;
	}
	
	private ISolver solver;
	private static SMTLib smtlib;
	private static Map<String, ISort> createdSorts;
}
