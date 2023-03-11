package org.jboss.resteasy.core;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.RuntimeDelegate;
import jakarta.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import org.jboss.resteasy.annotations.StringParameterUnmarshallerBinder;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.spi.util.Types;
import org.jboss.resteasy.util.StringToPrimitive;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @author Nicolas NESMON
 * @version $Revision: 1 $
 */
@SuppressWarnings(value = { "unchecked" })
public class StringParameterInjector {
    private static final ParamConverter<Character> characterParamConverter = new ParamConverter<Character>() {

        @Override
        public Character fromString(String value) {
            if (value != null && value.length() == 1) {
                return value.charAt(0);
            }
            return null;
        }

        @Override
        public String toString(Character value) {
            return null;
        }

    };

    private static final class UnmodifiableArrayList<E> extends ArrayList<E> {

        private static final long serialVersionUID = -4912938596876802150L;

        private UnmodifiableArrayList(final Collection<E> collection) {
            super(collection);
        }

        @Override
        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, E element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public E remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public E set(int index, E element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sort(Comparator<? super E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return new UnmodifiableArrayList<>(super.subList(fromIndex, toIndex));
        }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private final Iterator<? extends E> iterator = UnmodifiableArrayList.super.iterator();

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public E next() {
                    return iterator.next();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void forEachRemaining(Consumer<? super E> action) {
                    iterator.forEachRemaining(action);
                }
            };
        }

        @Override
        public ListIterator<E> listIterator() {
            return listIterator(0);
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return new ListIterator<E>() {
                private final ListIterator<? extends E> iterator = UnmodifiableArrayList.super.listIterator(index);

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public E next() {
                    return iterator.next();
                }

                @Override
                public boolean hasPrevious() {
                    return iterator.hasPrevious();
                }

                @Override
                public E previous() {
                    return iterator.previous();
                }

                @Override
                public int nextIndex() {
                    return iterator.nextIndex();
                }

                @Override
                public int previousIndex() {
                    return iterator.previousIndex();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void set(E e) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void add(E e) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void forEachRemaining(Consumer<? super E> action) {
                    iterator.forEachRemaining(action);
                }
            };

        }

    }

    private static final class UnmodifiableHashSet<E> extends HashSet<E> {

        private static final long serialVersionUID = 9175388977415467750L;
        private final boolean initialized;

        private UnmodifiableHashSet(final Collection<E> collection) {
            super(collection);
            this.initialized = true;
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(E e) {
            //Called by constructor
            if (initialized) {
                throw new UnsupportedOperationException();
            }
            return super.add(e);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            //Called by constructor
            if (initialized) {
                throw new UnsupportedOperationException();
            }
            return super.addAll(c);
        }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private final Iterator<? extends E> iterator = UnmodifiableHashSet.super.iterator();

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public E next() {
                    return iterator.next();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void forEachRemaining(Consumer<? super E> action) {
                    iterator.forEachRemaining(action);
                }
            };
        }

    }

    private static final class UnmodifiableTreeSet<E> extends TreeSet<E> {

        private static final long serialVersionUID = 6337958351217117300L;
        private final boolean initialized;

        private UnmodifiableTreeSet(final Collection<E> collection) {
            super(collection);
            this.initialized = true;
        }

        private UnmodifiableTreeSet(final Comparator<? super E> comparator, final Collection<E> collection) {
            super(comparator);
            addAll(collection);
            this.initialized = true;
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public E pollFirst() {
            throw new UnsupportedOperationException();
        }

        @Override
        public E pollLast() {
            throw new UnsupportedOperationException();
        }

        @Override
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            return new UnmodifiableTreeSet<>(super.subSet(fromElement, fromInclusive, toElement, toInclusive));
        }

        @Override
        public SortedSet<E> subSet(E fromElement, E toElement) {
            return new UnmodifiableTreeSet<>(super.subSet(fromElement, toElement));
        }

        @Override
        public NavigableSet<E> descendingSet() {
            NavigableSet<E> descendingSet = super.descendingSet();
            return new UnmodifiableTreeSet<>(descendingSet.comparator(), descendingSet);
        }

        @Override
        public SortedSet<E> headSet(E toElement) {
            return new UnmodifiableTreeSet<>(super.headSet(toElement));
        }

        @Override
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            return new UnmodifiableTreeSet<>(super.headSet(toElement, inclusive));
        }

        @Override
        public SortedSet<E> tailSet(E fromElement) {
            return new UnmodifiableTreeSet<>(super.tailSet(fromElement));
        }

