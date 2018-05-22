package org.jboss.resteasy.util;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Type conversions and generic type manipulations
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Types
{
   /**
    * Is the genericType of a certain class?
    * @param clazz class
    * @param pType type
    * @return boolean result
    */
   @SuppressWarnings(value = "unchecked")
   public static boolean isA(Class clazz, ParameterizedType pType)
   {
      return clazz.isAssignableFrom((Class) pType.getRawType());
   }

   /**
    * Gets the index-th type argument.
    * @param pType type
    * @param index index
    * @return the type of argument at index position
    */
   public static Class getArgumentType(ParameterizedType pType, int index)
   {
      return (Class) pType.getActualTypeArguments()[index];
   }

   public static Class getTemplateParameterOfInterface(Class base, Class desiredInterface)
   {
      Object rtn = searchForInterfaceTemplateParameter(base, desiredInterface);
      if (rtn != null && rtn instanceof Class) return (Class) rtn;
      return null;
   }


   private static Object searchForInterfaceTemplateParameter(Class base, Class desiredInterface)
   {
      for (int i = 0; i < base.getInterfaces().length; i++)
      {
         Class intf = base.getInterfaces()[i];
         if (intf.equals(desiredInterface))
         {
            Type generic = base.getGenericInterfaces()[i];
            if (generic instanceof ParameterizedType)
            {
               ParameterizedType p = (ParameterizedType) generic;
               Type type = p.getActualTypeArguments()[0];
               Class rtn = getRawTypeNoException(type);
               if (rtn != null) return rtn;
               return type;
            }
            else
            {
               return null;
            }
         }
      }
      if (base.getSuperclass() == null || base.getSuperclass().equals(Object.class)) return null;
      Object rtn = searchForInterfaceTemplateParameter(base.getSuperclass(), desiredInterface);
      if (rtn == null || rtn instanceof Class) return rtn;
      if (!(rtn instanceof TypeVariable)) return null;

      String name = ((TypeVariable) rtn).getName();
      int index = -1;
      TypeVariable[] variables = base.getSuperclass().getTypeParameters();
      if (variables == null || variables.length < 1) return null;

      for (int i = 0; i < variables.length; i++)
      {
         if (variables[i].getName().equals(name)) index = i;
      }
      if (index == -1) return null;


      Type genericSuperclass = base.getGenericSuperclass();
      if (!(genericSuperclass instanceof ParameterizedType)) return null;

      ParameterizedType pt = (ParameterizedType) genericSuperclass;
      Type type = pt.getActualTypeArguments()[index];

      Class clazz = getRawTypeNoException(type);
      if (clazz != null) return clazz;
      return type;
   }


   /**
    * See if the two methods are compatible, that is they have the same relative signature.
    *
    * @param method first method
    * @param intfMethod second method
    * @return boolean result
    */
   @SuppressWarnings(value = "unchecked")
   public static boolean isCompatible(Method method, Method intfMethod)
   {
      if (method == intfMethod) return true;

      if (!method.getName().equals(intfMethod.getName())) return false;
      if (method.getParameterTypes().length != intfMethod.getParameterTypes().length) return false;

      for (int i = 0; i < method.getParameterTypes().length; i++)
      {
         Class rootParam = method.getParameterTypes()[i];
         Class intfParam = intfMethod.getParameterTypes()[i];
         if (!intfParam.isAssignableFrom(rootParam)) return false;
      }
      return true;
   }

   /**
    * Given a method and a root class, find the actual method declared in the root that implements the method.
    *
    * @param clazz class
    * @param intfMethod method
    * @return method in the root class
    */
   @SuppressWarnings(value = "unchecked")
   public static Method getImplementingMethod(Class clazz, Method intfMethod)
   {
      Class<?> declaringClass = intfMethod.getDeclaringClass();
      if (declaringClass.equals(clazz)) return intfMethod;

      Class[] paramTypes = intfMethod.getParameterTypes();

      if (declaringClass.getTypeParameters().length > 0 && paramTypes.length > 0)
      {
         Type[] intfTypes = findParameterizedTypes(clazz, declaringClass);
         Map<String, Type> typeVarMap = new HashMap<String, Type>();
         TypeVariable<? extends Class<?>>[] vars = declaringClass.getTypeParameters();
         for (int i = 0; i < vars.length; i++)
         {
            if (intfTypes != null && i < intfTypes.length)
            {
               typeVarMap.put(vars[i].getName(), intfTypes[i]);
            }
            else
            {
               // Interface type parameters may not have been filled out
               typeVarMap.put(vars[i].getName(), vars[i].getGenericDeclaration());
            }
         }
         Type[] paramGenericTypes = intfMethod.getGenericParameterTypes();
         paramTypes = new Class[paramTypes.length];

         for (int i = 0; i < paramTypes.length; i++)
         {
            if (paramGenericTypes[i] instanceof TypeVariable)
            {
               TypeVariable tv = (TypeVariable)paramGenericTypes[i];
               Type t = typeVarMap.get(tv.getName());
               if (t == null)
               {
                  throw new RuntimeException(Messages.MESSAGES.unableToResolveTypeVariable());
               }
               paramTypes[i] = getRawType(t);
            }
            else
            {
               paramTypes[i] = getRawType(paramGenericTypes[i]);
            }
         }

      }

      try
      {
         return clazz.getMethod(intfMethod.getName(), paramTypes);
      }
      catch (NoSuchMethodException e)
      {
      }

      try
      {
         Method tmp = clazz.getMethod(intfMethod.getName(), intfMethod.getParameterTypes());
         return tmp;
      }
      catch (NoSuchMethodException e)
      {

      }
      return intfMethod;
   }

   /**
    * Find the by a method implementation implemented interface method.
    *
    * @param root The root class of the class hierarchy.
    * @param iface The containing interface of the implemented method in the class hierarchy.
    * @param implementation The method implementation.
    * @return The implemented method.
    */
   public static Method getImplementedInterfaceMethod(final Class<?> root, final Class<?> iface, final Method implementation)
   {
      Method overriddenInterfaceMethod = findOverriddenMethod(root, iface, implementation);
      if (overriddenInterfaceMethod != null) {
         return overriddenInterfaceMethod;
      }

      for (Class<?> extended : iface.getInterfaces()) {
         overriddenInterfaceMethod = getImplementedInterfaceMethod(root, extended, implementation);

         if (overriddenInterfaceMethod != null) {
            return overriddenInterfaceMethod;
         }
      }

      return null;
   }

   /**
    * Find the by a method implementation overridden superclass method.
    *
    * @param root The root class of the class hierarchy.
    * @param superClass The containing class of the overridden method in the class hierarchy.
    * @param implementation The method implementation.
    * @return The overridden method.
    */
   public static Method findOverriddenMethod(final Class<?> root, final Class<?> superClass, final Method implementation)
   {
      // Check if the overridden method exists without generics
      try
      {
         final Method method = superClass.getDeclaredMethod(implementation.getName(), implementation.getParameterTypes());
         return method;
      }
      catch (NoSuchMethodException e)
      {
         // ignore
      }

      // Check if the overridden method exists with generics
      final Map<TypeVariable<?>, Type> genericTypeMap = populateParameterizedMap(root, superClass);

      for (Method superClassMethod : superClass.getDeclaredMethods())
      {
         if (superClassMethod.isSynthetic())
         {
            continue;
         }
         if (!implementation.getName().equals(superClassMethod.getName()) || implementation.getParameterCount() != superClassMethod.getParameterCount())
         {
            continue;
         }

         final Type[] actualMethodParameters = extractTypeVariables(genericTypeMap, superClassMethod.getGenericParameterTypes());
         if (Arrays.equals(implementation.getGenericParameterTypes(), actualMethodParameters))
         {
            return superClassMethod;
         }
      }

      return null;
   }

   public static Class<?> getRawType(Type type)
   {
      if (type instanceof Class<?>)
      {
         // type is a normal class.
         return (Class<?>) type;

      }
      else if (type instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) type;
         Type rawType = parameterizedType.getRawType();
         return (Class<?>) rawType;
      }
      else if (type instanceof GenericArrayType)
      {
         final GenericArrayType genericArrayType = (GenericArrayType) type;
         final Class<?> componentRawType = getRawType(genericArrayType.getGenericComponentType());
         return Array.newInstance(componentRawType, 0).getClass();
      }
      else if (type instanceof TypeVariable)
      {
         final TypeVariable typeVar = (TypeVariable) type;
         if (typeVar.getBounds() != null && typeVar.getBounds().length > 0)
         {
            return getRawType(typeVar.getBounds()[0]);
         }
      }
      else if (type instanceof WildcardType)
      {
          WildcardType wildcardType = (WildcardType) type;
          Type[] upperBounds = wildcardType.getUpperBounds();
          if (upperBounds != null && upperBounds.length > 0)
          {
              return getRawType(upperBounds[0]);
          }
      }
      throw new RuntimeException(Messages.MESSAGES.unableToDetermineBaseClass());
   }


   public static Class<?> getRawTypeNoException(Type type)
   {
      if (type instanceof Class<?>)
      {
         // type is a normal class.
         return (Class<?>) type;

      }
      else if (type instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) type;
         Type rawType = parameterizedType.getRawType();
         return (Class<?>) rawType;
      }
      else if (type instanceof GenericArrayType)
      {
         final GenericArrayType genericArrayType = (GenericArrayType) type;
         final Class<?> componentRawType = getRawType(genericArrayType.getGenericComponentType());
         return Array.newInstance(componentRawType, 0).getClass();
      }
      return null;
   }

   /**
    * Returns the type argument from a parameterized type.
    *
    * @param genericType type
    * @return null if there is no type parameter
    */
   public static Class<?> getTypeArgument(Type genericType)
   {
      if (!(genericType instanceof ParameterizedType)) return null;
      ParameterizedType parameterizedType = (ParameterizedType) genericType;
      Class<?> typeArg = (Class<?>) parameterizedType.getActualTypeArguments()[0];
      return typeArg;
   }


   public static Class getCollectionBaseType(Class type, Type genericType)
   {
      if (genericType instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) genericType;
         Type componentGenericType = parameterizedType.getActualTypeArguments()[0];
         return getRawType(componentGenericType);
      }
      else if (genericType instanceof GenericArrayType)
      {
         final GenericArrayType genericArrayType = (GenericArrayType) genericType;
         Type componentGenericType = genericArrayType.getGenericComponentType();
         return getRawType(componentGenericType);
      }
      else if (type.isArray())
      {
         return type.getComponentType();
      }
      return null;
   }


   public static Class getMapKeyType(Type genericType)
   {
      if (genericType instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) genericType;
         Type componentGenericType = parameterizedType.getActualTypeArguments()[0];
         return getRawType(componentGenericType);
      }
      return null;
   }

   public static Class getMapValueType(Type genericType)
   {
      if (genericType instanceof ParameterizedType)
      {
         ParameterizedType parameterizedType = (ParameterizedType) genericType;
         Type componentGenericType = parameterizedType.getActualTypeArguments()[1];
         return getRawType(componentGenericType);
      }
      return null;
   }

   public static Type resolveTypeVariables(Class<?> root, Type type)
   {
      if (type instanceof TypeVariable)
      {
         Type newType = resolveTypeVariable(root, (TypeVariable)type);
         return (newType == null) ? type : newType;
      }
      else if (type instanceof ParameterizedType)
      {
         final ParameterizedType param = (ParameterizedType)type;
         final Type[] actuals = new Type[param.getActualTypeArguments().length];
         for (int i = 0; i < actuals.length; i++)
         {
            Type newType = resolveTypeVariables(root, param.getActualTypeArguments()[i]);
            actuals[i] = newType == null ? param.getActualTypeArguments()[i] : newType;
         }
         return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments()
            {
               return actuals;
            }

            @Override
            public Type getRawType()
            {
               return param.getRawType();
            }

            @Override
            public Type getOwnerType()
            {
               return param.getOwnerType();
            }
         };
      }
      else if (type instanceof GenericArrayType)
      {
         GenericArrayType arrayType = (GenericArrayType)type;
         final Type componentType = resolveTypeVariables(root, arrayType.getGenericComponentType());
         if (componentType == null) return type;
         return new GenericArrayType()
         {
            @Override
            public Type getGenericComponentType()
            {
               return componentType;
            }
         };
      }
      else
      {
         return type;
      }
   }


   /**
    * Finds an actual value of a type variable. The method looks in a class hierarchy for a class defining the variable
    * and returns the value if present.
    *
    * @param root root class
    * @param typeVariable type variable
    * @return actual type of the type variable
    */
   public static Type resolveTypeVariable(Class<?> root, TypeVariable<?> typeVariable)
   {
      if (typeVariable.getGenericDeclaration() instanceof Class<?>)
      {
         Class<?> classDeclaringTypeVariable = (Class<?>) typeVariable.getGenericDeclaration();
         Type[] types = findParameterizedTypes(root, classDeclaringTypeVariable);
         if (types == null) return  null;
         for (int i = 0; i < types.length; i++)
         {
            TypeVariable<?> tv = classDeclaringTypeVariable.getTypeParameters()[i];
            if (tv.equals(typeVariable))
            {
               return types[i];
            }
         }
      }
      return null;
   }


   /**
    * Given a class and an interfaces, go through the class hierarchy to find the interface and return its type arguments.
    *
    * @param classToSearch class
    * @param interfaceToFind interface to find
    * @return type arguments of the interface
    */
   public static Type[] getActualTypeArgumentsOfAnInterface(Class<?> classToSearch, Class<?> interfaceToFind)
   {
      Type[] types = findParameterizedTypes(classToSearch, interfaceToFind);
      if (types == null) throw new RuntimeException(Messages.MESSAGES.unableToFindTypeArguments(interfaceToFind));
      return types;
   }

   private static final Type[] EMPTY_TYPE_ARRAY = {};

   /**
    * Search for the given interface or class within the root's class/interface hierarchy.
    * If the searched for class/interface is a generic return an array of real types that fill it out.
    *
    * @param root root class
    * @param searchedFor searched class
    * @return for generic class/interface returns array of real types
    */
   public static Type[] findParameterizedTypes(Class<?> root, Class<?> searchedFor)
   {
      if (searchedFor.isInterface())
      {
         return findInterfaceParameterizedTypes(root, null, searchedFor);
      }
      return findClassParameterizedTypes(root, null, searchedFor);
   }

   public static Type[] findClassParameterizedTypes(Class<?> root, ParameterizedType rootType, Class<?> searchedForClass)
   {
      if (Object.class.equals(root)) return null;

      Map<TypeVariable<?>, Type> typeVarMap = populateParameterizedMap(root, rootType);

      Class<?> superclass = root.getSuperclass();
      Type genericSuper = root.getGenericSuperclass();

      if (superclass.equals(searchedForClass))
      {
         return extractTypes(typeVarMap, genericSuper);
      }


      if (genericSuper instanceof ParameterizedType)
      {
         ParameterizedType intfParam = (ParameterizedType) genericSuper;
         Type[] types = findClassParameterizedTypes(superclass, intfParam, searchedForClass);
         if (types != null)
         {
            return extractTypeVariables(typeVarMap, types);
         }
      }
      else
      {
         Type[] types = findClassParameterizedTypes(superclass, null, searchedForClass);
         if (types != null)
         {
            return types;
         }
      }
      return null;
   }

   private static Map<TypeVariable<?>, Type> populateParameterizedMap(Class<?> root, ParameterizedType rootType)
   {
      Map<TypeVariable<?>, Type> typeVarMap = new HashMap<>();
      if (rootType != null)
      {
         TypeVariable<? extends Class<?>>[] vars = root.getTypeParameters();
         for (int i = 0; i < vars.length; i++)
         {
            typeVarMap.put(vars[i], rootType.getActualTypeArguments()[i]);
         }
      }
      return typeVarMap;
   }

   /**
    * Create a mapping for generic types to actual types between two classes / interfaces of a type hierarchy.
    *
    * @param root The root class / interface of the type hierarchy.
    * @param superClass A superclass / interface of the type hierarchy.
    * @return The Mapping for generic types to actual types.
    */
   public static Map<TypeVariable<?>, Type> populateParameterizedMap(final Class<?> root, final Class<?> superClass)
   {
      final Type[] types = Types.findParameterizedTypes(root, superClass);
      final TypeVariable<? extends Class<?>>[] typeParameters = superClass.getTypeParameters();

      final Map<TypeVariable<?>, Type> genericTypeMap = new HashMap<>();
      if (typeParameters != null && types != null)
      {
         int i = 0;
         while (i < types.length)
         {
            genericTypeMap.put(typeParameters[i], types[i]);
            i++;
         }
         while (i < typeParameters.length)
         {
            genericTypeMap.put(typeParameters[i], typeParameters[i].getGenericDeclaration());
            i++;
         }
      }

      return genericTypeMap;
   }

   public static Type[] findInterfaceParameterizedTypes(Class<?> root, ParameterizedType rootType, Class<?> searchedForInterface)
   {
      Map<TypeVariable<?>, Type> typeVarMap = populateParameterizedMap(root, rootType);

      for (int i = 0; i < root.getInterfaces().length; i++)
      {
         Class<?> sub = root.getInterfaces()[i];
         Type genericSub = root.getGenericInterfaces()[i];
         if (sub.equals(searchedForInterface))
         {
            return extractTypes(typeVarMap, genericSub);
         }
      }

      for (int i = 0; i < root.getInterfaces().length; i++)
      {
         Type genericSub = root.getGenericInterfaces()[i];
         Class<?> sub = root.getInterfaces()[i];

         Type[] types = recurseSuperclassForInterface(searchedForInterface, typeVarMap, genericSub, sub);
         if (types != null) return types;
      }
      if (root.isInterface()) return null;

      Class<?> superclass = root.getSuperclass();
      Type genericSuper = root.getGenericSuperclass();


      return recurseSuperclassForInterface(searchedForInterface, typeVarMap, genericSuper, superclass);
   }

   private static Type[] recurseSuperclassForInterface(Class<?> searchedForInterface, Map<TypeVariable<?>, Type> typeVarMap, Type genericSub, Class<?> sub)
   {
      if (genericSub instanceof ParameterizedType)
      {
         ParameterizedType intfParam = (ParameterizedType) genericSub;
         Type[] types = findInterfaceParameterizedTypes(sub, intfParam, searchedForInterface);
         if (types != null)
         {
            return extractTypeVariables(typeVarMap, types);
         }
      }
      else
      {
         Type[] types = findInterfaceParameterizedTypes(sub, null, searchedForInterface);
         if (types != null)
         {
            return types;
         }
      }
      return null;
   }

   /**
    * Resolve generic types to actual types.
    *
    * @param typeVarMap The mapping for generic types to actual types.
    * @param types The types to resolve.
    * @return An array of resolved method parameter types in declaration order.
    */
   private static Type[] extractTypeVariables(final Map<TypeVariable<?>, Type> typeVarMap, final Type[] types)
   {
      final Type[] resolvedMethodParameterTypes = new Type[types.length];

      for (int i = 0; i < types.length; i++)
      {
         final Type methodParameterType = types[i];

         if (methodParameterType instanceof TypeVariable<?>)
         {
            resolvedMethodParameterTypes[i] = typeVarMap.get(methodParameterType);
         }
         else
         {
            resolvedMethodParameterTypes[i] = methodParameterType;
         }
      }

      return resolvedMethodParameterTypes;
   }

   private static Type[] extractTypes(Map<TypeVariable<?>, Type> typeVarMap, Type genericSub)
   {
      if (genericSub instanceof ParameterizedType)
      {
         ParameterizedType param = (ParameterizedType) genericSub;
         Type[] types = param.getActualTypeArguments();

         Type[] returnTypes = extractTypeVariables(typeVarMap, types);
         return returnTypes;
      }
      else
      {
         return EMPTY_TYPE_ARRAY;
      }
   }
}
