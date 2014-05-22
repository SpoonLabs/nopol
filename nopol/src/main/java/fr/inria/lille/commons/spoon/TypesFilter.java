package fr.inria.lille.commons.spoon;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.CompositeFilter;
import spoon.reflect.visitor.filter.FilteringOperator;
import spoon.reflect.visitor.filter.TypeFilter;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.lille.commons.collections.CollectionLibrary;
import fr.inria.lille.commons.collections.SetLibrary;

public class TypesFilter {

	public TypesFilter(File sourceFolder, Collection<Class<CtElement>> types) {
		compiler = spoonCompilerFor(sourceFolder);
		typesFilter = compoundFilterFrom(types);
	}
	
	protected SpoonCompiler spoonCompilerFor(File sourceFolder) {
		SpoonCompiler compiler = null;
		try {
			compiler = new Launcher().createCompiler();
			compiler.addInputSource(sourceFolder);
			compiler.build();
		} catch (JSAPException jsape) {
			jsape.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return compiler;
	}

	protected CompositeFilter compoundFilterFrom(Collection<Class<CtElement>> types) {
		Collection<Filter> typeFilters = SetLibrary.newHashSet();
		for (Class aClass : types) {
			typeFilters.add(new TypeFilter(aClass));
		}
		return new CompositeFilter(FilteringOperator.UNION, CollectionLibrary.toArray(Filter.class, typeFilters));
	}

	public Collection<CtElement> matching() {
		return Query.getElements(spoonCompiler().getFactory(), typesFilter());
	}

	private Filter<CtElement> typesFilter() {
		return typesFilter;
	}

	private SpoonCompiler spoonCompiler() {
		return compiler;
	}

	private SpoonCompiler compiler;
	private CompositeFilter typesFilter;
}
