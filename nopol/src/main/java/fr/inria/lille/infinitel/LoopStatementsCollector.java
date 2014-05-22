package fr.inria.lille.infinitel;

import java.io.File;
import java.util.Collection;

import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.code.CtWhileImpl;
import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.spoon.TypesFilter;

public class LoopStatementsCollector {

	public LoopStatementsCollector(File sourceFolder) {
		typesFilter = new TypesFilter(sourceFolder, spoonTargetClasses());
	}
	
	public Collection<Class<CtElement>> spoonTargetClasses() { 
		return (Collection) ListLibrary.newArrayList(CtWhileImpl.class);
	}
	
	public Collection<CtElement> statements() {
		return typesFilter().matching();
	}
	
	public TypesFilter typesFilter() {
		return typesFilter;
	}
	
	TypesFilter typesFilter;
}
