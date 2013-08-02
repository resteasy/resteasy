/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package javax.ws.rs.core;

import java.io.StringWriter;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.ext.RuntimeDelegate;

/**
 * Abstraction for a resource representation variant.
 *
 * Contains information about media type, language and encoding of the resource representation.
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @since 1.0
 */
public class Variant {

    private Locale language;
    private MediaType mediaType;
    private String encoding;

    /**
     * Create a new instance of Variant.
     *
     * @param mediaType the media type of the variant - may be {@code null}.
     * @param language  the language of the variant (two-letter ISO-639 code);
     *                  may be {@code null}.
     * @param encoding  the content encoding of the variant - may be {@code null}.
     * @throws java.lang.IllegalArgumentException
     *          if all the parameters are {@code null}.
     * @since 2.0
     */
    public Variant(MediaType mediaType, String language, String encoding) {
        if (mediaType == null && language == null && encoding == null) {
            throw new IllegalArgumentException("mediaType, language, encoding all null");
        }
        this.encoding = encoding;
        this.language = (language == null) ? null : new Locale(language);
        this.mediaType = mediaType;
    }

    /**
     * Create a new instance of Variant.
     *
     * @param mediaType the media type of the variant - may be {@code null}.
     * @param language  the language of the variant (two-letter ISO-639 code);
     *                  may be {@code null}.
     * @param country   uppercase two-letter ISO-3166 language code of the variant;
     *                  may be {@code null} provided {@code language} is {@code null} too.
     * @param encoding  the content encoding of the variant - may be {@code null}.
     * @throws java.lang.IllegalArgumentException
     *          if all the parameters are {@code null}.
     * @since 2.0
     */
    public Variant(MediaType mediaType, String language, String country, String encoding) {
        if (mediaType == null && language == null && encoding == null) {
            throw new IllegalArgumentException("mediaType, language, encoding all null");
        }
        this.encoding = encoding;
        this.language = (language == null) ? null : new Locale(language, country);
        this.mediaType = mediaType;
    }

    /**
     * Create a new instance of Variant.
     *
     * @param mediaType       the media type of the variant - may be {@code null}.
     * @param language        the language of the variant (two-letter ISO-639 code);
     *                        may be {@code null}.
     * @param country         uppercase two-letter ISO-3166 language code of the variant;
     *                        may be {@code null} provided {@code language} is {@code null} too.
     * @param languageVariant vendor and browser specific language code of the variant
     *                        (see also {@link Locale} class description);
     *                        may be {@code null} provided {@code language} and
     *                        {@code country} are {@code null} too.
     * @param encoding        the content encoding of the variant - may be {@code null}.
     * @throws java.lang.IllegalArgumentException
     *          if all the parameters are {@code null}.
     * @since 2.0
     */
    public Variant(MediaType mediaType, String language, String country, String languageVariant, String encoding) {
        if (mediaType == null && language == null && encoding == null) {
            throw new IllegalArgumentException("mediaType, language, encoding all null");
        }
        this.encoding = encoding;
        this.language = (language == null) ? null : new Locale(language, country, languageVariant);
        this.mediaType = mediaType;
    }

    /**
     * Create a new instance of Variant.
     *
     * @param mediaType the media type of the variant - may be {@code null}.
     * @param language  the language of the variant - may be {@code null}.
     * @param encoding  the content encoding of the variant - may be {@code null}.
     * @throws java.lang.IllegalArgumentException
     *          if all the parameters are {@code null}.
     */
    public Variant(MediaType mediaType, Locale language, String encoding) {
        if (mediaType == null && language == null && encoding == null) {
            throw new IllegalArgumentException("mediaType, language, encoding all null");
        }
        this.encoding = encoding;
        this.language = language;
        this.mediaType = mediaType;
    }

    /**
     * Get the language of the variant.
     *
     * @return the language or  {@code null} if none set.
     */
    public Locale getLanguage() {
        return language;
    }

    /**
     * Get the string representation of the variant language,
     * or {@code null} if no language has been set.
     *
     * @return the string representing variant language or {@code null}
     *         if none set.
     * @since 2.0
     */
    public String getLanguageString() {
        return (language == null) ? null : language.toString();
    }

    /**
     * Get the media type of the variant.
     *
     * @return the media type or {@code null} if none set.
     */
    public MediaType getMediaType() {
        return mediaType;
    }

