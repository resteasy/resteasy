/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response;
import static javax.ws.rs.core.HttpHeaders.WWW_AUTHENTICATE;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * A runtime exception indicating request authorization failure caused by one of the following
 * scenarios:
 * <ul>
 * <li>
 * a client did not send the required authorization credentials to access the requested resource,
 * i.e. {@link javax.ws.rs.core.HttpHeaders#AUTHORIZATION Authorization} HTTP header is missing
 * in the request,
 * </li>
 * <li>
 * or - in case the request already contains the HTTP {@code Authorization} header - then
 * the exception indicates that authorization has been refused for the credentials contained
 * in the request header.
 * </li>
 * </ul>
 *
 * @author Marek Potociar
 * @since 2.0
 */
public class NotAuthorizedException extends ClientErrorException {

    private static final long serialVersionUID = -3156040750581929702L;

    private transient List<Object> challenges;

    /**
     * Construct a new "not authorized" exception.
     *
     * @param challenge      authorization challenge applicable to the resource requested
     *                       by the client.
     * @param moreChallenges additional authorization challenge applicable to the
     *                       requested resource.
     * @throws NullPointerException in case the {@code challenge} parameter is {@code null}.
     */
    public NotAuthorizedException(Object challenge, Object... moreChallenges) {
        super(createUnauthorizedResponse(challenge, moreChallenges));
        this.challenges = cacheChallenges(challenge, moreChallenges);
    }

    /**
     * Construct a new "not authorized" exception.
     *
     * @param message        the detail message (which is saved for later retrieval
     *                       by the {@link #getMessage()} method).
     * @param challenge      authorization challenge applicable to the resource requested
     *                       by the client.
     * @param moreChallenges additional authorization challenge applicable to the
     *                       requested resource.
     * @throws NullPointerException in case the {@code challenge} parameter is {@code null}.
     */
    public NotAuthorizedException(String message, Object challenge, Object... moreChallenges) {
        super(message, createUnauthorizedResponse(challenge, moreChallenges));
        this.challenges = cacheChallenges(challenge, moreChallenges);
    }

    /**
     * Construct a new "not authorized" exception.
     *
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 401}.
     */
    public NotAuthorizedException(Response response) {
        super(validate(response, UNAUTHORIZED));
    }

    /**
     * Construct a new "not authorized" exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 401}.
     */
    public NotAuthorizedException(String message, Response response) {
        super(message, validate(response, UNAUTHORIZED));
    }

    /**
     * Construct a new "not authorized" exception.
     *
     * @param cause          the underlying cause of the exception.
     * @param challenge      authorization challenge applicable to the requested resource.
     * @param moreChallenges additional authorization challenge applicable to the
     *                       requested resource.
     */
    public NotAuthorizedException(Throwable cause, Object challenge, Object... moreChallenges) {
        super(createUnauthorizedResponse(challenge, moreChallenges), cause);
        this.challenges = cacheChallenges(challenge, moreChallenges);
    }

    /**
     * Construct a new "not authorized" exception.
     *
     * @param message        the detail message (which is saved for later retrieval
     *                       by the {@link #getMessage()} method).
     * @param cause          the underlying cause of the exception.
     * @param challenge      authorization challenge applicable to the requested resource.
     * @param moreChallenges additional authorization challenge applicable to the
     *                       requested resource.
     */
    public NotAuthorizedException(String message, Throwable cause, Object challenge, Object... moreChallenges) {
        super(message, createUnauthorizedResponse(challenge, moreChallenges), cause);
        this.challenges = cacheChallenges(challenge, moreChallenges);
    }

    /**
     * Construct a new "not authorized" exception.
     *
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 401}.
     */
    public NotAuthorizedException(Response response, Throwable cause) {
        super(validate(response, UNAUTHORIZED), cause);
    }

    /**
     * Construct a new "not authorized" exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 401}.
     */
    public NotAuthorizedException(String message, Response response, Throwable cause) {
        super(message, validate(response, UNAUTHORIZED), cause);
    }

    /**
     * Get the list of authorization challenges associated with the exception and
     * applicable to the resource requested by the client.
     *
     * @return list of authorization challenges applicable to the resource requested
     *         by the client.
     */
    public List<Object> getChallenges() {
        if (challenges == null) {
            this.challenges = getResponse().getHeaders().get(WWW_AUTHENTICATE);
        }
        return challenges;
    }

    private static Response createUnauthorizedResponse(Object challenge, Object[] otherChallenges) {
        if (challenge == null) {
            throw new NullPointerException("Primary challenge parameter must not be null.");
        }

        Response.ResponseBuilder builder = Response.status(UNAUTHORIZED)
                .header(WWW_AUTHENTICATE, challenge);

        if (otherChallenges != null) {
            for (Object oc : otherChallenges) {
                builder.header(WWW_AUTHENTICATE, oc);
            }
        }

        return builder.build();
    }

    private static List<Object> cacheChallenges(Object challenge, Object[] moreChallenges) {
        List<Object> temp = new ArrayList<Object>(1 + ((moreChallenges == null) ? 0 : moreChallenges.length));
        temp.add(challenge);
        if (moreChallenges != null) {
            temp.addAll(Arrays.asList(moreChallenges));
        }
        return Collections.unmodifiableList(temp);
    }
}
