/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package javax.ws.rs.core;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * Represents a generic message entity type {@code T}.
 *
 * Supports in-line instantiation of objects that represent generic types with
 * actual type parameters. An object that represents any parameterized type may
 * be obtained by sub-classing {@code GenericType}. Alternatively, an object
 * representing a concrete parameterized type can be created using a
 * {@link #of(Class, java.lang.reflect.Type) factory method} and manually specifying
 * the {@link #getRawType() raw class type} and the {@link #getType() actual
 * (parameterized) type}.
 *
 * <pre>
 *  GenericType&lt;List&lt;String>> stringListType = new GenericType&lt;List&lt;String>>() {};
 * </pre>
 *
 * @param <T> the generic type parameter.
 *
 * @author Jerome Dochez
 * @author Marek Potociar
 * @since 2.0
 */
public abstract class GenericType<T> {

    /**
     * Type represented by the generic type instance.
     */
    private transient Type type;
    /**
     * The actual raw parameter type.
     */
    private transient Class<T> rawType;
    /**
     * The types of the generic type parameters (if any).
     */
    private transient Type[] parameterTypes;


    /**
     * Construct a generic type instance with programmatically set values of type
     * and raw type.
     *
     * @param <T> Java type.
     *
     * @param rawType raw parameter type.
     * @param type parameter type (possibly generic).
     * @return programmatically constructed generic type instance.
     */
    public static <T> GenericType<T> of(final Class<?> rawType, final Type type) {
        return new GenericType<T>() {

            @Override
            @SuppressWarnings("unchecked")
            Class<T> _rawType() {
                return (Class<T>) rawType;
            }

            @Override
            Type _type() {
                return type;
            }
        };
    }

    /**
     * Protected constructor for a generic type of a concrete Java type.
     */
    protected GenericType() {
    }

    /*package*/ Type _type() {
        return type;
    }

    /*package*/ Class<T> _rawType() {
        return rawType;
    }

    /**
     * Retrieve the type represented by the generic type instance.
     *
     * @return the actual type represented by this generic type instance.
     */
    public final Type getType() {
        if (type == null) {
            type = _type();
            if (type == null) {
                // Get the class that directly extends GenericType<?>
                Class<?> typeLiteralSubclass = getTypeLiteralSubclass(this.getClass());

                // Get the type parameter of GenericType<T> (aka the T value)
                type = getTypeParameter(typeLiteralSubclass);
                if (type == null) {
                    throw new RuntimeException(getClass() + " does not specify the type parameter T of TypeLiteral<T>");
                }
            }
        }
        return type;
    }

    /**
     * Retrieve an array of {@link Type} objects representing the actual type
     * arguments to the type represented by this generic type instance.
     * <p/>
     * Note that in some cases, the returned array may be empty. This can occur
     * if the type represented by this generic type is a non-parameterized type.
     *
     * @return an array of {@code Type} objects representing the actual type
     *     arguments to this type.
     * @exception java.lang.TypeNotPresentException if any of the actual type arguments
     *     refers to a non-existent type declaration.
     * @exception java.lang.reflect.MalformedParameterizedTypeException if any of the
     *     actual type parameters refer to a parameterized type that cannot
     *     be instantiated for any reason.
     */
    public final Type[] getParameterTypes() {
        if (parameterTypes == null) {
            Type t = getType();
            if (t instanceof ParameterizedType) {
                parameterTypes = ((ParameterizedType) t).getActualTypeArguments();
            } else {
                parameterTypes = new Type[0];
            }
        }
        return parameterTypes;
    }

    /**
     * Returns the object representing the class or interface that declared
     * the type represented by this generic type instance.
     *
     * @return the class or interface that declared the type represented by this
     *     generic type instance.
     */
    @SuppressWarnings("unchecked")
    public final Class<T> getRawType() {
        if (rawType == null) {
            rawType = _rawType();
            if (rawType == null) {
                // Get the actual type
                Type t = getType();
                rawType = (Class<T>) getRawType(t);
            }
        }

        return rawType;
    }

    /**
     * Returns the object representing the class or interface that declared
     * the supplied {@code type}.
     *
     * @param type {@code Type} to inspect.
     * @return the class or interface that declared the supplied {@code type}.
     */
    private static Class<?> getRawType(Type type) {
        if (type instanceof Class) {

            return (Class<?>) type;

        } else if (type instanceof ParameterizedType) {

            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<?>) parameterizedType.getRawType();

        } else if (type instanceof GenericArrayType) {

            return Object[].class;

        } else if (type instanceof WildcardType) {
            return null;
        } else {
            throw new RuntimeException("Illegal type");
        }
    }

    /**
     * Return the class that directly extends the {@code GenericType<T>} class
     * in the superclass hierarchy of the supplied {@code clazz} class.
     *
     * @param clazz instance to inspect.
     * @return the direct descendant of {@code GenericType<T>} in the hierarchy
     *     of the supplied class.
     * @exception IllegalArgumentException if the provided class does not extend
     *     the {@code GenericType<T>}
     */
    private static Class<?> getTypeLiteralSubclass(Class<?> clazz) {
        // Start with a super class
        Class<?> superClass = clazz.getSuperclass();

        if (superClass.equals(GenericType.class)) {
            // Super class is GenericType, return the current class
            return clazz;
        } else if (!GenericType.class.isAssignableFrom(clazz)) {
            // The clazz IS a direct GenericType instance, we should not be here at all...
            throw new IllegalStateException("No type information has been specified for this TypeLiteral instance.");
        } else {
            // Continue processing, one level deeper
            return (getTypeLiteralSubclass(superClass));
        }
    }

    /**
     * Return the value of the type parameter of {@code GenericType<T>}.
     *
     * @param typeLiteralSubclass subClass of {@code GenericType<T>} to analyze.
     * @return the parameterized type of {@code GenericType<T>} (aka T)
     */
    private static Type getTypeParameter(Class<?> typeLiteralSubclass) {
        // Access the typeLiteral<T> super class using generics
        Type type = typeLiteralSubclass.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            // GenericType is indeed parametrized
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getActualTypeArguments().length == 1) {
                // Return the value of the type parameter (aka T)
                return parameterizedType.getActualTypeArguments()[0];
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = this == obj;
        if (!result && obj instanceof GenericType) {
            // Compare inner type for equality
            GenericType<?> that = (GenericType<?>) obj;
            return this.getType().equals(that.getType());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return getType().hashCode();
    }

    @Override
    public String toString() {
        return "GenericType{" + getType().toString() +"}";
    }
}
