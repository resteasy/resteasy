/**
 *
 */
package com.damnhandy.resteasy.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Classes marked with this annotation are identified as a resource and will respond 
 * to requests made to the URI value. A valid WebResource must define one or more {@link HttpMethod} 
 * annotations. If the {@link WebResources} annotation is used, each <code>WebResource</code> must define an ID 
 * value in order to distinguish multiple URI's from one another. 
 * </p>
 * 
 * @author Ryan J. McDonough
 * @since 1.0
 * Nov 6, 2006
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WebResource {
    public static final String DEFAULT_ID = "_DEFAULT_ID_VALUE_";
    /**
     * Defines the url template for this resource. This property is a path 
     * fragment which should begin after the ResourceDispatchServlet urlPattern
     * value. Assume the following URL:
     * 
     * <pre>http://localhost/resteasy-demo/app/contacts/33445</pre>
     * 
     * If your web-apps root context begins at <code>http://localhost/resteasy-demo</code> 
     * and the ResourceDispatchServlet url-pattern value is /app/*, the 
     * value should be <code>/contacts/12345</code>. 
     * <p>
     * Assuming that this is not a static resource and the that end of this URL
     * is an identifier for a given contact, we can substitute a path parameter
     * id so that it reads as follows: 
     * </p>
     * <pre>/contacts/{contactId}</pre>
     * <p>
     * At startup, RESTEasy will generate a regular expression to match this 
     * template and direct requests to this resource. The method which is 
     * marked with an {@link HttpResource} annotation should also identify which method
     * parameter maps to the path paramter using the PathParam annotion.
     * </p>
     * For example:
     * <pre>
     *  &#64;WebResource("/contacts/{contactId}")
     *  public class ContactResource {
     *
     *       &#64;HttpMethod(HttpMethod.GET)
     *       public Contact getContactById( &#64;URIParam("contactId") Integer id) {
     *           // ...look up a contact
     *           return contact;
     *       }
     * </pre>
     * <p></p>
     */
    public String value();
    
    /**
     * An optional attribute of type xsd:ID that identifies the resource element.
     * @return
     */
    public String id() default DEFAULT_ID;
    
    /**
     * Defines the media type for the query component of the resource URI. Defaults to ‘application/xwww-
	 * form-urlencoded’ if not specified which results in query strings being formatted as specified in
 	 * section 17.13 of HTML 4.01[4].
     * @return
     */
    public String queryType() default "application/x-www-form-urlencoded";
    
    
}
