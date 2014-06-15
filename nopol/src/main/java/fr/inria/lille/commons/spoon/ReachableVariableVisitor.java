package fr.inria.lille.commons.spoon;

import java.util.Collection;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.CtAbstractVisitor;
import fr.inria.lille.commons.collections.SetLibrary;

public class ReachableVariableVisitor extends CtAbstractVisitor {

	public ReachableVariableVisitor(CtElement startingNode) {
		this.startingNode = startingNode;
		this.beforeFilter = new BeforeLocationFilter(CtVariable.class, startingNode().getPosition());
		excludesInstanceFields = SpoonLibrary.inStaticCode(startingNode());
	}
	
	public CtElement startingNode() {
		return startingNode;
	}
	
	public Collection<CtVariable<?>> reachedVariables() {
		if (reachedVariables == null) {
			reachedVariables = SetLibrary.newHashSet();
			scan(startingNode());
		}
		return reachedVariables;
	}
	
	@Override
	public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
		/* this method has to be implemented by the non abstract class */
	}
	
	@Override
	public void scan(CtElement element) {
		super.scan(element);
		if (hasSafelyReachableParent(element)) {
			scan(element.getParent());
		}
	}
	
	private boolean hasSafelyReachableParent(CtElement element) {
		CtElement parent = element.getParent();
		if (parent != null) {
			return ! (isAnonymousClass(element) || isInitializationBlock(element) || isConstructor(element) || isAType(element) && isBlock(parent));
		}
		return false;
	}

	@Override
	public <R> void visitCtBlock(CtBlock<R> block) {
		scanElementsIn((Collection) block.getStatements());
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> method) {
		scanElementsIn((Collection) method.getParameters());
	}
	
	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		scanElementsIn((Collection) ctClass.getFields());
	}

	@Override
	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
		if (isReachable(localVariable)) {
			reachedVariables().add(localVariable);
		}
	}
	
	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {
		reachedVariables().add(parameter);
	}
	
	@Override
	public <T> void visitCtField(CtField<T> field) {
		if (! excludesInstanceFields() || hasStaticModifier(field)) {
			reachedVariables().add(field);
		}
	}
	
	private void scanElementsIn(Collection<CtElement> elements) {
		for (CtElement element : elements) {
			super.scan(element);
		}
	}
	
	private boolean isAnonymousClass(CtElement element) {
		return SpoonLibrary.isAnonymousClass(element);
	}

	private boolean isInitializationBlock(CtElement element) {
		return SpoonLibrary.isInitializationBlock(element);
	}
	
	private boolean isAType(CtElement element) {
		return SpoonLibrary.isAType(element);
	}
	
	private boolean isConstructor(CtElement element) {
		return SpoonLibrary.isConstructor(element);
	}
	
	private boolean isBlock(CtElement element) {
		return SpoonLibrary.isBlock(element);
	}
	
	private boolean isReachable(CtVariable<?> variable) {
		return beforeFilter().matches(variable);
	}
	
	private boolean hasStaticModifier(CtElement element) {
		return SpoonLibrary.hasStaticModifier(element);
	}

	private boolean excludesInstanceFields() {
		return excludesInstanceFields;
	}
	
	private BeforeLocationFilter<CtVariable<?>> beforeFilter() {
		return beforeFilter;
	}
	
	private BeforeLocationFilter<CtVariable<?>> beforeFilter;
	private CtElement startingNode;
	private boolean excludesInstanceFields;
	private Collection<CtVariable<?>> reachedVariables;
}