        @Override
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            return new UnmodifiableTreeSet<>(super.tailSet(fromElement, inclusive));
        }

        @Override
        public boolean add(E e) {
            //Called by constructor
            if (initialized) {
                throw new UnsupportedOperationException();
            }
            return super.add(e);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            //Called by constructor
            if (initialized) {
                throw new UnsupportedOperationException();
            }
            return super.addAll(c);
        }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private final Iterator<? extends E> iterator = UnmodifiableTreeSet.super.iterator();

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public E next() {
                    return iterator.next();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void forEachRemaining(Consumer<? super E> action) {
                    iterator.forEachRemaining(action);
                }
            };
        }

        @Override
        public Iterator<E> descendingIterator() {
            return new Iterator<E>() {
                private final Iterator<? extends E> iterator = UnmodifiableTreeSet.super.descendingIterator();

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public E next() {
                    return iterator.next();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void forEachRemaining(Consumer<? super E> action) {
                    iterator.forEachRemaining(action);
                }
            };
        }

    }

    protected Class<?> type;
    protected Class<?> baseType;
    protected Type baseGenericType;
    protected Constructor<?> constructor;
    protected Method valueOf;
    protected String defaultValue;
    protected String paramName;
    protected Class<?> paramType;
    protected boolean isCollection;
    protected boolean isArray;
    @SuppressWarnings("rawtypes")
    protected Class<? extends Collection> collectionType;
    protected AccessibleObject target;
    protected ParamConverter<?> paramConverter;
    protected StringParameterUnmarshaller<?> unmarshaller;
    protected RuntimeDelegate.HeaderDelegate<?> delegate;

    public StringParameterInjector() {

    }

    public StringParameterInjector(final Class<?> type, final Type genericType, final String paramName,
            final Class<?> paramType, final String defaultValue, final AccessibleObject target, final Annotation[] annotations,
            final ResteasyProviderFactory factory) {
        initialize(type, genericType, paramName, paramType, defaultValue, target, annotations, factory, Collections.emptyMap());
    }

    public StringParameterInjector(final Class<?> type, final Type genericType, final String paramName,
            final Class<?> paramType, final String defaultValue, final AccessibleObject target,
            final Annotation[] annotations, final ResteasyProviderFactory factory,
            final Map<Class<? extends Annotation>, Collection<Class<?>>> ignoredTypes) {
        initialize(type, genericType, paramName, paramType, defaultValue, target, annotations, factory, ignoredTypes);
    }

    public boolean isCollectionOrArray() {
        return isCollection || isArray;
    }

    protected void initialize(Class<?> type, Type genericType, String paramName, Class<?> paramType, String defaultValue,
            AccessibleObject target, Annotation[] annotations, ResteasyProviderFactory factory) {
        initialize(type, genericType, paramName, paramType, defaultValue, target, annotations, factory, Collections.emptyMap());
    }

    protected void initialize(Class<?> type, Type genericType, String paramName, Class<?> paramType, String defaultValue,
            AccessibleObject target, Annotation[] annotations, ResteasyProviderFactory factory,
            final Map<Class<? extends Annotation>, Collection<Class<?>>> ignoredTypes) {
        this.type = type;
        this.paramName = paramName;
        this.paramType = paramType;
        this.defaultValue = defaultValue;
        this.target = target;
        baseType = type;
        baseGenericType = genericType;

        // Check if the annotation contains types to ignore. Ignored types will need to be handled in the subtype.
        if (ignoredTypes.containsKey(paramType)) {
            // Check the types which are handled elsewhere
            for (Class<?> c : ignoredTypes.get(paramType)) {
                if (c.isAssignableFrom(type)) {
                    return;
                }
            }
        }

        //Step 1: try to find a conversion mechanism using the type as it is
        if (initialize(annotations, factory)) {
            return;
        }

        //Step2: try to find a conversion mechanism if the type is an array type
        if (type.isArray()) {
            isArray = true;
            baseType = type.getComponentType();
            if (initialize(annotations, factory)) {
                return;
            }
        }

        //Step 3: try to find a conversion mechanism if the type is a collection type
        collectionType = convertParameterTypeToCollectionType();
        if (collectionType != null) {
            isCollection = true;
            if (genericType instanceof ParameterizedType) {
                ParameterizedType zType = (ParameterizedType) baseGenericType;
                baseType = Types.getRawType(zType.getActualTypeArguments()[0]);
                baseGenericType = zType.getActualTypeArguments()[0];
            } else {
                baseType = String.class;
                baseGenericType = null;
            }
            if (initialize(annotations, factory)) {
                return;
            }
        }

        throw new RuntimeException(Messages.MESSAGES.unableToFindConstructor(getParamSignature(), target, baseType.getName()));

    }

    private boolean initialize(Annotation[] annotations, ResteasyProviderFactory factory) {

        // First try to find a ParamConverter if any
        paramConverter = factory.getParamConverter(baseType, baseGenericType, annotations);
        if (paramConverter != null) {
            return true;
        }

        // Else try to find a StringParameterUnmarshaller if any
        unmarshaller = factory.createStringParameterUnmarshaller(baseType);
        if (unmarshaller != null) {
            unmarshaller.setAnnotations(annotations);
            return true;
        }
        for (Annotation annotation : annotations) {
            StringParameterUnmarshallerBinder binder = annotation.annotationType()
                    .getAnnotation(StringParameterUnmarshallerBinder.class);
            if (binder != null) {
                try {
                    unmarshaller = binder.value().newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e.getCause());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                factory.injectProperties(unmarshaller);
                unmarshaller.setAnnotations(annotations);
                return true;
            }
        }

        // Else try to find a RuntimeDelegate.HeaderDelegate if any
        if (HeaderParam.class.equals(paramType) || org.jboss.resteasy.annotations.jaxrs.HeaderParam.class.equals(paramType)) {
            delegate = factory.getHeaderDelegate(baseType);
            if (delegate != null) {
                return true;
            }
        }

        // Else try to find a public Constructor that accepts a single String argument if any
        try {
            constructor = baseType.getConstructor(String.class);
            if (!Modifier.isPublic(constructor.getModifiers())) {
                constructor = null;
            } else {
                return true;
            }
        } catch (NoSuchMethodException ignored) {

        }

        // Else try to find a public fromValue (JAXB enum) or valueOf or fromString method that accepts a single String argument if any.
        try {
            // this is for JAXB generated enums.
            Method fromValue = baseType.getDeclaredMethod("fromValue", String.class);
            if (Modifier.isPublic(fromValue.getModifiers())) {
                for (Annotation ann : baseType.getAnnotations()) {
                    if (ann.annotationType().getName().equals("jakarta.xml.bind.annotation.XmlEnum")) {
                        valueOf = fromValue;
                    }
                }
            }
        } catch (NoSuchMethodException e) {
        }
        if (StringToPrimitive.isPrimitive(baseType)) {
            return true;
        }
        if (valueOf == null) {
            Method fromString = null;

            try {
                fromString = baseType.getDeclaredMethod("fromString", String.class);
                if (Modifier.isStatic(fromString.getModifiers()) == false)
                    fromString = null;
            } catch (NoSuchMethodException ignored) {
            }
            try {
                valueOf = baseType.getDeclaredMethod("valueOf", String.class);
                if (Modifier.isStatic(valueOf.getModifiers()) == false)
                    valueOf = null;
            } catch (NoSuchMethodException ignored) {
            }
            // If enum use fromString if it exists: as defined in JAX-RS spec
            if (baseType.isEnum()) {
                if (fromString != null) {
                    valueOf = fromString;
                }
            } else if (valueOf == null) {
                valueOf = fromString;
            }
            if (valueOf == null) {
                if (Character.class.equals(baseType)) {
                    paramConverter = characterParamConverter;
                    return true;
                }
            }
        }

        return valueOf != null;
    }

    @SuppressWarnings("rawtypes")
    private Class<? extends Collection> convertParameterTypeToCollectionType() {
        if (List.class.equals(type) || ArrayList.class.equals(type)) {
            return ArrayList.class;
        } else if (SortedSet.class.equals(type) || TreeSet.class.equals(type)) {
            return TreeSet.class;
        } else if (Set.class.equals(type) || HashSet.class.equals(type)) {
            return HashSet.class;
        }
        return null;
    }

    public String getParamSignature() {
        return (paramType != null ? paramType.getName() : "") + "(\"" + paramName + "\")";
    }

    public Object extractValues(List<String> values) {
        if (values == null && (isArray || isCollection) && defaultValue != null) {
            values = new ArrayList<String>(1);
            values.add(defaultValue);
        } else if (values == null) {
            values = Collections.emptyList();
        }
        if (isArray) {
            if (values == null)
                return null;
            Object vals = Array.newInstance(type.getComponentType(), values.size());
            for (int i = 0; i < values.size(); i++)
                Array.set(vals, i, extractValue(values.get(i)));
            return vals;
        } else if (isCollection) {
            if (values == null)
                return null;
            @SuppressWarnings("rawtypes")
            Collection collection = null;
            try {
                collection = collectionType.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            for (String str : values) {
                collection.add(extractValue(str));
            }
            if (ArrayList.class.equals(collectionType)) {
                return new UnmodifiableArrayList<>(collection);
            } else if (TreeSet.class.equals(collectionType)) {
                return new UnmodifiableTreeSet<>(collection);
            } else if (HashSet.class.equals(collectionType)) {
                return new UnmodifiableHashSet<>(collection);
            }
            throw new RuntimeException("Unable to handle " + collectionType);
        } else {
            if (values == null)
                return extractValue(null);
            if (values.size() == 0)
                return extractValue(null);
            return extractValue(values.get(0));
        }

    }

    public Object extractValue(String strVal) {
        if (strVal == null) {
            if (defaultValue == null) {
                //System.out.println("NO DEFAULT VALUE");
                if (!StringToPrimitive.isPrimitive(baseType))
                    return null;
                else
                    return StringToPrimitive.stringToPrimitiveBoxType(baseType, strVal);
            } else {
                strVal = defaultValue;
                //System.out.println("DEFAULT VAULUE: " + strVal);
            }
        }
        if (paramConverter != null) {
            try {
                return paramConverter.fromString(strVal);
            } catch (WebApplicationException wae) {
                throw wae;
            } catch (Exception pce) {
                LogMessages.LOGGER.unableToExtractParameter(pce, getParamSignature(), strVal, target);
                throwProcessingException(Messages.MESSAGES.unableToExtractParameter(
                        getParamSignature(), strVal), pce);
            }
        }
        if (unmarshaller != null) {
            try {
                return unmarshaller.fromString(strVal);
            } catch (WebApplicationException wae) {
                throw wae;
            } catch (Exception ue) {
                LogMessages.LOGGER.unableToExtractParameter(ue, getParamSignature(), strVal, target);
                throwProcessingException(Messages.MESSAGES.unableToExtractParameter(
                        getParamSignature(), strVal), ue);
            }
        } else if (delegate != null) {
            try {
                return delegate.fromString(strVal);
            } catch (WebApplicationException wae) {
                throw wae;
            } catch (Exception pce) {
                LogMessages.LOGGER.unableToExtractParameter(pce, getParamSignature(), strVal, target);
                throwProcessingException(Messages.MESSAGES.unableToExtractParameter(
                        getParamSignature(), strVal), pce);
            }
        } else if (constructor != null) {
            try {
                return constructor.newInstance(strVal);
            } catch (InstantiationException e) {
                LogMessages.LOGGER.unableToExtractParameter(e, getParamSignature(), strVal, target);
                throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), _encode(strVal)), e);
            } catch (IllegalAccessException e) {
                LogMessages.LOGGER.unableToExtractParameter(e, getParamSignature(), strVal, target);
                throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), _encode(strVal)), e);
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                if (targetException instanceof WebApplicationException) {
                    throw ((WebApplicationException) targetException);
                }
                LogMessages.LOGGER.unableToExtractParameter(e, getParamSignature(), strVal, target);
                throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), _encode(strVal)),
                        targetException);
            }
        } else if (valueOf != null) {
            try {
                return valueOf.invoke(null, strVal);
            } catch (IllegalAccessException e) {
                LogMessages.LOGGER.unableToExtractParameter(e, getParamSignature(), strVal, target);
                throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), _encode(strVal)), e);
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                if (targetException instanceof WebApplicationException) {
                    throw ((WebApplicationException) targetException);
                }
                LogMessages.LOGGER.unableToExtractParameter(targetException, getParamSignature(), strVal, target);
                throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), _encode(strVal)),
                        targetException);
            }
        }
        try {
            if (StringToPrimitive.isPrimitive(baseType))
                return StringToPrimitive.stringToPrimitiveBoxType(baseType, strVal);
        } catch (Exception e) {
            LogMessages.LOGGER.unableToExtractParameter(e, getParamSignature(), strVal, target);
            throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), _encode(strVal)), e);
        }
        return null;
    }

    private String _encode(String strVal) {
        try {
            return URLEncoder.encode(strVal, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public ParamConverter<?> getParamConverter() {
        return paramConverter;
    }

    public HeaderDelegate<?> getHeaderDelegate() {
        return delegate;
    }

    protected void throwProcessingException(String message, Throwable cause) {
        throw new BadRequestException(message, cause);
    }
}
