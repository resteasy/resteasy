package org.jboss.resteasy.core;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.annotations.Query;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ValueInjector;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceConstructor;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.spi.util.Types;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.jboss.resteasy.spi.util.FindAnnotation.findAnnotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class InjectorFactoryImpl implements InjectorFactory
{
   public static final InjectorFactoryImpl INSTANCE = new InjectorFactoryImpl();

   @Override
   public ConstructorInjector createConstructor(Constructor constructor, ResteasyProviderFactory providerFactory)
   {
      return new ConstructorInjectorImpl(constructor, providerFactory);
   }

   @Override
   public ConstructorInjector createConstructor(ResourceConstructor constructor, ResteasyProviderFactory providerFactory)
   {
      return new ConstructorInjectorImpl(constructor, providerFactory);
   }

   @Override
   public PropertyInjector createPropertyInjector(Class resourceClass, ResteasyProviderFactory providerFactory)
   {
      return new PropertyInjectorImpl(resourceClass, providerFactory);
   }

   @Override
   public PropertyInjector createPropertyInjector(ResourceClass resourceClass, ResteasyProviderFactory providerFactory)
   {
      return new ResourcePropertyInjector(resourceClass, providerFactory);
   }

   @Override
   public MethodInjector createMethodInjector(ResourceLocator method, ResteasyProviderFactory factory)
   {
      return new MethodInjectorImpl(method, factory);
   }

   @Override
   public ValueInjector createParameterExtractor(Parameter parameter, ResteasyProviderFactory providerFactory)
   {
      return OptionalInjections.wrap(parameter.getGenericType(), innerType -> createParameterExtractor0(parameter, providerFactory, innerType));
   }

   private ValueInjector createParameterExtractor0(Parameter parameter, ResteasyProviderFactory providerFactory, Type parameterType)
   {
    Class<?> rawType = Types.getRawType(parameterType);
    switch (parameter.getParamType())
      {
         case QUERY_PARAM:
            return new QueryParamInjector(rawType, parameterType, parameter.getAccessibleObject(), parameter.getParamName(), parameter.getDefaultValue(), parameter.isEncoded(), parameter.getAnnotations(), providerFactory);
         case QUERY:
            return new QueryInjector(rawType, providerFactory);
         case HEADER_PARAM:
            return new HeaderParamInjector(rawType, parameterType, parameter.getAccessibleObject(), parameter.getParamName(), parameter.getDefaultValue(), parameter.getAnnotations(), providerFactory);
         case FORM_PARAM:
            return new FormParamInjector(rawType, parameterType, parameter.getAccessibleObject(), parameter.getParamName(), parameter.getDefaultValue(), parameter.isEncoded(), parameter.getAnnotations(), providerFactory);
         case COOKIE_PARAM:
            return new CookieParamInjector(rawType, parameterType, parameter.getAccessibleObject(), parameter.getParamName(), parameter.getDefaultValue(), parameter.getAnnotations(), providerFactory);
         case PATH_PARAM:
             return new PathParamInjector(parameter.getType(), parameter.getGenericType(), parameter.getAccessibleObject(), parameter.getParamName(), parameter.getDefaultValue(), parameter.isEncoded(), parameter.getAnnotations(), providerFactory);
          case FORM:
         {
            String prefix = parameter.getParamName();
            if (prefix.length() > 0)
            {
               if (parameterType instanceof ParameterizedType)
               {
                  ParameterizedType pType = (ParameterizedType) parameterType;
                  if (Types.isA(List.class, pType))
                  {
                     return new ListFormInjector(rawType, Types.getArgumentType(pType, 0), prefix, providerFactory);
                  }
                  if (Types.isA(Map.class, pType))
                  {
                     return new MapFormInjector(rawType, Types.getArgumentType(pType, 0), Types.getArgumentType(pType, 1), prefix, providerFactory);
                  }
               }
               return new PrefixedFormInjector(rawType, prefix, providerFactory);
            }
            return new FormInjector(rawType, providerFactory);
         }
         case BEAN_PARAM:
            return new FormInjector(rawType, providerFactory);
         case MATRIX_PARAM:
            return new MatrixParamInjector(rawType, parameterType, parameter.getAccessibleObject(), parameter.getParamName(), parameter.getDefaultValue(), parameter.isEncoded(), parameter.getAnnotations(), providerFactory);
         case CONTEXT:
            return new ContextParameterInjector(null, rawType, parameterType, parameter.getAnnotations(), providerFactory);
         case SUSPENDED:
            return new AsynchronousResponseInjector();
         case MESSAGE_BODY:
            return new MessageBodyParameterInjector(parameter.getResourceClass().getClazz(), parameter.getAccessibleObject(), rawType, parameterType, parameter.getAnnotations(), providerFactory);
         default:
            return null;
      }
   }


   @Override
   public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, String defaultName, Class type,
                                                 Type genericType, Annotation[] annotations, ResteasyProviderFactory providerFactory)
   {
      return createParameterExtractor(injectTargetClass, injectTarget, defaultName, type, genericType, annotations, true, providerFactory);
   }

   @Override
   public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, String defaultName, Class type, Type genericType, Annotation[] annotations, boolean useDefault, ResteasyProviderFactory providerFactory)
   {
      return OptionalInjections.wrap(genericType, innerType -> createParameterExtractor0(injectTargetClass, injectTarget, defaultName, genericType.equals(innerType) ? type : Types.getRawType(innerType), innerType, annotations, useDefault, providerFactory));
   }

   private ValueInjector createParameterExtractor0(Class injectTargetClass, AccessibleObject injectTarget, String defaultName, Class type, Type genericType, Annotation[] annotations, boolean useDefault, ResteasyProviderFactory providerFactory)
   {
      DefaultValue defaultValue = findAnnotation(annotations, DefaultValue.class);
      boolean encode = findAnnotation(annotations, Encoded.class) != null || injectTarget.isAnnotationPresent(Encoded.class) || type.isAnnotationPresent(Encoded.class);
      String defaultVal = null;
      if (defaultValue != null) defaultVal = defaultValue.value();

      QueryParam queryParam;
      HeaderParam header;
      MatrixParam matrix;
      PathParam uriParam;
      CookieParam cookie;
      FormParam formParam;
      Form form;

      if ((queryParam = findAnnotation(annotations, QueryParam.class)) != null)
      {
         return new QueryParamInjector(type, genericType, injectTarget, queryParam.value(), defaultVal, encode, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, org.jboss.resteasy.annotations.jaxrs.QueryParam.class) != null)
      {
         return new QueryParamInjector(type, genericType, injectTarget, defaultName, defaultVal, encode, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, Query.class) != null) {
         return new QueryInjector(type, providerFactory);
      }
      else if ((header = findAnnotation(annotations, HeaderParam.class)) != null)
      {
         return new HeaderParamInjector(type, genericType, injectTarget, header.value(), defaultVal, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, org.jboss.resteasy.annotations.jaxrs.HeaderParam.class) != null)
      {
         return new HeaderParamInjector(type, genericType, injectTarget, defaultName, defaultVal, annotations, providerFactory);
      }
      else if ((formParam = findAnnotation(annotations, FormParam.class)) != null)
      {
         return new FormParamInjector(type, genericType, injectTarget, formParam.value(), defaultVal, encode, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, org.jboss.resteasy.annotations.jaxrs.FormParam.class) != null)
      {
         return new FormParamInjector(type, genericType, injectTarget, defaultName, defaultVal, encode, annotations, providerFactory);
      }
      else if ((cookie = findAnnotation(annotations, CookieParam.class)) != null)
      {
         return new CookieParamInjector(type, genericType, injectTarget, cookie.value(), defaultVal, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, org.jboss.resteasy.annotations.jaxrs.CookieParam.class) != null)
      {
         return new CookieParamInjector(type, genericType, injectTarget, defaultName, defaultVal, annotations, providerFactory);
      }
      else if ((uriParam = findAnnotation(annotations, PathParam.class)) != null)
      {
         return new PathParamInjector(type, genericType, injectTarget, uriParam.value(), defaultVal, encode, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, org.jboss.resteasy.annotations.jaxrs.PathParam.class) != null)
      {
         return new PathParamInjector(type, genericType, injectTarget, defaultName, defaultVal, encode, annotations, providerFactory);
      }
      else if ((form = findAnnotation(annotations, Form.class)) != null)
      {
         String prefix = form.prefix();
         if (prefix.length() > 0)
         {
            if (genericType instanceof ParameterizedType)
            {
               ParameterizedType pType = (ParameterizedType) genericType;
               if (Types.isA(List.class, pType))
               {
                  return new ListFormInjector(type, Types.getArgumentType(pType, 0), prefix, providerFactory);
               }
               if (Types.isA(Map.class, pType))
               {
                  return new MapFormInjector(type, Types.getArgumentType(pType, 0), Types.getArgumentType(pType, 1), prefix, providerFactory);
               }
            }
            return new PrefixedFormInjector(type, prefix, providerFactory);
         }
         return new FormInjector(type, providerFactory);
      }
      else if (findAnnotation(annotations, BeanParam.class) != null)
      {
         return new FormInjector(type, providerFactory);
      }
      else if ((matrix = findAnnotation(annotations, MatrixParam.class)) != null)
      {
         return new MatrixParamInjector(type, genericType, injectTarget, matrix.value(), defaultVal, encode, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, org.jboss.resteasy.annotations.jaxrs.MatrixParam.class) != null)
      {
         return new MatrixParamInjector(type, genericType, injectTarget, defaultName, defaultVal, encode, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, Context.class) != null)
      {
         return new ContextParameterInjector(null, type, genericType, annotations, providerFactory);
      }
      else if (findAnnotation(annotations, Suspended.class) != null)
      {
         return new AsynchronousResponseInjector();
      }
      else if (jakarta.ws.rs.container.AsyncResponse.class.isAssignableFrom(type))
      {
         return new AsynchronousResponseInjector();
      }
      else if (useDefault)
      {
         return new MessageBodyParameterInjector(injectTargetClass, injectTarget, type, genericType, annotations, providerFactory);
      }
      else
      {
         return null;
      }
   }

    enum OptionalInjections {
        OPT     (Optional.class, OptionalInjections::getTypeArgument, Optional::empty, Optional::of),
        OINT    (OptionalInt.class, x -> Integer.class, OptionalInt::empty, v -> OptionalInt.of((Integer) v)),
        OLONG   (OptionalLong.class, x -> Long.class, OptionalLong::empty, v -> OptionalLong.of((Long) v)),
        ODOUBLE (OptionalDouble.class, x -> Double.class, OptionalDouble::empty, v -> OptionalDouble.of((Double) v)),
        ;

        static Map<Class<?>, OptionalInjections> optionalTypes;

        static {
            optionalTypes = Arrays.stream(OptionalInjections.values())
                    .collect(Collectors.toMap(o -> o.optional, Function.identity()));
        }

        final Class<?> optional;
        final Function<Type, Type> valueType;
        final Supplier<Object> empty;
        final Function<Object, Object> present;

        OptionalInjections(final Class<?> optional, final Function<Type, Type> valueType,
                           final Supplier<Object> empty, final Function<Object, Object> present)
        {
            this.optional = optional;
            this.valueType = valueType;
            this.empty = empty;
            this.present = present;
        }


        static ValueInjector wrap(Type paramType, Function<Type, ValueInjector> injectorFactory) {
            return Optional.ofNullable(optionalTypes.get(Types.getRawType(paramType)))
                    .<ValueInjector>map(oi -> {
                       ValueInjector valueInjector = injectorFactory.apply(oi.valueType.apply(paramType));
                       return valueInjector == null ? null : new DelegatingInjector(oi, valueInjector);
                    })
                    .orElseGet(() -> injectorFactory.apply(paramType));
        }

        static Type getTypeArgument(Type type) {
             if (!(type instanceof ParameterizedType))
                 throw new UnsupportedOperationException("non-parameterized Optional type: " + type);
             return ((ParameterizedType) type).getActualTypeArguments()[0];
        }

        static class DelegatingInjector implements ValueInjector {
            private final OptionalInjections oi;
            private final ValueInjector delegate;

            DelegatingInjector(final OptionalInjections oi, final ValueInjector delegate) {
                this.oi = oi;
                this.delegate = delegate;
            }

            @Override
            public Object inject(boolean unwrapAsync) {
                Object injectedValue = delegate.inject(unwrapAsync);
                if (injectedValue != null && injectedValue instanceof CompletionStage) {
                    return ((CompletionStage<Object>)injectedValue).thenApply(this::wrap);
                }
                return wrap(CompletionStageHolder.resolve(injectedValue));
            }

            @Override
            public Object inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
                Object injectedValue = delegate.inject(request, response, unwrapAsync);
                injectedValue = injectedValue;
                if (injectedValue != null && injectedValue instanceof CompletionStage) {
                    return ((CompletionStage<Object>)injectedValue).thenApply(this::wrap);
                }
                return wrap(CompletionStageHolder.resolve(injectedValue));
            }

            public Object wrap(Object value) {
                if (value == null) {
                    return oi.empty.get();
                }
                return oi.present.apply(value);
            }
        }
    }
}
