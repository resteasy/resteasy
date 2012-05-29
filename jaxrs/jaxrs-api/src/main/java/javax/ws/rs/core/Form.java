/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2012 Oracle and/or its affiliates. All rights reserved.
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

/**
 * Represents the the HTML form data request entity encoded using the
 * {@code "application/x-www-form-urlencoded"} content type.
 *
 * @author Marek Potociar
 * @since 2.0
 */
public class Form {

    private static final long serialVersionUID = 9081959911712065219L;
    private final MultivaluedMap<String, String> parameters;

    /**
     * Create a new form data instance.
     */
    public Form() {
        this(new MultivaluedHashMap<String, String>());
    }

    /**
     * Create a new form data instance with a single parameter entry.
     *
     * @param parameterName form parameter name.
     * @param parameterValue form parameter value.
     */
    public Form(final String parameterName, final String parameterValue) {
        this(new MultivaluedHashMap<String, String>());

        parameters.add(parameterName, parameterValue);
    }

    /**
     * Create a new form data instance and register the underlying parameter
     * store.
     *
     * @param store form data store used by the created form instance.
     */
    public Form(final MultivaluedMap<String, String> store) {
        this.parameters = store;
    }

    /**
     * Adds a new value to the specified form parameter.
     *
     * @param name name of the parameter.
     * @param value new parameter value to be added.
     * @return updated {@code Form} instance.
     */
    public Form param(final String name, final String value) {
        parameters.add(name, value);

        return this;
    }

    /**
     * Returns multivalued map representation of the form.
     *
     * @return form represented as multivalued map.
     * @see MultivaluedMap
     */
    public MultivaluedMap<String, String> asMap() {
        return parameters;
    }
}
