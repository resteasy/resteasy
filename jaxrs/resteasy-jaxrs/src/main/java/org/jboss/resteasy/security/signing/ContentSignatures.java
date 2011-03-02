package org.jboss.resteasy.security.signing;

import org.jboss.resteasy.util.GroupParameterParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Collection of ContentSignature objects.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContentSignatures
{
   protected List<ContentSignature> signatures = new ArrayList<ContentSignature>();

   public ContentSignatures(String header)
   {
      addSignature(header);
   }

   /**
    * Parses Content-Header string for one or more signatures
    *
    * @param header
    */
   public void addSignature(String header)
   {
      GroupParameterParser parser = new GroupParameterParser();
      List<Map<String, String>> sigs = parser.parse(header, ';', ',');
      for (Map<String, String> attributes : sigs)
      {
         signatures.add(new ContentSignature(attributes));
      }
   }

   public ContentSignatures()
   {
   }

   public String toString()
   {
      boolean first = true;
      StringBuffer buf = new StringBuffer();
      for (ContentSignature signature : signatures)
      {
         if (first) first = false;
         else
         {
            buf.append(",");
         }
         buf.append(signature.toString());
      }
      return buf.toString();
   }

   public ContentSignature addNew()
   {
      ContentSignature signature = new ContentSignature();
      signatures.add(signature);
      return signature;
   }

   public List<ContentSignature> getSignatures()
   {
      return signatures;
   }

   public ContentSignature getBy(String attribute, String value)
   {
      for (ContentSignature signature : signatures)
      {
         String val = signature.getAttributes().get(attribute);
         if (val == null) continue;
         if (val.equals(value)) return signature;
      }
      return null;
   }

}
