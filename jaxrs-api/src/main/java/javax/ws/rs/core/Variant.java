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
 */

/*
 * Variant.java
 *
 * Created on September 27, 2007, 3:12 PM
 *
 */

package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;

/**
 * Abstraction for a resource representation variant.
 */
public class Variant
{

   private Locale language;
   private MediaType mediaType;
   private String encoding;

   /**
    * Create a new instance of Variant
    *
    * @param mediaType the media type of the variant - may be null
    * @param language  the language of the variant - may be null
    * @param encoding  the content encoding of the variant - may be null
    * @throws java.lang.IllegalArgumentException
    *          if all three parameters are
    *          null
    */
   public Variant(MediaType mediaType, Locale language, String encoding)
   {
      if (mediaType == null && language == null && encoding == null)
         throw new IllegalArgumentException("mediaType, language, encoding all null");
      this.encoding = encoding;
      this.language = language;
      this.mediaType = mediaType;
   }

   /**
    * Get the language of the variant
    *
    * @return the language or null if none set
    */
   public Locale getLanguage()
   {
      return language;
   }

   /**
    * Get the media type of the variant
    *
    * @return the media type or null if none set
    */
   public MediaType getMediaType()
   {
      return mediaType;
   }

   /**
    * Get the encoding of the variant
    *
    * @return the encoding or null if none set
    */
   public String getEncoding()
   {
      return encoding;
   }

   /**
    * Create a {@link VariantListBuilder} initialized with a set of supported
    * media types.
    *
    * @param mediaTypes the available mediaTypes. If specific charsets
    *                   are supported they should be included as parameters of the respective
    *                   media type.
    * @return the initailized builder
    * @throws java.lang.IllegalArgumentException
    *          if mediaTypes is null or
    *          contains no elements.
    */
   public static VariantListBuilder mediaTypes(MediaType... mediaTypes)
   {
      VariantListBuilder b = VariantListBuilder.newInstance();
      b.mediaTypes(mediaTypes);
      return b;
   }

   /**
    * Create a {@link VariantListBuilder} initialized with a set of supported
    * languages.
    *
    * @param languages the available languages.
    * @return the initailized builder
    * @throws java.lang.IllegalArgumentException
    *          if languages is null or
    *          contains no elements.
    */
   public static VariantListBuilder languages(Locale... languages)
   {
      VariantListBuilder b = VariantListBuilder.newInstance();
      b.languages(languages);
      return b;
   }

   /**
    * Create a {@link VariantListBuilder} initialized with a set of supported
    * encodings.
    *
    * @param encodings the available encodings.
    * @return the initailized builder
    * @throws java.lang.IllegalArgumentException
    *          if encodings is null or
    *          contains no elements.
    */
   public static VariantListBuilder encodings(String... encodings)
   {
      VariantListBuilder b = VariantListBuilder.newInstance();
      b.encodings(encodings);
      return b;
   }

   /**
    * Generate hash code from variant properties.
    *
    * @return the hash code
    */
   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = 29 * hash + (this.language != null ? this.language.hashCode() : 0);
      hash = 29 * hash + (this.mediaType != null ? this.mediaType.hashCode() : 0);
      hash = 29 * hash + (this.encoding != null ? this.encoding.hashCode() : 0);
      return hash;
   }

   /**
    * Compares obj to this variant to see if they are the same
    * considering all property values.
    *
    * @param obj the object to compare to
    * @return true if the two variants are the same, false otherwise.
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj == null)
      {
         return false;
      }
      if (getClass() != obj.getClass())
      {
         return false;
      }
      final Variant other = (Variant) obj;
      if (this.language != other.language && (this.language == null || !this.language.equals(other.language)))
      {
         return false;
      }
      if (this.mediaType != other.mediaType && (this.mediaType == null || !this.mediaType.equals(other.mediaType)))
      {
         return false;
      }
      if (this.encoding != other.encoding && (this.encoding == null || !this.encoding.equals(other.encoding)))
      {
         return false;
      }
      return true;
   }

   @Override
   public String toString()
   {
      StringWriter w = new StringWriter();
      w.append("Variant[mediaType=");
      w.append(mediaType == null ? "null" : mediaType.toString());
      w.append(", language=");
      w.append(language == null ? "null" : language.toString());
      w.append(", encoding=");
      w.append(encoding == null ? "null" : encoding);
      w.append("]");
      return w.toString();
   }

   /**
    * A builder for a list of representation variants.
    */
   public static abstract class VariantListBuilder
   {

      /**
       * Protected constructor, use the static <code>newInstance</code>
       * method to obtain an instance.
       */
      protected VariantListBuilder()
      {
      }

      /**
       * Create a new builder instance.
       *
       * @return a new Builder
       */
      public static VariantListBuilder newInstance()
      {
         VariantListBuilder b = RuntimeDelegate.getInstance().createVariantListBuilder();
         return b;
      }

      /**
       * Build a list of representation variants from the current state of
       * the builder. After this method is called the builder is reset to
       * an empty state.
       *
       * @return a list of representation variants
       */
      public abstract List<Variant> build();

      /**
       * Add the current combination of metadata to the list of supported variants,
       * after this method is called the current combination of metadata is emptied.
       * If more than one value is supplied for one or more of the variant properties
       * then a variant will be generated for each possible combination. E.g.
       * in the following <code>list</code> would have four members:
       * <p><pre>List<Variant> list = VariantListBuilder.newInstance().languages("en","fr")
       *   .encodings("zip", "identity").add().build()</pre>
       *
       * @return the updated builder
       * @throws java.lang.IllegalStateException
       *          if there is not at least one
       *          mediaType, language or encoding set for the current variant.
       */
      public abstract VariantListBuilder add();

      /**
       * Set the language[s] for this variant.
       *
       * @param languages the available languages
       * @return the updated builder
       */
      public abstract VariantListBuilder languages(Locale... languages);

      /**
       * Set the encoding[s] for this variant.
       *
       * @param encodings the available encodings
       * @return the updated builder
       */
      public abstract VariantListBuilder encodings(String... encodings);

      /**
       * Set the media type[s] for this variant.
       *
       * @param mediaTypes the available mediaTypes. If specific charsets
       *                   are supported they should be included as parameters of the respective
       *                   media type.
       * @return the updated builder
       */
      public abstract VariantListBuilder mediaTypes(MediaType... mediaTypes);
   }
}
