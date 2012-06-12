package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.util.PickConstructor;

import javax.ws.rs.BindingPriority;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LegacyPrecedence
{
   private static final int DEFAULTS_ORDER = 100000;
   protected Map<String, Integer> precedenceOrder = new HashMap<String, Integer>();
   protected List<String> precedenceList = new ArrayList<String>();

   public int calculateOrder(Class<?> clazz)
   {
      Precedence precedence = (Precedence)clazz.getAnnotation(Precedence.class);
      if (precedence != null)
      {
         String value = precedence.value();
         Integer o = precedenceOrder.get(value);
         if (o == null) throw new RuntimeException("Unknown interceptor precedence: " + value);
         return o;
      }
      else
      {
         for (Annotation annotation : clazz.getAnnotations())
         {
            precedence = annotation.annotationType().getAnnotation(Precedence.class);
            if (precedence != null)
            {
               String value = precedence.value();
               Integer o = precedenceOrder.get(value);
               if (o == null) throw new RuntimeException("Unknown interceptor precedence: " + value);
               return o;
            }
         }
      }
      return BindingPriority.USER;
   }

   public void addPrecedence(String precedent, int order)
   {
      precedenceList.add(precedent);
      precedenceOrder.put(precedent, order);
   }

   public void appendPrecedence(String precedence)
   {
      precedenceList.add(precedence);

      int greatest = 0;
      for (Integer i : precedenceOrder.values())
      {
         if (i > greatest &&  ((int)i) != DEFAULTS_ORDER) greatest = i;
      }

      addPrecedence(precedence, greatest + 100);
   }

   public void insertPrecedenceAfter(String after, String newPrecedence)
   {
      int order = precedenceOrder.get(after);
      order++;
      addPrecedence(newPrecedence, order);
   }

   public void insertPrecedenceBefore(String after, String newPrecedence)
   {
      int order = precedenceOrder.get(after);
      order--;
      addPrecedence(newPrecedence, order);
   }

   public LegacyPrecedence()
   {
      precedenceOrder.put("DEFAULT", DEFAULTS_ORDER);
   }

}
