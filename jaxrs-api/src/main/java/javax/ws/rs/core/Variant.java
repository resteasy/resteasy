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

import java.util.List;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * Abstraction for a resource representation variant. 
 */
public class Variant {
    
    private String language;
    private MediaType mediaType;
    private String encoding;
    
    /**
     * Create a new instance of Variant
     * @param mediaType the media type of the variant - may be null
     * @param language the language of the variant - may be null
     * @param encoding the content encoding of the variant - may be null
     */
    public Variant(MediaType mediaType, String language, String encoding) {
        this.encoding = encoding;
        this.language = language;
        this.mediaType = mediaType;
    }
    
    /**
     * Get the language of the variant
     * @return the language or null if none set
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Get the media type of the variant
     * @return the media type or null if none set
     */
    public MediaType getMediaType() {
        return mediaType;
    }

    /**
     * Get the encoding of the variant
     * @return the encoding or null if none set
     */
    public String getEncoding() {
        return encoding;
    }
    
    /**
     * A builder for a list of representation variants. 
     */
    public static abstract class VariantListBuilder {
        
        /**
         * Protected constructor, use the static <code>newInstance</code>
         * method to obtain an instance.
         */
        protected VariantListBuilder() {}
        
        /**
         * Create a new builder instance.
         * @return a new Builder
         */
        public static VariantListBuilder newInstance() {
            VariantListBuilder b = RuntimeDelegate.getInstance().createVariantListBuilder();
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
         * <p><pre>List<Variant> list = VariantListBuilder.newInstance().languages("en","fr")
         *   .encodings("zip", "identity").add().build()</pre>
         * 
         * 
         * @return the updated builder
         */
        public abstract VariantListBuilder add();
        
        /**
         * Set the language[s] for this variant.
         * @param languages the available languages
         * @return the updated builder
         */
        public abstract VariantListBuilder languages(String... languages);
        
        /**
         * Set the encoding[s] for this variant.
         * @param encodings the available encodings
         * @return the updated builder
         */
        public abstract VariantListBuilder encodings(String... encodings);
        
        /**
         * Set the media type[s] for this variant.
         * @param mediaTypes the available mediaTypes. If specific charsets
         * are supported they should be included as parameters of the respective
         * media type.
         * @return the updated builder
         */
        public abstract VariantListBuilder mediaTypes(MediaType... mediaTypes);
    }
}