    /**
     * Get the encoding of the variant.
     *
     * @return the encoding or {@code null} if none set.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Create a {@link VariantListBuilder} initialized with a set of supported
     * media types.
     *
     * @param mediaTypes the available mediaTypes. If specific char-sets
     *                   are supported they should be included as parameters of the respective
     *                   media type.
     * @return the initialized builder.
     * @throws java.lang.IllegalArgumentException
     *          if mediaTypes is null or
     *          contains no elements.
     */
    public static VariantListBuilder mediaTypes(MediaType... mediaTypes) {
        VariantListBuilder b = VariantListBuilder.newInstance();
        b.mediaTypes(mediaTypes);
        return b;
    }

    /**
     * Create a {@link VariantListBuilder} initialized with a set of supported
     * languages.
     *
     * @param languages the available languages.
     * @return the initialized builder.
     * @throws java.lang.IllegalArgumentException
     *          if languages is null or
     *          contains no elements.
     */
    public static VariantListBuilder languages(Locale... languages) {
        VariantListBuilder b = VariantListBuilder.newInstance();
        b.languages(languages);
        return b;
    }

    /**
     * Create a {@link VariantListBuilder} initialized with a set of supported
     * encodings.
     *
     * @param encodings the available encodings.
     * @return the initialized builder.
     * @throws java.lang.IllegalArgumentException
     *          if encodings is null or
     *          contains no elements.
     */
    public static VariantListBuilder encodings(String... encodings) {
        VariantListBuilder b = VariantListBuilder.newInstance();
        b.encodings(encodings);
        return b;
    }

    /**
     * Generate hash code from variant properties.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
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
     * @param obj the object to compare to.
     * @return true if the two variants are the same, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Variant other = (Variant) obj;
        if (this.language != other.language && (this.language == null || !this.language.equals(other.language))) {
            return false;
        }
        if (this.mediaType != other.mediaType && (this.mediaType == null || !this.mediaType.equals(other.mediaType))) {
            return false;
        }
        //noinspection StringEquality
        return this.encoding == other.encoding || (this.encoding != null && this.encoding.equals(other.encoding));
    }

    @Override
    public String toString() {
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
    public static abstract class VariantListBuilder {

        /**
         * Protected constructor, use the static {@code newInstance}
         * method to obtain an instance.
         */
        protected VariantListBuilder() {
        }

        /**
         * Create a new builder instance.
         *
         * @return a new builder instance.
         */
        public static VariantListBuilder newInstance() {
            return RuntimeDelegate.getInstance().createVariantListBuilder();
        }

        /**
         * Add the current combination of metadata to the list of supported variants
         * (provided the current combination of metadata is not empty) and
         * build a list of representation variants from the current state of
         * the builder. After this method is called the builder is reset to
         * an empty state.
         *
         * @return a list of representation variants.
         */
        public abstract List<Variant> build();

        /**
         * Add the current combination of metadata to the list of supported variants,
         * after this method is called the current combination of metadata is emptied.
         * <p>
         * If more than one value is supplied for one or more of the variant properties
         * then a variant will be generated for each possible combination. E.g.
         * in the following {@code list} would have five (4 + 1) members:
         * </p>
         * <pre>List<Variant> list = VariantListBuilder.newInstance()
         *         .languages(Locale.ENGLISH, Locale.FRENCH).encodings("zip", "identity").add()
         *         .languages(Locale.GERMAN).mediaTypes(MediaType.TEXT_PLAIN_TYPE).add()
         *         .build()</pre>
         * <p>
         * Note that it is not necessary to call the {@code add()} method immediately before
         * the build method is called. E.g. the resulting list produced in the example above
         * would be identical to the list produced by the following code:
         * </p>
         * <pre>List<Variant> list = VariantListBuilder.newInstance()
         *         .languages(Locale.ENGLISH, Locale.FRENCH).encodings("zip", "identity").add()
         *         .languages(Locale.GERMAN).mediaTypes(MediaType.TEXT_PLAIN_TYPE)
         *         .build()</pre>
         *
         * @return the updated builder.
         * @throws IllegalStateException if there is not at least one
         *                               mediaType, language or encoding set for the current variant.
         */
        public abstract VariantListBuilder add();

        /**
         * Set the language(s) for this variant.
         *
         * @param languages the available languages.
         * @return the updated builder.
         */
        public abstract VariantListBuilder languages(Locale... languages);

        /**
         * Set the encoding(s) for this variant.
         *
         * @param encodings the available encodings.
         * @return the updated builder.
         */
        public abstract VariantListBuilder encodings(String... encodings);

        /**
         * Set the media type(s) for this variant.
         *
         * @param mediaTypes the available mediaTypes. If specific charsets
         *                   are supported they should be included as parameters of the respective
         *                   media type.
         * @return the updated builder.
         */
        public abstract VariantListBuilder mediaTypes(MediaType... mediaTypes);
    }
}
