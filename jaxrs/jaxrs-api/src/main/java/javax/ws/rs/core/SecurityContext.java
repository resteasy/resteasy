/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2012 Oracle and/or its affiliates. All rights reserved.
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

import java.security.Principal;

/**
 * An injectable interface that provides access to security related
 * information.
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @see Context
 * @since 1.0
 */
public interface SecurityContext {

    /**
     * String identifier for Basic authentication. Value "BASIC"
     */
    public static final String BASIC_AUTH = "BASIC";
    /**
     * String identifier for Client Certificate authentication. Value "CLIENT_CERT"
     */
    public static final String CLIENT_CERT_AUTH = "CLIENT_CERT";
    /**
     * String identifier for Digest authentication. Value "DIGEST"
     */
    public static final String DIGEST_AUTH = "DIGEST";
    /**
     * String identifier for Form authentication. Value "FORM"
     */
    public static final String FORM_AUTH = "FORM";

    /**
     * Returns a <code>java.security.Principal</code> object containing the
     * name of the current authenticated user. If the user
     * has not been authenticated, the method returns null.
     *
     * @return a <code>java.security.Principal</code> containing the name
     *         of the user making this request; null if the user has not been
     *         authenticated
     * @throws java.lang.IllegalStateException
     *          if called outside the scope of a request
     */
    public Principal getUserPrincipal();

    /**
     * Returns a boolean indicating whether the authenticated user is included
     * in the specified logical "role". If the user has not been authenticated,
     * the method returns <code>false</code>.
     *
     * @param role a <code>String</code> specifying the name of the role
     * @return a <code>boolean</code> indicating whether the user making
     *         the request belongs to a given role; <code>false</code> if the user
     *         has not been authenticated
     * @throws java.lang.IllegalStateException
     *          if called outside the scope of a request
     */
    public boolean isUserInRole(String role);

    /**
     * Returns a boolean indicating whether this request was made
     * using a secure channel, such as HTTPS.
     *
     * @return <code>true</code> if the request was made using a secure
     *         channel, <code>false</code> otherwise
     * @throws java.lang.IllegalStateException
     *          if called outside the scope of a request
     */
    public boolean isSecure();

    /**
     * Returns the string value of the authentication scheme used to protect
     * the resource. If the resource is not authenticated, null is returned.
     *
     * Values are the same as the CGI variable AUTH_TYPE
     *
     * @return one of the static members BASIC_AUTH, FORM_AUTH,
     *         CLIENT_CERT_AUTH, DIGEST_AUTH (suitable for == comparison) or the
     *         container-specific string indicating the authentication scheme,
     *         or null if the request was not authenticated.
     * @throws java.lang.IllegalStateException
     *          if called outside the scope of a request
     */
    public String getAuthenticationScheme();
}
