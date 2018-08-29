package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;

import javax.ws.rs.Priorities;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Deprecated
public class LegacyPrecedence
{
   private static final int DEFAULTS_ORDER = 100000;
   protected Map<String, Integer> precedenceOrder = new HashMap<String, Integer>();
   protected List<String> precedenceList = new ArrayList<String>();

   public LegacyPrecedence clone()
   {
      LegacyPrecedence precedence = new LegacyPrecedence();
      precedence.precedenceList.addAll(precedenceList);
      precedence.precedenceOrder.putAll(precedenceOrder);
      return precedence;
   }

   public int calculateOrder(Class<?> clazz)
   {
      Precedence precedence = (Precedence)clazz.getAnnotation(Precedence.class);
      if (precedence != null)
      {
         String value = precedence.value();
         Integer o = precedenceOrder.get(value);
         if (o == null) throw new RuntimeException(Messages.MESSAGES.unknownInterceptorPrecedence(value));
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
               if (o == null) throw new RuntimeException(Messages.MESSAGES.unknownInterceptorPrecedence(value));
               return o;
            }
         }
      }
      return Priorities.USER;
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
