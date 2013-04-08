/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2013 Oracle and/or its affiliates. All rights reserved.
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
package javax.ws.rs.client;

import java.lang.annotation.Annotation;
import java.util.Locale;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Variant;

/**
 * Encapsulates message entity including the associated variant information.
 *
 * @param <T> entity type.
 * @author Marek Potociar
 */
public final class Entity<T> {
    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];

    private final T entity;
    private final Variant variant;
    private final Annotation[] annotations;

    /**
     * Create an entity using a supplied content media type.
     *
     * @param <T>       entity Java type.
     * @param entity    entity data.
     * @param mediaType entity content type.
     * @return entity instance.
     */
    public static <T> Entity<T> entity(final T entity, final MediaType mediaType) {
        return new Entity<T>(entity, mediaType);
    }

    /**
     * Create an entity using a supplied content media type.
     *
     * @param <T>         entity Java type.
     * @param entity      entity data.
     * @param mediaType   entity content type.
     * @param annotations entity annotations.
     * @return entity instance.
     */
    public static <T> Entity<T> entity(final T entity, final MediaType mediaType, Annotation[] annotations) {
        return new Entity<T>(entity, mediaType, annotations);
    }

    /**
     * Create an entity using a supplied content media type.
     *
     * @param <T>       entity Java type.
     * @param entity    entity data.
     * @param mediaType entity content type.
     * @return entity instance.
     * @throws IllegalArgumentException if the supplied string cannot be parsed
     *                                  or is {@code null}.
     */
    public static <T> Entity<T> entity(final T entity, final String mediaType) {
        return new Entity<T>(entity, MediaType.valueOf(mediaType));
    }

    /**
     * Create an entity using a supplied content media type.
     *
     * @param <T>     entity Java type.
     * @param entity  entity data.
     * @param variant entity {@link Variant variant} information.
     * @return entity instance.
     */
    public static <T> Entity<T> entity(final T entity, final Variant variant) {
        return new Entity<T>(entity, variant);
    }

    /**
     * Create an entity using a supplied content media type.
     *
     * @param <T>         entity Java type.
     * @param entity      entity data.
     * @param variant     entity {@link Variant variant} information.
     * @param annotations entity annotations.
     * @return entity instance.
     */
    public static <T> Entity<T> entity(final T entity, final Variant variant, Annotation[] annotations) {
        return new Entity<T>(entity, variant, annotations);
    }

    /**
     * Create a {@value javax.ws.rs.core.MediaType#TEXT_PLAIN} entity.
     *
     * @param <T>    entity Java type.
     * @param entity entity data.
     * @return {@value javax.ws.rs.core.MediaType#TEXT_PLAIN} entity instance.
     */
    public static <T> Entity<T> text(final T entity) {
        return new Entity<T>(entity, MediaType.TEXT_PLAIN_TYPE);
    }

    /**
     * Create an {@value javax.ws.rs.core.MediaType#APPLICATION_XML} entity.
     *
     * @param <T>    entity Java type.
     * @param entity entity data.
     * @return {@value javax.ws.rs.core.MediaType#APPLICATION_XML} entity instance.
     */
    public static <T> Entity<T> xml(final T entity) {
        return new Entity<T>(entity, MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Create an {@value javax.ws.rs.core.MediaType#APPLICATION_JSON} entity.
     *
     * @param <T>    entity Java type.
     * @param entity entity data.
     * @return {@value javax.ws.rs.core.MediaType#APPLICATION_JSON} entity instance.
     */
    public static <T> Entity<T> json(final T entity) {
        return new Entity<T>(entity, MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Create a {@value javax.ws.rs.core.MediaType#TEXT_HTML} entity.
     *
     * @param <T>    entity Java type.
     * @param entity entity data.
     * @return {@value javax.ws.rs.core.MediaType#TEXT_HTML} entity instance.
     */
    public static <T> Entity<T> html(final T entity) {
        return new Entity<T>(entity, MediaType.TEXT_HTML_TYPE);
    }

    /**
     * Create an {@value javax.ws.rs.core.MediaType#APPLICATION_XHTML_XML} entity.
     *
     * @param <T>    entity Java type.
     * @param entity entity data.
     * @return {@value javax.ws.rs.core.MediaType#APPLICATION_XHTML_XML} entity
     *         instance.
     */
    public static <T> Entity<T> xhtml(final T entity) {
        return new Entity<T>(entity, MediaType.APPLICATION_XHTML_XML_TYPE);
    }

    /**
     * Create an {@value javax.ws.rs.core.MediaType#APPLICATION_FORM_URLENCODED}
     * form entity.
     *
     * @param form form data.
     * @return {@value javax.ws.rs.core.MediaType#APPLICATION_FORM_URLENCODED}
     *         form entity instance.
     */
    public static Entity<Form> form(final Form form) {
        return new Entity<Form>(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
    }

    /**
     * Create an {@value javax.ws.rs.core.MediaType#APPLICATION_FORM_URLENCODED}
     * form entity.
     *
     * @param formData multivalued map representing the form data.
     * @return {@value javax.ws.rs.core.MediaType#APPLICATION_FORM_URLENCODED}
     *         form entity instance.
     */
    public static Entity<Form> form(final MultivaluedMap<String, String> formData) {
        return new Entity<Form>(new Form(formData), MediaType.APPLICATION_FORM_URLENCODED_TYPE);
    }

    private Entity(final T entity, final MediaType mediaType) {
        this(entity, new Variant(mediaType, (Locale) null, null), EMPTY_ANNOTATIONS);
    }

    private Entity(final T entity, final Variant variant) {
        this(entity, variant, EMPTY_ANNOTATIONS);
    }

    private Entity(final T entity, final MediaType mediaType, Annotation[] annotations) {
        this(entity, new Variant(mediaType, (Locale) null, null), annotations);
    }

    private Entity(final T entity, final Variant variant, Annotation[] annotations) {
        this.entity = entity;
        this.variant = variant;
        this.annotations = annotations;
    }

    /**
     * Get entity {@link Variant variant} information.
     *
     * @return entity variant information.
     */
    public Variant getVariant() {
        return variant;
    }

    /**
     * Get entity media type.
     *
     * @return entity media type.
     */
    public MediaType getMediaType() {
        return variant.getMediaType();
    }

    /**
     * Get entity encoding.
     *
     * @return entity encoding.
     */
    public String getEncoding() {
        return variant.getEncoding();
    }

    /**
     * Get entity language.
     *
     * @return entity language.
     */
    public Locale getLanguage() {
        return variant.getLanguage();
    }

    /**
     * Get entity data.
     *
     * @return entity data.
     */
    public T getEntity() {
        return entity;
    }

    /**
     * Get the entity annotations.
     *
     * @return entity annotations if set, an empty annotation array if no
     *         entity annotations have been specified.
     */
    public Annotation[] getAnnotations() {
        return annotations;
    }
}
