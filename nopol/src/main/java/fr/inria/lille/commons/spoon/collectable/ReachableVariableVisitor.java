package fr.inria.lille.commons.spoon.collectable;

import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.hasStaticModifier;
import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.inStaticCode;
import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.isAnonymousClass;
import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.isBlock;
import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.isConstructor;
import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.isInitializationBlock;
import static fr.inria.lille.commons.spoon.util.SpoonElementLibrary.isSimpleType;

import java.util.Collection;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.CtAbstractVisitor;
import xxl.java.container.classic.MetaSet;
import fr.inria.lille.commons.spoon.filter.BeforeLocationFilter;

public class ReachableVariableVisitor extends CtAbstractVisitor {

	public ReachableVariableVisitor(CtElement startingNode) {
		this.startingNode = startingNode;
		this.beforeFilter = new BeforeLocationFilter(CtVariable.class, startingNode().getPosition());
		excludesInstanceFields = inStaticCode(startingNode());
	}
	
	public CtElement startingNode() {
		return startingNode;
	}
	
	public Collection<CtVariable<?>> reachedVariables() {
		if (reachedVariables == null) {
			reachedVariables = MetaSet.newHashSet();
			scan(startingNode());
		}
		return reachedVariables;
	}
	
	@Override
	public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
		/* this method has to be implemented by the non abstract class */
	}
	
	@Override
	public void visitCtCodeSnippetStatement(CtCodeSnippetStatement statement) {
		/* ignore any code snippet, only work with what was originally in the code */
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
			return ! (isAnonymousClass(element) || isInitializationBlock(element) || isConstructor(element) || isSimpleType(element) && isBlock(parent));
		}
		return false;
	}

	@Override
	public <R> void visitCtBlock(CtBlock<R> block) {
		scanElementsIn(block.getStatements());
	}

	@Override
	public <T> void visitCtMethod(CtMethod<T> method) {
		scanElementsIn(method.getParameters());
	}
	
	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		scanElementsIn(ctClass.getFields());
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
	
	private void scanElementsIn(Collection<? extends CtElement> elements) {
		for (CtElement element : elements) {
			super.scan(element);
		}
	}

	private boolean isReachable(CtVariable<?> variable) {
		return beforeFilter().matches(variable);
	}

	private boolean excludesInstanceFields() {
		return excludesInstanceFields;
	}
	
	private BeforeLocationFilter<CtVariable<?>> beforeFilter() {
		return beforeFilter;
	}
	
	private CtElement startingNode;
	private boolean excludesInstanceFields;
	private Collection<CtVariable<?>> reachedVariables;
	private BeforeLocationFilter<CtVariable<?>> beforeFilter;
}
