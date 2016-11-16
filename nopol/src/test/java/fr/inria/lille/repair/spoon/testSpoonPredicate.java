package fr.inria.lille.repair.spoon;

import fr.inria.lille.repair.nopol.spoon.SpoonPredicate;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.TypeFilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by bdanglot on 11/14/16.
 */
public class testSpoonPredicate {

	@Test
	public void testStatementPredicate() throws Exception {

		/* test the filter on statement of the SpoonPredicate.canBeRepairedByAddingPrecondition
			it must return true in case we can try to fix at the given statement, false otherwise.
		*/

		Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/resources/spoon/example/Arabidopsis.java");
		launcher.buildModel();

		final CtClass<Object> arabidopsis = launcher.getFactory().Class().get("Arabidopsis");
		final CtInvocation invocationOfIsAGreatClass = arabidopsis.getElements(new TypeFilter<CtInvocation>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation element) {
				return "isAGreatClass()".equals(element.toString());
			}
		}).get(0);

		final CtInvocation invocationOfSuper = arabidopsis.getElements(new TypeFilter<CtInvocation>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation element) {
				return "super()".equals(element.toString());
			}
		}).get(0);

		final CtFor forLoop = arabidopsis.getElements(new TypeFilter<CtFor>(CtFor.class) {
			@Override
			public boolean matches(CtFor element) {
				return true;//there is only 1 for loop in Arabidopsis;
			}
		}).get(0);

		final CtReturn returnTrue = arabidopsis.getElements(new TypeFilter<CtReturn>(CtReturn.class) {
			@Override
			public boolean matches(CtReturn element) {
				return true;
			}
		}).get(0);

		assertFalse(SpoonPredicate.canBeRepairedByAddingPrecondition(forLoop.getExpression()));
		assertFalse(SpoonPredicate.canBeRepairedByAddingPrecondition(forLoop.getForUpdate().get(0)));
		assertFalse(SpoonPredicate.canBeRepairedByAddingPrecondition(forLoop.getForInit().get(0)));
		assertFalse(SpoonPredicate.canBeRepairedByAddingPrecondition(invocationOfIsAGreatClass));
		assertFalse(SpoonPredicate.canBeRepairedByAddingPrecondition(invocationOfSuper));
		assertFalse(SpoonPredicate.canBeRepairedByAddingPrecondition(returnTrue));

		assertTrue(SpoonPredicate.canBeRepairedByAddingPrecondition(invocationOfIsAGreatClass.getParent()));
	}
}
