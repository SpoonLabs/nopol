package fr.inria.lille.commons.synthesis.smt;

import org.smtlib.ICommand;
import org.smtlib.IExpr;
import org.smtlib.IExpr.*;
import org.smtlib.IParser.ParserException;
import org.smtlib.ISort;
import org.smtlib.sexpr.Parser;
import xxl.java.container.classic.MetaList;

import java.util.Collection;
import java.util.List;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.smtlib;

public class SMTLibParser {

    public static List<IExpr> expressionsFrom(Collection<String> expressions) throws ParserException {
        List<IExpr> parsedExpressions = MetaList.newArrayList();
        for (String expression : expressions) {
            parsedExpressions.add(expressionFrom(expression));
        }
        return parsedExpressions;
    }

    public static List<IDeclaration> declarationsFrom(Collection<String> declarations) throws ParserException {
        List<IDeclaration> parsedDeclarations = MetaList.newArrayList();
        for (String declaration : declarations) {
            parsedDeclarations.add(declarationFrom(declaration));
        }
        return parsedDeclarations;
    }

    public static ISymbol symbolFrom(String symbol) throws ParserException {
        // format:	[a-zA-Z~!@%$&^*_+=<>.?/-][0-9a-zA-Z~!@%$&^*_+=<>.?/-]*
        return parser(symbol).parseSymbol();
    }

    public static IBinaryLiteral binaryFrom(String binary) throws ParserException {
        // format:	#b[01]+
        return parser(binary).parseBinary();
    }

    public static IHexLiteral hexFrom(String hex) throws ParserException {
        // format:	#x[0-9a-fA-F]+
        return parser(hex).parseHex();
    }

    public static INumeral numeralFrom(String numeral) throws ParserException {
        // format:	0|([1-9][0-9]*)
        return parser(numeral).parseNumeral();
    }

    public static IDecimal decimalFrom(String decimal) throws ParserException {
        // format:	<numeral>[.]([0-9]+)
        return parser(decimal).parseDecimal();
    }

    public static IKeyword keywordFrom(String keyword) throws ParserException {
        // format:	:[a-zA-Z~!@%$&^*_+=<>.?/-]+
        return parser(keyword).parseKeyword();
    }

    public static ISort sortFrom(String sort) throws ParserException {
        return parser(sort).parseSort(null);
    }

    public static ICommand commandFrom(String command) {
        return parser(command).parseCommand();
    }

    public static IExpr expressionFrom(String expression) throws ParserException {
        return parser(expression).parseExpr();
    }

    public static IDeclaration declarationFrom(String declaration) throws ParserException {
        return parser(declaration).parseDeclaration();
    }

    private static Parser parser(String string) {
        return smtlib().parserFor(string);
    }
}
