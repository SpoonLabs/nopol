package fr.inria.lille.spirals.repair.expression;


import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Type;
import fr.inria.lille.spirals.repair.commons.Candidates;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * is the generic type of an expression
 */
public abstract class ExpressionImpl implements Expression {
    private double priority;
    
    /**
     *  for primitive expressions, a com.sun.PrimitiveType or an Integer/Boolean
     */
    private Object value;
    private Class returnType;
    private List<Expression> alternatives;
    private List<Expression> inAlternativesOf;
    private List<Expression> inExpressions;

    /**
     *
     */
    public ExpressionImpl(Object value, Class returnType) {
        setValue(value);
        this.returnType = returnType;
        this.alternatives = new ArrayList<>();
        this.inAlternativesOf = new ArrayList<>();
        this.inExpressions = new ArrayList<>();
        this.priority = 1;
    }

    /**
     *
     */
    @Override
    public Object getValue() {
        return this.value;
    }

    protected void setValue(Object value) {
    	if (value instanceof ReferenceType) {
    		throw new IllegalArgumentException();
    	}
        this.value = value;
    }

    /**
     *
     */
    @Override
    public Class getType() {
        return returnType;
    }

    protected void setType(Class returnType) {
        this.returnType = returnType;
    }

    /**
     *
     */
    @Override
    public List<Expression> getAlternatives() {
        return alternatives;
    }

    @Override
    public List<Expression> getInAlternativesOf() {
        return inAlternativesOf;
    }

    public List<Expression> getInExpressions() {
        return inExpressions;
    }

    private Class<?> getClass(String typeName) {
        if (typeName.equals("byte"))
            return Byte.class;
        if (typeName.equals("short"))
            return Short.class;
        if (typeName.equals("int"))
            return Integer.class;
        if (typeName.equals("long"))
            return Long.class;
        if (typeName.equals("char"))
            return Character.class;
        if (typeName.equals("float"))
            return Float.class;
        if (typeName.equals("double"))
            return Double.class;
        if (typeName.equals("boolean"))
            return Boolean.class;
        if (typeName.equals("void"))
            return void.class;
        try {
            return Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found : " + typeName);
        }
    }

    /** if the value of this expression compatible with type refAss? */
    @Override
    public boolean isAssignableTo(Type refAss) {
		try {
			ReferenceType ref;

            if(this instanceof PrimitiveTypeExpression && !(refAss instanceof PrimitiveType)) {
                return false;
            }
            if(this instanceof ComplexTypeExpression && (refAss instanceof PrimitiveType)) {
                return false;
            }

			if (getValue() == null) {
				// this should result in never passing null as method parameters
				return false;
			}
			// either it is a debug object
			else if (this.getValue() instanceof com.sun.jdi.Value) {
				// big hack
				// isAssignableTo is not in the API, so we have to set it accessible
				ref = ((ObjectReference) this.getValue()).referenceType();
				java.lang.reflect.Method isAssignableTo = ref.getClass()
						.getDeclaredMethod("isAssignableTo",
								ReferenceType.class);
				isAssignableTo.setAccessible(true);
                try {
                    return (boolean) isAssignableTo.invoke(ref, refAss);
                } catch (IllegalArgumentException e) {
                    // the two types are not compatible
                    return false;
                }
			}
			// or it is an actual value, a real object
			else {
				// classical isAssignableFrom
				// constants are never handled in isAssignableTo
				return getClass(refAss.name()).isAssignableFrom(
						value.getClass());
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	
	}

    @Override
    public double getPriority() {
        return priority;
    }

    @Override
    public void setPriority(double priority) {
        this.priority = priority;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Expression)) return false;
        if (o instanceof Constant && !(this instanceof Constant)) return false;
        if (this instanceof Constant && !(o instanceof Constant)) return false;

        Expression that = (Expression) o;

        if (returnType != null ? !returnType.equals(that.getType()) : that.getType() != null) return false;
        if (!sameExpression(that)) { return false; }
        return !(value != null ? !value.equals(that.getValue()) : that.getValue() != null);

    }

    public boolean sameExpression(Expression e) {
        return this.toString().equals(e.toString());
    }

    @Override
    public int compareTo(Expression o) {
        return (int) (Math.round(100 * this.getWeight()) - Math.round(100 * o.getWeight()));
    }

    @Override
    public Object evaluate(Candidates values) {
        for (int i = 0; i < values.size(); i++) {
            /*ExpressionImpl expression = (ExpressionImpl) values.get(i);
            if (expression.sameExpression(this)) {
                return expression.getValue();
            }
            for (int j = 0; j < expression.getAlternatives().size(); j++) {
                ExpressionImpl expression1 = (ExpressionImpl) expression.getAlternatives().get(j);
                if (expression1.sameExpression(this)) {
                    return expression1.getValue();
                }
            }*/
        }
        throw new RuntimeException("Expression not found");
    }

    @Override
    public String asPatch() {
        return this.toString();
    }

    public byte[] getMD5() {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(this.toString().getBytes());
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

