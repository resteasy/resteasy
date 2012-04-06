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

package javax.ws.rs.core;

import java.security.Principal;

/**
 * An injectable interface that provides access to security related
 * information.
 *
 * @see Context
 */
public interface SecurityContext
{

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
    * <p/>
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
