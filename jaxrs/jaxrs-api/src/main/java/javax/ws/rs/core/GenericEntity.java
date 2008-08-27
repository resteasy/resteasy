package javax.ws.rs.core;

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.php
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.ws.rs.ext.MessageBodyWriter;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Represents a response entity of a generic type {@code T}.
 * <p/>
 * <p>Normally type erasure removes generic type information such that a
 * {@link Response} instance that contains, e.g., an entity of type
 * {@code List<String>} appears to contain a raw {@code List<?>} at runtime.
 * When the generic type is required to select a suitable
 * {@link MessageBodyWriter}, this class may be used to wrap the entity and
 * capture its generic type.</p>
 * <p/>
 * <p>There are two ways to create an instance:</p>
 * <ol>
 * <li>Create a (typically anonymous) subclass of this
 * class which enables retrieval of the type information at runtime despite
 * type erasure. For example, the following code shows how to create a
 * {@link Response} containing an entity of type {@code List<String>} whose
 * generic type will be available at runtime for selection of a suitable
 * {@link MessageBodyWriter}:
 * <p/>
 * <pre>List&lt;String&gt; list = new ArrayList&lt;String&gt;();
 * GenericEntity&lt;List&lt;String&gt;&gt; entity = new GenericEntity&lt;List&lt;String&gt;&gt;(list) {};
 * Response response = Response.ok(entity).build();</pre>
 * <p/>
 * <p>where <code>list</code> is the instance of <code>List&lt;String&gt</code>
 * that will form the response body and entity is an instance of an anonymous
 * subclass of {@code GenericEntity}.</p></li>
 * <li>Create an instance directly by supplying the generic type information
 * with the entity. For example the following code shows how to create
 * a response containing the result of a method invoked via reflection:
 * <pre>Method method = ...;
 * GenericEntity&lt;Object&gt; entity = new GenericEntity&lt;Object&gt;(
 *    method.invoke(...), method.getGenericReturnType());
 * Response response = Response.ok(entity).build();</pre></li>
 * <p>The above obtains the generic type from the return type of the method,
 * the raw type is the class of entity.</p>
 * </ol>
 */
public class GenericEntity<T>
{

   final Class<?> rawType;
   final Type type;
   final T entity;

   /**
    * Constructs a new generic entity. Derives represented class from type
    * parameter. Note that this constructor is protected, users should create
    * a (usually anonymous) subclass as shown above.
    *
    * @param entity the entity instance, must not be null
    * @throws IllegalArgumentException if entity is null
    */
   protected GenericEntity(T entity)
   {
      if (entity == null)
      {
         throw new IllegalArgumentException("The entity must not be null");
      }
      this.entity = entity;
      this.type = getSuperclassTypeParameter(getClass());
      this.rawType = entity.getClass();
   }

   /**
    * Create a new instance of GenericEntity, supplying the generic type
    * information. The entity must be assignable to a variable of the
    * supplied generic type, e.g. if {@code entity} is an instance of
    * {@code ArrayList<String>} then {@code genericType} could
    * be the same or a superclass of {@code ArrayList} with the same generic
    * type like {@code List<String>}.
    *
    * @param entity      the entity instance, must not be null
    * @param genericType the generic type, must not be null
    * @throws IllegalArgumentException if the entity is not assignable to
    *                                  a variable of the supplied generic type or if entity or genericType
    *                                  is null.
    */
   public GenericEntity(T entity, Type genericType)
   {
      if (entity == null || genericType == null)
      {
         throw new IllegalArgumentException("Arguments must not be null");
      }
      this.entity = entity;
      this.rawType = entity.getClass();
      checkTypeCompatibility(this.rawType, genericType);
      this.type = genericType;
   }

   private void checkTypeCompatibility(Class<?> c, Type t)
   {
      if (t instanceof Class)
      {
         Class<?> ct = (Class<?>) t;
         if (ct.isAssignableFrom(c))
            return;
      }
      else if (t instanceof ParameterizedType)
      {
         ParameterizedType pt = (ParameterizedType) t;
         Type rt = pt.getRawType();
         checkTypeCompatibility(c, rt);
         return;
      }
      else if (c.isArray() && (t instanceof GenericArrayType))
      {
         GenericArrayType at = (GenericArrayType) t;
         Type rt = at.getGenericComponentType();
         checkTypeCompatibility(c.getComponentType(), rt);
         return;
      }
      throw new IllegalArgumentException("The type is incompatible with the class of the entity");
   }

   /**
    * Returns the type from super class's type parameter.
    */
   private static Type getSuperclassTypeParameter(Class<?> subclass)
   {
      Type superclass = subclass.getGenericSuperclass();
      if (!(superclass instanceof ParameterizedType))
      {
         throw new RuntimeException("Missing type parameter.");
      }
      ParameterizedType parameterized = (ParameterizedType) superclass;
      return parameterized.getActualTypeArguments()[0];
   }

   /**
    * Gets the raw type of the enclosed entity. Note that this is the raw type of
    * the instance, not the raw type of the type parameter. I.e. in the example
    * in the introduction, the raw type is {@code ArrayList} not {@code List}.
    *
    * @return the raw type
    */
   public final Class<?> getRawType()
   {
      return rawType;
   }

   /**
    * Gets underlying {@code Type} instance. Note that this is derived from the
    * type parameter, not the enclosed instance. I.e. in the example
    * in the introduction, the type is {@code List<String>} not
    * {@code ArrayList<String>}.
    *
    * @return the type
    */
   public final Type getType()
   {
      return type;
   }

   /**
    * Get the enclosed entity
    *
    * @return the enclosed entity
    */
   public final T getEntity()
   {
      return entity;
   }
}