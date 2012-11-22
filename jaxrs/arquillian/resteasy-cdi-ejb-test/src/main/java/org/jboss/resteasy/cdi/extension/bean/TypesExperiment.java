/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.resteasy.cdi.extension.bean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.junit.Test;


public class TypesExperiment
{
   interface I<T> {}
   static class C<T> implements I<T>
   {
      
   }
   static class D<T> extends C<T> {}
   
   @Test
   public void testTypes() throws Exception
   {
      System.out.println("C.class: " + C.class);
      C<Object> c = new C<Object>();
      System.out.println("c.getClass(): " + c.getClass()); 
      for (TypeVariable<?> tv : c.getClass().getTypeParameters())
      {
         System.out.println("c.getClass().getTypeParameters()[i]: " + tv);
         System.out.println("c.getClass().getTypeParameters()[i].getGenericDeclaration(): " + tv.getGenericDeclaration());
         System.out.println("c.getClass().getTypeParameters()[i].getGenericDeclaration().getClass(): " + tv.getGenericDeclaration().getClass());
         System.out.println("c.getClass().getTypeParameters()[i].getName(): " + tv.getName());
         for (Type t : tv.getBounds())
         {
            System.out.println("c.getClass().getTypeParameters()[i].getBounds[j]: " + t);
         }
         for (TypeVariable<?> tv2 : tv.getGenericDeclaration().getTypeParameters())
         {
            System.out.println("c.getClass().getTypeParameters()[i].getGenericDeclaration().getTypeParameters()[j]: " + tv2);
         }
         System.out.println(c.getClass() == c.getClass().getTypeParameters()[0].getGenericDeclaration());
      }
      
      for (Type t : c.getClass().getGenericInterfaces())
      {
         System.out.println("c.getClass().getGenericInterfaces()[i]: " + t);
         System.out.println("c.getClass().getGenericInterfaces()[i].getClass(): " + t.getClass());
         System.out.println("c.getClass().getGenericInterfaces()[i].ParameterizedType.class.cast(t).getRawType(): " + ParameterizedType.class.cast(t).getRawType());
         System.out.println("c.getClass().getGenericInterfaces()[i].ParameterizedType.class.cast(t).getActualTypeArguments()[0]: " + ParameterizedType.class.cast(t).getActualTypeArguments()[0]);
      }
      
      System.out.println("");
      D<Object> d = new D<Object>();
      Type dt = d.getClass().getGenericSuperclass();
      System.out.println("d.getClass().getTypeParameters()[0]: " + d.getClass().getTypeParameters()[0]);
      System.out.println("d.getClass().getGenericSuperclass(): " + dt);
      System.out.println("d.getClass().getGenericSuperclass().getClass(): " + dt.getClass());
      ParameterizedType pdt = ParameterizedType.class.cast(dt);
      System.out.println("ParameterizedType.class.cast(d.getClass().getGenericSuperclass()).getRawType(): " + pdt.getRawType());
      System.out.println("ParameterizedType.class.cast(d.getClass().getGenericSuperclass()).getActualTypeArguments()[0]: " + pdt.getActualTypeArguments()[0]);
      
      @SuppressWarnings("unused")
      C<Object> c2 = new D<Object>();
   }
}

