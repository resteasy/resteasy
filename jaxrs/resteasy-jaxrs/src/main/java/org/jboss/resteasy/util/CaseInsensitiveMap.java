package org.jboss.resteasy.util;

import org.jboss.resteasy.specimpl.MultivaluedTreeMap;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
//public class CaseInsensitiveMap<V> implements MultivaluedMap<String, V>, Serializable
public class CaseInsensitiveMap<V> extends MultivaluedTreeMap<String, V>
{
   public static final Comparator<String> CASE_INSENSITIVE_ORDER
           = new CaseInsensitiveComparator();
   private static class CaseInsensitiveComparator
           implements Comparator<String>, java.io.Serializable {

      public int compare(String s1, String s2) {
         if (s1 == s2) return 0;
         int n1 = 0;
         // null check is different than JDK version of this method
         if (s1 != null) n1 = s1.length();
         int n2 = 0;
         if (s2 != null) n2 = s2.length();
         int min = Math.min(n1, n2);
         for (int i = 0; i < min; i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);
            if (c1 != c2) {
               c1 = Character.toLowerCase(c1);
               c2 = Character.toLowerCase(c2);
               if (c1 != c2) {
                  // No overflow because of numeric promotion
                  return c1 - c2;
               }
            }
         }
         return n1 - n2;
      }
   }

   public CaseInsensitiveMap()
   {
      super(CASE_INSENSITIVE_ORDER);
   }


}
