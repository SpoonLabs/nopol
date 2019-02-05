package fr.inria.lille.commons.synthesis.smt;

import org.slf4j.Logger;
import org.smtlib.*;
import org.smtlib.ICommand.IScript;
import org.smtlib.ICommand.Iassert;
import org.smtlib.ICommand.Ideclare_fun;
import org.smtlib.ICommand.Idefine_fun;
import org.smtlib.IExpr.*;
import org.smtlib.IExpr.IError;
import org.smtlib.IResponse.*;
import org.smtlib.ISort.*;
import xxl.java.container.classic.MetaSet;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static xxl.java.library.LoggerLibrary.loggerFor;

public class SMTLibEqualVisitor implements IVisitor<Boolean> {

    public static void addAllIfNotContained(Collection<? extends IAccept> elements, Collection<IAccept> collection) {
        for (IAccept element : elements) {
            addIfNotContained(element, collection);
        }
    }

    public static boolean addIfNotContained(IAccept element, Collection<IAccept> collection) {
        if (!contains(element, collection)) {
            collection.add(element);
            return true;
        }
        return false;
    }

    public static boolean haveSameElements(Collection<IAccept> actual, Collection<IAccept> expected) {
        boolean sameSize = expected.size() == actual.size();
        if (sameSize) {
            Collection<Integer> alreadyMatched = MetaSet.newHashSet();
            for (IAccept expectedItem : expected) {
                if (!contains(expectedItem, actual, alreadyMatched)) {
                    return false;
                }
            }
        }
        return sameSize;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean contains(IAccept expected, Collection<IAccept> actual) {
        return contains(expected, actual, (Collection) MetaSet.newHashSet());
    }

    private static boolean contains(IAccept expected, Collection<IAccept> actual, Collection<Integer> alreadyMatched) {
        int index = 0;
        for (IAccept actualItem : actual) {
            if (areEquals(actualItem, expected) && !alreadyMatched.contains(index)) {
                alreadyMatched.add(index);
                return true;
            }
            index += 1;
        }
        return false;
    }

    public static boolean areEquals(IAccept actual, IAccept expected) {
        boolean areEquals = false;
        try {
            SMTLibEqualVisitor visitor = new SMTLibEqualVisitor(expected);
            areEquals = visitor.sortComparison(expected, actual);
            if (actual.getClass().isAssignableFrom(expected.getClass())) {
                areEquals = actual.accept(visitor);
            }
        } catch (AssertionError ae) {
        } catch (VisitorException ve) {
            ve.printStackTrace();
        }
        return areEquals;
    }

    private SMTLibEqualVisitor(Object expected) {
        this.expected = expected;
    }

    @Override
    public Boolean visit(ISymbol actual) throws VisitorException {
        if (instanceOf(ISymbol.class, expected())) {
            ISymbol casted = (ISymbol) expected();
            assertEquals(casted.kind(), actual.kind());
            assertEquals(casted.value(), actual.value());
        }
        return true;
    }

    @Override
    public Boolean visit(IBinaryLiteral actual) throws VisitorException {
        if (instanceOf(IBinaryLiteral.class, expected())) {
            IBinaryLiteral casted = (IBinaryLiteral) expected();
            assertEquals(casted.kind(), actual.kind());
            assertEquals(casted.value(), actual.value());
            assertEquals(casted.length(), actual.length());
            assertEquals(casted.intValue(), actual.intValue());
        }
        return true;
    }

    @Override
    public Boolean visit(INumeral actual) throws VisitorException {
        if (instanceOf(INumeral.class, expected())) {
            INumeral casted = (INumeral) expected();
            assertEquals(casted.kind(), actual.kind());
            assertEquals(casted.value(), actual.value());
            assertEquals(casted.intValue(), actual.intValue());
        }
        return true;
    }

    @Override
    public Boolean visit(IDecimal actual) throws VisitorException {
        if (instanceOf(IDecimal.class, expected())) {
            IDecimal casted = (IDecimal) expected();
            assertEquals(casted.kind(), actual.kind());
            assertEquals(casted.value(), actual.value());
        }
        return true;
    }

    @Override
    public Boolean visit(IHexLiteral actual) throws VisitorException {
        if (instanceOf(IHexLiteral.class, expected())) {
            IHexLiteral casted = (IHexLiteral) expected();
            assertEquals(casted.kind(), actual.kind());
            assertEquals(casted.value(), actual.value());
            assertEquals(casted.length(), actual.length());
            assertEquals(casted.intValue(), actual.intValue());
        }
        return true;
    }

    @Override
    public Boolean visit(IKeyword actual) throws VisitorException {
        if (instanceOf(IKeyword.class, expected())) {
            IKeyword casted = (IKeyword) expected();
            assertEquals(casted.kind(), actual.kind());
            assertEquals(casted.value(), actual.value());
        }
        return true;
    }

    @Override
    public Boolean visit(IDeclaration actual) throws VisitorException {
        if (instanceOf(IDeclaration.class, expected())) {
            IDeclaration casted = (IDeclaration) expected();
            sortComparison(casted.sort(), actual.sort());
            symbolComparison(casted.parameter(), actual.parameter());
        }
        return true;
    }

    @Override
    public Boolean visit(IParameter actual) throws VisitorException {
        if (instanceOf(IParameter.class, expected())) {
            IParameter casted = (IParameter) expected();
            assertEquals(casted.intArity(), actual.intArity());
            assertEquals(casted.isBool(), actual.isBool());
            sortComparison(casted.expand(), actual.expand());
            symbolComparison(casted.symbol(), actual.symbol());
            identifierComparison(casted.identifier(), actual.identifier());
        }
        return true;
    }

    @Override
    public Boolean visit(IFcnExpr actual) throws VisitorException {
        if (instanceOf(IFcnExpr.class, expected())) {
            IFcnExpr casted = (IFcnExpr) expected();
            assertEquals(casted.kind(), actual.kind());
            qualifiedIdentifierComparison(casted.head(), actual.head());
            assertEquals(casted.args().size(), actual.args().size());
            int index = 0;
            for (IExpr arg : casted.args()) {
                expressionComparison(arg, actual.args().get(index));
                index += 1;
            }
        }
        return true;
    }

    @Override
    public Boolean visit(IExists actual) throws VisitorException {
        if (instanceOf(IExists.class, expected())) {
            IExists casted = (IExists) expected();
            assertEquals(casted.kind(), actual.kind());
            expressionComparison(casted.expr(), actual.expr());
            assertEquals(casted.parameters().size(), actual.parameters().size());
            int index = 0;
            for (IDeclaration parameter : casted.parameters()) {
                declarationComparison(parameter, actual.parameters().get(index));
                index += 1;
            }
        }
        return true;
    }

    @Override
    public Boolean visit(IForall actual) throws VisitorException {
        if (instanceOf(IForall.class, expected())) {
            IForall casted = (IForall) expected();
            assertEquals(casted.kind(), actual.kind());
            expressionComparison(casted.expr(), actual.expr());
            assertEquals(casted.parameters().size(), actual.parameters().size());
            int index = 0;
            for (IDeclaration parameter : casted.parameters()) {
                declarationComparison(parameter, actual.parameters().get(index));
                index += 1;
            }
        }
        return true;
    }

    @Override
    public Boolean visit(ICommand actual) throws VisitorException {
        if (instanceOf(ICommand.class, expected())) {
            if (instanceOf(Ideclare_fun.class, expected(), actual)) {
                declareFunctionComparison((Ideclare_fun) expected(), (Ideclare_fun) actual);
            } else if (instanceOf(Idefine_fun.class, expected(), actual)) {
                defineFunctionComparison((Idefine_fun) expected(), (Idefine_fun) actual);
            } else if (instanceOf(Iassert.class, expected(), actual)) {
                assertComparison((Iassert) expected(), (Iassert) actual);
            } else {
                assertEquals(actual, expected());
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IAttribute<?> arg0) throws VisitorException {
        if (instanceOf(IAttribute.class, expected())) {
            IAttribute<?> casted = (IAttribute<?>) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IAttribute)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IAttributedExpr arg0) throws VisitorException {
        if (instanceOf(IAttributedExpr.class, expected())) {
            IAttributedExpr casted = (IAttributedExpr) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IAttributedExpr)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IBinding arg0) throws VisitorException {
        if (instanceOf(IBinding.class, expected())) {
            IBinding casted = (IBinding) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IBinding)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IError arg0) throws VisitorException {
        if (instanceOf(IError.class, expected())) {
            IError casted = (IError) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IError)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(ILet arg0) throws VisitorException {
        if (instanceOf(ILet.class, expected())) {
            ILet casted = (ILet) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(ILet)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IParameterizedIdentifier arg0) throws VisitorException {
        if (instanceOf(IParameterizedIdentifier.class, expected())) {
            IParameterizedIdentifier casted = (IParameterizedIdentifier) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IParameterizedIdentifier)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IAsIdentifier arg0) throws VisitorException {
        if (instanceOf(IAsIdentifier.class, expected())) {
            IAsIdentifier casted = (IAsIdentifier) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IAsIdentifier)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IStringLiteral arg0) throws VisitorException {
        if (instanceOf(IStringLiteral.class, expected())) {
            IStringLiteral casted = (IStringLiteral) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IStringLiteral)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IScript arg0) throws VisitorException {
        if (instanceOf(IScript.class, expected())) {
            IScript casted = (IScript) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IScript)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IFamily arg0) throws VisitorException {
        if (instanceOf(IFamily.class, expected())) {
            IFamily casted = (IFamily) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IFamily)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IAbbreviation arg0) throws VisitorException {
        if (instanceOf(IAbbreviation.class, expected())) {
            IAbbreviation casted = (IAbbreviation) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IAbbreviation)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IApplication arg0) throws VisitorException {
        if (instanceOf(IApplication.class, expected())) {
            IApplication casted = (IApplication) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IApplication)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IFcnSort arg0) throws VisitorException {
        if (instanceOf(IFcnSort.class, expected())) {
            IFcnSort casted = (IFcnSort) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IFcnSort)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(ILogic arg0) throws VisitorException {
        if (instanceOf(ILogic.class, expected())) {
            ILogic casted = (ILogic) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(ILogic)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(ITheory arg0) throws VisitorException {
        if (instanceOf(ITheory.class, expected())) {
            ITheory casted = (ITheory) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(ITheory)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IResponse arg0) throws VisitorException {
        if (instanceOf(IResponse.class, expected())) {
            IResponse casted = (IResponse) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IResponse)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(org.smtlib.IResponse.IError arg0) throws VisitorException {
        if (instanceOf(IResponse.IError.class, expected())) {
            IResponse.IError casted = (IResponse.IError) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IResponse.IError)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IAssertionsResponse arg0) throws VisitorException {
        if (instanceOf(IAssertionsResponse.class, expected())) {
            IAssertionsResponse casted = (IAssertionsResponse) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IAssertionsResponse)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IAssignmentResponse arg0) throws VisitorException {
        if (instanceOf(IAssignmentResponse.class, expected())) {
            IAssignmentResponse casted = (IAssignmentResponse) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IAssignmentResponse)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IProofResponse arg0) throws VisitorException {
        if (instanceOf(IProofResponse.class, expected())) {
            IProofResponse casted = (IProofResponse) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IProofResponse)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IValueResponse arg0) throws VisitorException {
        if (instanceOf(IValueResponse.class, expected())) {
            IValueResponse casted = (IValueResponse) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IValueResponse)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IUnsatCoreResponse arg0) throws VisitorException {
        if (instanceOf(IUnsatCoreResponse.class, expected())) {
            IUnsatCoreResponse casted = (IUnsatCoreResponse) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IUnsatCoreResponse)");
        }
        return true;
    }

    @Override
    @SuppressWarnings("unused")
    public Boolean visit(IAttributeList arg0) throws VisitorException {
        if (instanceOf(IAttributeList.class, expected())) {
            IAttributeList casted = (IAttributeList) expected();
            logger().warn("Empty implementation of " + getClass().getCanonicalName() + ".visit(IAttributeList)");
        }
        return true;
    }

    private void symbolComparison(ISymbol expected, ISymbol actual) throws VisitorException {
        actual.accept(new SMTLibEqualVisitor(expected));
    }

    private boolean sortComparison(Object expected, Object actual) throws VisitorException {
        if (instanceOf(ISort.class, expected, actual)) {
            ISort expectedSort = (ISort) expected;
            ISort actualSort = (ISort) actual;
            assertEquals(expectedSort.isBool(), actualSort.isBool());
            assertEquals(expectedSort.toString(), actualSort.toString());
            return true;
        }
        return false;
    }

    private void declarationComparison(IDeclaration expected, IDeclaration actual) throws VisitorException {
        sortComparison(expected.sort(), actual.sort());
        symbolComparison(expected.parameter(), actual.parameter());
        actual.accept(new SMTLibEqualVisitor(expected));
    }

    private void qualifiedIdentifierComparison(IQualifiedIdentifier expected, IQualifiedIdentifier actual) throws VisitorException {
        assertEquals(expected.kind(), actual.kind());
        symbolComparison(expected.headSymbol(), actual.headSymbol());
    }

    private void identifierComparison(IIdentifier expected, IIdentifier actual) throws VisitorException {
        assertEquals(expected.kind(), actual.kind());
        symbolComparison(expected.headSymbol(), actual.headSymbol());
    }

    private void expressionComparison(IExpr expected, IExpr actual) throws VisitorException {
        assertEquals(expected.kind(), actual.kind());
        actual.accept(new SMTLibEqualVisitor(expected));
    }

    private void declareFunctionComparison(Ideclare_fun expected, Ideclare_fun actual) throws VisitorException {
        symbolComparison(expected.symbol(), actual.symbol());
        sortComparison(expected.resultSort(), actual.resultSort());
        assertEquals(expected.argSorts().size(), actual.argSorts().size());
        int index = 0;
        for (ISort sort : expected.argSorts()) {
            sortComparison(sort, actual.argSorts().get(index));
            index += 1;
        }
    }

    private void defineFunctionComparison(Idefine_fun expected, Idefine_fun actual) throws org.smtlib.IVisitor.VisitorException {
        symbolComparison(expected.symbol(), actual.symbol());
        sortComparison(expected.resultSort(), actual.resultSort());
        expressionComparison(expected.expression(), actual.expression());
        assertEquals(expected.parameters().size(), actual.parameters().size());
        int index = 0;
        for (IDeclaration parameter : expected.parameters()) {
            declarationComparison(parameter, actual.parameters().get(index));
            index += 1;
        }
    }

    private void assertComparison(Iassert expected, Iassert actual) throws VisitorException {
        expressionComparison(expected.expr(), actual.expr());
    }

    private boolean instanceOf(Class<?> aClass, Object... objects) {
        for (Object object : objects) {
            if (!aClass.isInstance(object)) {
                return false;
            }
        }
        return true;
    }

    private Object expected() {
        return expected;
    }

    private Logger logger() {
        return loggerFor(this);
    }

    private Object expected;
}
