package fr.inria.lille.commons.spoon;

import java.util.Collection;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.visitor.CtAbstractVisitor;
import fr.inria.lille.commons.collections.SetLibrary;

public class ReachableVariableVisitor extends CtAbstractVisitor {

	public ReachableVariableVisitor(CtElement startingNode) {
		this.startingNode = startingNode;
		reachedVariables = SetLibrary.newHashSet();
		excludesInstanceFields = SpoonLibrary.inStaticCode(startingNode());
	}
	
	public CtElement startingNode() {
		return startingNode;
	}
	
	public Collection<CtVariable<?>> reachedVariables() {
		return reachedVariables;
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
	public <R> void visitCtBlock(final CtBlock<R> block) {
		CtVariable<?> variable;
		for (CtStatement statement : block.getStatements()) {
			if (! isReachable(statement.getPosition())) {
				break;
			}
			if (isLocalVariable(statement)) {
				variable = (CtVariable<?>) statement;
				reachedVariables().add(variable);
			}
		}
	}

	@Override
	public <T> void visitCtClass(final CtClass<T> ctClass) {
		for (CtField<?> field : ctClass.getFields()) {
			if (! excludesInstanceFields() || hasStaticModifier(field)) {
				reachedVariables().add(field);
			}
		}
	}

	@Override
	public <T> void visitCtMethod(final CtMethod<T> method) {
		reachedVariables().addAll(method.getParameters());
	}
	
	@Override
	public <T> void visitCtThisAccess(CtThisAccess<T> thisAccess) {
		/* this method has to be implemented by the non abstract class */
	}
	
	private boolean isAnonymousClass(CtElement element) {
		return SpoonLibrary.isAnonymousClass(element);
	}
	
	private boolean isLocalVariable(CtElement element) {
		return SpoonLibrary.isLocalVariable(element);
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
	
	private boolean isReachable(SourcePosition position) {
		return SpoonLibrary.appearsBefore(position, startingNode().getPosition());
	}
	
	private boolean hasStaticModifier(CtElement element) {
		return SpoonLibrary.hasStaticModifier(element);
	}

	private boolean excludesInstanceFields() {
		return excludesInstanceFields;
	}
	
	private CtElement startingNode;
	private boolean excludesInstanceFields;
	private Collection<CtVariable<?>> reachedVariables;
}
