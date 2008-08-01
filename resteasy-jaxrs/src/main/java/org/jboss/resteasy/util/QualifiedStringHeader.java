package org.jboss.resteasy.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QualifiedStringHeader
{
   private String value;
   private Map<String, String> parameters = new HashMap<String, String>();
   private double q = 1.0;

   protected QualifiedStringHeader()
   {

   }

   public QualifiedStringHeader(String value)
   {
      this.value = value;
   }

   public String getValue()
   {
      return value;
   }

   public Map<String, String> getParameters()
   {
      return parameters;
   }

   public double getQ()
   {
      return q;
   }

   public static QualifiedStringHeader parse(String value)
   {
      QualifiedStringHeader header = new QualifiedStringHeader();
      int idx = value.indexOf(";");
      if (idx < 0)
      {
         header.value = value;
         header.q = 1.0;
      }
      else
      {
         header.value = value.substring(0, idx);
         String params = value.substring(idx + 1);
         if (params.startsWith(";")) params = params.substring(1);
         String[] array = params.split(";");
         for (String param : array)
         {
            int pidx = param.indexOf("=");
            String name = param.substring(0, pidx);
            String val = param.substring(pidx + 1);
            if (name.equals("q"))
            {
               header.q = Double.valueOf(val);
            }
            else header.parameters.put(name, val);
         }
      }
      return header;
   }

   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (!(o instanceof QualifiedStringHeader)) return false;

      QualifiedStringHeader comp = (QualifiedStringHeader) o;

      if (!value.equals(comp.value)) return false;

      Map<String, String> params1 = this.getParameters();
      Map<String, String> params2 = comp.getParameters();

      if (params1 == params2) return true;
      if (params1 == null || params2 == null) return false;
      if (params1.size() == 0 && params2.size() == 0) return true;
      int numParams1 = params1.size();
      if (params1.containsKey("q")) numParams1--;
      int numParams2 = params2.size();
      if (params2.containsKey("q")) numParams2--;

      if (numParams1 != numParams2) return false;
      if (numParams1 == 0) return true;

      for (String key : params1.keySet())
      {
         if (key.equals("q")) continue;
         String value = params1.get(key);
         String value2 = params2.get(key);
         if (value == value2) continue; // both null
         if (value == null || value2 == null) return false;
         if (value.equals(value2) == false) return false;
      }
      return true;
   }

   public int compareWeight(QualifiedStringHeader header)
   {
      if (q == header.q) return 0;
      else if (q < header.q) return 1;
      else return -1;
   }

}
