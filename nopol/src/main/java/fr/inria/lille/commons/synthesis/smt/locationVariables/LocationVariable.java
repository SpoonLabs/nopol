package fr.inria.lille.commons.synthesis.smt.locationVariables;

import static fr.inria.lille.commons.synthesis.smt.SMTLib.smtlib;

import java.util.Collection;
import java.util.List;

import org.smtlib.IExpr;
import org.smtlib.ISort;

import fr.inria.lille.commons.collections.ListLibrary;
import fr.inria.lille.commons.collections.Multimap;
import fr.inria.lille.commons.synthesis.expression.Expression;
import fr.inria.lille.commons.synthesis.expression.ObjectTemplate;
import fr.inria.lille.commons.synthesis.smt.SMTLib;


public abstract class LocationVariable<T> extends Expression<T> {

	public static Multimap<ISort, LocationVariable<?>> bySort(Collection<LocationVariable<?>> locationVariables) {
		Multimap<ISort, LocationVariable<?>> multimap = Multimap.newSetMultimap();
		for (LocationVariable<?> locationVariable : locationVariables) {
			multimap.add(sortFor(locationVariable), locationVariable);
		}
		return multimap;
	}
	
	public static ISort sortFor(LocationVariable<?> locationVariable) {
		return smtlib().sortFor(locationVariable.type());
	}

	public static List<String> expressionsOf(Collection<LocationVariable<?>> variables) {
		List<String> expressions = ListLibrary.newArrayList();
		for (LocationVariable<?> variable : variables) {
			expressions.add(variable.expression());
		}
		return expressions;
	}
	
	public static List<String> subexpressionsOf(Collection<LocationVariable<?>> variables) {
		List<String> identifiers = ListLibrary.newArrayList();
		for (LocationVariable<?> variable : variables) {
			identifiers.add(variable.subexpression());
		}
		return identifiers;
	}
	
	public LocationVariable(ObjectTemplate<T> objectTemplate, String subexpression) {
		super(objectTemplate.type(), "L@" + subexpression);
		this.subexpression = subexpression;
		this.objectTemplate = objectTemplate;
	}

	public ObjectTemplate<?> objectTemplate() {
		return objectTemplate;
	}

	public IExpr encodedLineNumber() {
		return SMTLib.smtlib().symbolFor(expression());
	}
	
	public String subexpression() {
		return subexpression;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((subexpression == null) ? 0 : subexpression.hashCode());
		result = prime * result + ((objectTemplate == null) ? 0 : objectTemplate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocationVariable<?> other = (LocationVariable<?>) obj;
		if (subexpression == null) {
			if (other.subexpression != null)
				return false;
		} else if (!subexpression.equals(other.subexpression))
			return false;
		if (objectTemplate == null) {
			if (other.objectTemplate != null)
				return false;
		} else if (!objectTemplate.equals(other.objectTemplate))
			return false;
		return true;
	}

	private String subexpression;
	private ObjectTemplate<?> objectTemplate;
}