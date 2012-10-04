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
package javax.ws.rs;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

/**
 * Exception class used by JAX-RS implementations to report constraint
 * validations.
 *
 * @author Santiago.PericasGeertsen (Santiago.PericasGeertsen@oracle.com)
 * @since 2.0
 */
public class ValidationException extends WebApplicationException {

    private static final long serialVersionUID = 11660103L;
    private List<? extends ValidationError> violations;

    /**
     * Construct a new instance with an HTTP status code of 500 and an
     * empty list of violations.
     */
    public ValidationException() {
        this(Response.Status.INTERNAL_SERVER_ERROR, Collections.<ValidationError>emptyList());
    }

    /**
     * Construct a new instance with the specified HTTP status code and
     * an empty list of violations.
     *
     * @param status HTTP status code
     */
    public ValidationException(final int status) {
        this(status, Collections.<ValidationError>emptyList());
    }

    /**
     * Construct a new instance with the specified HTTP status code and
     * an empty list of violations.
     *
     * @param status HTTP status code
     * @throws IllegalArgumentException if status is {@code null}
     */
    public ValidationException(final Response.Status status) throws IllegalArgumentException {
        this(status, Collections.<ValidationError>emptyList());
    }

    /**
     * Construct a new instance with the specified HTTP status code and
     * a list of violations.
     *
     * @param status     HTTP status code
     * @param violations list of violations
     * @throws IllegalArgumentException if status or violations is {@code null}
     */
    public ValidationException(final int status, final List<? extends ValidationError> violations)
            throws IllegalArgumentException {
        super(status);
        if (violations == null) {
            throw new IllegalArgumentException("List of violations must not be null");
        }
        this.violations = violations;
    }

    /**
     * Construct a new instance with the specified HTTP status code and
     * a list of violations.
     *
     * @param status     HTTP status code
     * @param violations list of violations
     * @throws IllegalArgumentException if status or violations is {@code null}
     */
    public ValidationException(final Response.Status status, final List<? extends ValidationError> violations)
            throws IllegalArgumentException {
        super(status);
        if (violations == null) {
            throw new IllegalArgumentException("List of violations must not be null");
        }
        this.violations = violations;
    }

    /**
     * Get list of constraint validations.
     *
     * @return list of constraint validations.
     */
    public List<? extends ValidationError> getViolations() {
        return violations;
    }
}
