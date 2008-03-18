package org.resteasy.util;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AcceptableVariant implements Comparable<AcceptableVariant>
{
   private Variant variant;
   private MediaType type;
   private QualifiedStringHeader language;
   private QualifiedStringHeader encoding;

   public AcceptableVariant(Variant variant)
   {
      this.variant = variant;
      this.type = variant.getMediaType();
      if (variant.getLanguage() != null)
      {
         language = QualifiedStringHeader.parse(variant.getLanguage());
      }
      if (variant.getEncoding() != null)
      {
         encoding = QualifiedStringHeader.parse(variant.getEncoding());
      }
   }

   public int compareTo(AcceptableVariant acceptableVariant)
   {
      int compare = 0;
      if (type == acceptableVariant.type) compare = 0;
      else if (type != null && acceptableVariant.type != null)
         compare = MediaTypeHelper.compareWeight(type, acceptableVariant.type);
      else if (type == null) compare = 1;
      else if (type != null) compare = -1;

      if (compare != 0) return compare;

      if (language == acceptableVariant.language) compare = 0;
      else if (language != null && acceptableVariant.language != null)
         compare = language.compareWeight(acceptableVariant.language);
      else if (language == null) compare = 1;
      else if (language != null) compare = -1;


      if (compare != 0) return compare;

      if (encoding == acceptableVariant.encoding) compare = 0;
      else if (encoding != null && acceptableVariant.encoding != null)
         compare = encoding.compareWeight(acceptableVariant.encoding);
      else if (encoding == null) return 1;
      else if (encoding != null) return -1;

      return 0;
   }

   public Variant getVariant()
   {
      return variant;
   }

   public MediaType getType()
   {
      return type;
   }

   public QualifiedStringHeader getLanguage()
   {
      return language;
   }

   public QualifiedStringHeader getEncoding()
   {
      return encoding;
   }

   public static List<Variant> sort(List<Variant> variants)
   {
      ArrayList<AcceptableVariant> acceptable = new ArrayList<AcceptableVariant>();
      for (Variant v : variants) acceptable.add(new AcceptableVariant(v));
      Collections.sort(acceptable);
      List<Variant> sorted = new ArrayList<Variant>();
      for (AcceptableVariant v : acceptable)
      {
         sorted.add(v.getVariant());
      }
      return sorted;
   }

   public static Variant pick(List<Variant> wants, List<Variant> has)
   {
      ArrayList<AcceptableVariant> acceptable = new ArrayList<AcceptableVariant>();
      for (Variant v : wants) acceptable.add(new AcceptableVariant(v));
      Collections.sort(acceptable);

      ArrayList<AcceptableVariant> produces = new ArrayList<AcceptableVariant>();
      for (Variant v : has) produces.add(new AcceptableVariant(v));

      for (AcceptableVariant accept : acceptable)
      {
         for (AcceptableVariant produce : produces)
         {
            boolean match = false;
            if (produce.getType() == null || accept.getType() == null) match = true;
            else match = MediaTypeHelper.equivalent(produce.getType(), accept.getType());

            if (!match) continue;

            match = false;
            if (produce.getLanguage() == null || accept.getLanguage() == null) match = true;
            else match = produce.getLanguage().equals(accept.getLanguage());

            if (!match) continue;

            match = false;
            if (produce.getEncoding() == null || accept.getEncoding() == null) match = true;
            else match = produce.getEncoding().equals(accept.getEncoding());

            return produce.getVariant();
         }
      }
      return null;
   }
}
