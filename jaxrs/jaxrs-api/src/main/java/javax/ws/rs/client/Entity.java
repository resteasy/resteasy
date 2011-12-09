/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011 Oracle and/or its affiliates. All rights reserved.
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
public class Entity<T> {

    private final T entity;
    private final Variant variant;

    public static <T> Entity<T> entity(T entity, MediaType mediaType) {
        return new Entity<T>(entity, mediaType);
    }

    public static <T> Entity<T> entity(T entity, String mediaType) throws IllegalStateException {
        return new Entity<T>(entity, MediaType.valueOf(mediaType));
    }

    public static <T> Entity<T> entity(T entity, Variant variant) throws IllegalStateException {
        return new Entity<T>(entity, variant);
    }

    public static <T> Entity<T> text(T entity) {
        return new Entity<T>(entity, MediaType.TEXT_PLAIN_TYPE);
    }

    public static <T> Entity<T> xml(T entity) {
        return new Entity<T>(entity, MediaType.APPLICATION_XML_TYPE);
    }

    public static <T> Entity<T> json(T entity) {
        return new Entity<T>(entity, MediaType.APPLICATION_JSON_TYPE);
    }

    public static <T> Entity<T> html(T entity) {
        return new Entity<T>(entity, MediaType.TEXT_HTML_TYPE);
    }

    public static <T> Entity<T> xhtml(T entity) {
        return new Entity<T>(entity, MediaType.APPLICATION_XHTML_XML_TYPE);
    }

    public static Entity<Form> form(Form form) {
        return new Entity<Form>(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
    }

    public static Entity<Form> form(MultivaluedMap<String, String> formData) {
        return new Entity<Form>(new Form(formData), MediaType.APPLICATION_FORM_URLENCODED_TYPE);
    }

    private Entity(T entity, MediaType mediaType) {
        this.entity = entity;
        this.variant = new Variant(mediaType, null, null);
    }

    private Entity(T entity, Variant variant) {
        this.entity = entity;
        this.variant = variant;
    }

    public Variant getVariant() {
        return variant;
    }

    public MediaType getMediaType() {
        return variant.getMediaType();
    }

    public String getEncoding() {
        return variant.getEncoding();
    }

    public Locale getLanguage() {
        return variant.getLanguage();
    }

    public T getEntity() {
        return entity;
    }
}
