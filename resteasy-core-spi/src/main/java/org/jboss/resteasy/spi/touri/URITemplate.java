package org.jboss.resteasy.spi.touri;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * This annotation can be added to any object to perform Java beans-based
 * transformation between URI template and bean properties. For example, take
 * the following bean:
 * </p>
 * <pre>
 * &#064;URITemplate(&quot;/foo/{id}&quot;)
 * public class Foo
 * {
 *    private int id;
 *    // getters and setters
 * }
 * </pre>
 * <p>
 * for a Foo f with id = 123, ObjectToURI.getInstance(f) = "/foo/123"
 * </p>
 *
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */

@Target(
        {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface URITemplate
{
   String value();
}
