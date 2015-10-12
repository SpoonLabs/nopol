package fr.inria.lille.commons.synthesis.smt;

import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import org.smtlib.*;
import org.smtlib.ICommand.IScript;
import org.smtlib.ICommand.Iassert;
import org.smtlib.ICommand.Ideclare_fun;
import org.smtlib.ICommand.Idefine_fun;
import org.smtlib.IExpr.*;
import org.smtlib.SMT.Configuration;
import org.smtlib.logic.*;
import org.smtlib.sexpr.Parser;
import xxl.java.container.classic.MetaList;
import xxl.java.container.classic.MetaMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class SMTLib {

	/* Refer to: http://smtlib.cs.uiowa.edu/papers/smt-lib-reference-v2.0-r10.12.21.pdf */

    public static SMTLib smtlib() {
        if (smtlib == null) {
            smtlib = new SMTLib();
        }
        return smtlib;
    }

    public static ISymbol not() {
        return smtlib().globalSymbol("not");
    }

    public static ISymbol and() {
        return smtlib().globalSymbol("and");
    }

    public static ISymbol or() {
        return smtlib().globalSymbol("or");
    }

    public static ISymbol implies() {
        return smtlib().globalSymbol("=>");
    }

    public static ISymbol ifThenElse() {
        return smtlib().globalSymbol("ite");
    }

    public static ISymbol equality() {
        return smtlib().globalSymbol("=");
    }

    public static ISymbol lessThan() {
        return smtlib().globalSymbol("<");
    }

    public static ISymbol lessOrEqualThan() {
        return smtlib().globalSymbol("<=");
    }

    public static ISymbol distinct() {
        return smtlib().globalSymbol("distinct");
    }

    public static ISymbol addition() {
        return smtlib().globalSymbol("+");
    }

    public static ISymbol subtraction() {
        return smtlib().globalSymbol("-");
    }

    public static ISymbol multiplication() {
        return smtlib().globalSymbol("*");
    }

    public static ISymbol logicAuflia() {
        return smtlib().globalSymbol(AUFLIA.class.getSimpleName());
    }

    public static ISymbol logicAuflira() {
        return smtlib().globalSymbol(AUFLIRA.class.getSimpleName());
    }

    public static ISymbol logicAufnira() {
        return smtlib().globalSymbol(AUFNIRA.class.getSimpleName());
    }

    public static ISymbol logicLra() {
        return smtlib().globalSymbol(LRA.class.getSimpleName());
    }

    public static ISymbol logicQfUf() {
        return smtlib().globalSymbol(QF_UF.class.getSimpleName());
    }

    public static ISymbol logicQfLia() {
        return smtlib().globalSymbol(QF_LIA.class.getSimpleName());
    }

    public static ISymbol logicQfLra() {
        return smtlib().globalSymbol(QF_LRA.class.getSimpleName());
    }

    public static ISymbol logicQfNia() {
        return smtlib().globalSymbol(QF_NIA.class.getSimpleName());
    }

    public static ISymbol booleanTrue() {
        return org.smtlib.Utils.TRUE;
    }

    public static ISymbol booleanFalse() {
        return org.smtlib.Utils.FALSE;
    }

    public static ISort boolSort() {
        return smtlib().sortFactory().Bool();
    }

    public static ISort intSort() {
        return smtlib().sortFor("Int");
    }

    public static ISort numberSort() {
        return smtlib().sortFor("Real");
    }

    public SMTLib() {
        //
    }

    public IBinaryLiteral binary(String binary) {
        String regex = "[01]+";
        if (binary.matches(regex)) {
            return expressionFactory().binary(binary);
        }
        throw new IllegalStateException(format("[SMTLib] Binary '%s' does not match expected format '%s'", binary, regex));
    }

    public IHexLiteral hex(String hex) {
        String regex = "[0-9a-fA-F]+";
        if (hex.matches(regex)) {
            return expressionFactory().hex(hex);
        }
        throw new IllegalStateException(format("[SMTLib] Hex '%s' does not match expected format '%s'", hex, regex));
    }

    public INumeral numeral(String numeral) {
        String regex = "0|([1-9][0-9]*)";
        if (numeral.matches(regex)) {
            return expressionFactory().numeral(numeral);
        }
        throw new IllegalStateException(format("[SMTLib] Numeral '%s' does not match expected format '%s'", numeral, regex));
    }

    public IDecimal decimal(String decimal) {
        String regex = "(0|([1-9][0-9]*))([.]([0-9]+))?";
        if (decimal.matches(regex)) {
            return expressionFactory().decimal(decimal);
        }
        throw new IllegalStateException(format("[SMTLib] Decimal '%s' does not match expected format '%s'", decimal, regex));
    }

    public IKeyword keyword(String keyword) {
        String regex = ":[0-9a-zA-Z~!@%$&*_+=<>.?/-]+";
        if (keyword.matches(regex)) {
            return expressionFactory().keyword(keyword);
        }
        throw new IllegalStateException(format("[SMTLib] Keyword '%s' does not match expected format '%s'", keyword, regex));
    }

    public List<ISymbol> symbolsFor(Collection<String> symbols) {
        List<ISymbol> smtSymbols = MetaList.newLinkedList();
        for (String symbol : symbols) {
            smtSymbols.add(symbolFor(symbol));
        }
        return smtSymbols;
    }

    public ISymbol symbolFor(String symbol) {
        String regex = "[a-zA-Z~!@%$&^*_+=<>.?/-][0-9a-zA-Z~!@%$&^*_+=<>.?/-]*";
        if (symbol.matches(regex)) {
            return expressionFactory().symbol(symbol);
        }
        throw new IllegalStateException(format("[SMTLib] Symbol '%s' does not match expected format '%s'", symbol, regex));
    }

    public ISort sortFor(Class<?> aClass) {
        return ObjectToExpr.sortFor(aClass);
    }

    public List<IExpr> asIExprs(Collection<Object> objects) {
        List<IExpr> smtExprs = MetaList.newLinkedList();
        for (Object object : objects) {
            try {
                smtExprs.add(asIExpr(object));
            } catch (Exception e) {
                // ignore the object
            }
        }
        return smtExprs;
    }

    public IExpr asIExpr(Object object) {
        return ObjectToExpr.asIExpr(object);
    }

    public IFcnExpr equalsExpression(IExpr first, IExpr second) {
        return expression(equality(), first, second);
    }

    public IFcnExpr distinctExpression(IExpr first, IExpr second) {
        return expression(distinct(), first, second);
    }

    public IFcnExpr conjunctionExpression(List<? extends IExpr> elements) {
        return expression(and(), elements);
    }

    public IFcnExpr conjunctionExpression(IExpr... elements) {
        return expression(and(), asList(elements));
    }

    public IFcnExpr disjunctionExpression(List<? extends IExpr> elements) {
        return expression(or(), elements);
    }

    public IFcnExpr disjunctionExpression(IExpr... elements) {
        return expression(or(), asList(elements));
    }

    public IFcnExpr notExpression(IExpr subexpression) {
        return expression(not(), subexpression);
    }

    public IFcnExpr expression(ISymbol identifier, IExpr... arguments) {
        List<IExpr> argumentList = MetaList.newArrayList(arguments);
        return expression(identifier, argumentList);
    }

    public IFcnExpr expression(ISymbol identifier, List<? extends IExpr> arguments) {
        return expressionFactory().fcn(identifier, (List) arguments);
    }

    public IExists exists(List<IDeclaration> declarations, IExpr predicate) {
        if (!declarations.isEmpty()) {
            return expressionFactory().exists(declarations, predicate);
        }
        throw new IllegalStateException("Can not build an IExpr.IExists statement without declarations");
    }

    public IForall forall(List<IDeclaration> declarations, IExpr predicate) {
        if (!declarations.isEmpty()) {
            return expressionFactory().forall(declarations, predicate);
        }
        throw new IllegalStateException("Can not build an IExpr.IForall statement without declarations");
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
        List<ISort> zeroParameters = MetaList.newLinkedList();
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

    public IScript scriptFrom(ISymbol solverLogic, Collection<ICommand> commands, Collection<ICommand> assertions) {
        List<ICommand> allCommands = MetaList.newLinkedList(produceModelOption(), setLogicCommand(solverLogic));
        allCommands.addAll(commands);
        allCommands.addAll(assertions);
        return scriptFrom(allCommands);
    }

    public IScript scriptFrom(List<ICommand> commands) {
        return commandFactory().script(null, commands);
    }

    public Map<String, String> anySolutionFor(IScript script, List<? extends IExpr> variables) {
        SMTLibScriptSolution scriptSolver = scriptSolution(script, variables);
        if (scriptSolver.hasMoreElements()) {
            return scriptSolver.nextElement();
        }
        return MetaMap.newHashMap();
    }

    public SMTLibScriptSolution scriptSolution(IScript script, List<? extends IExpr> variables) {
        return new SMTLibScriptSolution(this, script, variables);
    }

    public Parser parserFor(String response) {
        ISource source = smtFactory().createSource(response, null);
        return (Parser) smtFactory().createParser(configuration(), source);
    }

    private ISort sortFor(String sort) {
        return (ISort) globalElement(sort, true);
    }

    private ISymbol globalSymbol(String name) {
        return (ISymbol) globalElement(name, false);
    }

    private IAccept globalElement(String name, boolean isSort) {
        if (existsGlobalElement(name)) {
            return globalSMTLibElement(name);
        }
        IAccept createdElement = symbolFor(name);
        if (isSort) {
            createdElement = sortFactory().createSortParameter((ISymbol) createdElement);
        }
        return globalSMTLibElement(name, createdElement);
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
            solver = SolverFactory.instance().newSolver();
        }
        return solver;
    }

    private boolean existsGlobalElement(String name) {
        return globalSMTLibElements().containsKey(name);
    }

    private IAccept globalSMTLibElement(String name, IAccept value) {
        globalSMTLibElements().put(name, value);
        return value;
    }

    private IAccept globalSMTLibElement(String name) {
        return globalSMTLibElements().get(name);
    }

    private Map<String, IAccept> globalSMTLibElements() {
        if (globalSMTLibElements == null) {
            globalSMTLibElements = MetaMap.newHashMap();
        }
        return globalSMTLibElements;
    }

    private ISolver solver;
    private static SMTLib smtlib;
    private static Map<String, IAccept> globalSMTLibElements;
}
