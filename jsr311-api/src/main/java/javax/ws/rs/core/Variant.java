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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.ext.ProviderFactory;

/**
 * Abstraction for a resource representation variant. 
 */
public interface Variant {
    
    /**
     * Get the character set of the variant
     * @return the character set or null if none set
     */
    public String getCharset();

    /**
     * Get the language of the variant
     * @return the language or null if none set
     */
    public String getLanguage();

    /**
     * Get the media type of the variant
     * @return the media type or null if none set
     */
    public MediaType getMediaType();

    /**
     * Get the encoding of the variant
     * @return the encoding or null if none set
     */
    public String getEncoding();
    
    /**
     * A builder for a list of representation variants. 
     */
    public static abstract class ListBuilder {
        
        private ListBuilder() {
        }

        /**
         * Create a new builder instance.
         * @return a new Builder
         */
        public static ListBuilder newInstance() {
            ListBuilder b = ProviderFactory.getInstance().createInstance(ListBuilder.class);
            if (b==null)
                throw new UnsupportedOperationException(ApiMessages.NO_BUILDER_IMPL());
            return b;
        }
                
        /**
         * Build a list of representation variants from the current state of
         * the builder. After this method is called the builder is reset to
         * an empty state.
         * @return a list of representation variants
         */
        public abstract List<Variant> build();
        
        /**
         * Add the current combination of metadata to the list of supported variants,
         * after this method is called the current combination of metadata is emptied.
         * If more than one value is supplied for one or more of the variant properties
         * then a variant will be generated for each possible combination. E.g.
         * in the following <code>list</code> would have four members:
         * <p><pre>List<Variant> list = ListBuilder.newInstance().languages("en","fr")
         *   .charsets("ISO-8859-1", "UTF-8").add().build()</pre>
         * 
         * @return the updated builder
         */
        public abstract ListBuilder add();
        
        /**
         * Set the character set[s] for this variant.
         * @param charsets the available character sets
         * @return the updated builder
         */
        public abstract ListBuilder charsets(String... charsets);
        
        /**
         * Set the language[s] for this variant.
         * @param languages the available languages
         * @return the updated builder
         */
        public abstract ListBuilder languages(String... languages);
        
        /**
         * Set the encoding[s] for this variant.
         * @param encodings the available encodings
         * @return the updated builder
         */
        public abstract ListBuilder encodings(String... encodings);
        
        /**
         * Set the media type[s] for this variant.
         * @param mediaTypes the available mediaTypes
         * @return the updated builder
         */
        public abstract ListBuilder mediaTypes(MediaType... mediaTypes);
    }
}
