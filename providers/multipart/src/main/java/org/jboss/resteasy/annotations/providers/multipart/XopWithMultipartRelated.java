package org.jboss.resteasy.annotations.providers.multipart;

import javax.xml.bind.annotation.XmlMimeType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to process/produce incoming/outgoing XOP messages
 * (packaged as multipart/related) to/from JAXB annotated objects.
 * 
 * <p>
 * XOP packaging can be suitable if you have to work with binary content and you
 * don't want to encode it (for example in base64 or hex inside an xml). XOP
 * makes it possible to pass binary through the network without any encoding on
 * it (binary contents will travel in separate parts of the multipart/related
 * message).
 * </p>
 * 
 * <p>
 * Example. A bean annotated with JAXB. {@link XmlMimeType} tells JAXB the mime
 * type of the binary content (its not required to do XOP packaging but it is
 * recommended to be set if you know the exact type):
 * 
 * <pre>
 * &#064;XmlRootElement
 * &#064;XmlAccessorType(XmlAccessType.FIELD)
 * public static class Xop {
 *   private Customer bill;
 * 
 *   private Customer monica;
 * 
 *   &#064;XmlMimeType(MediaType.APPLICATION_OCTET_STREAM)
 *   private byte[] myBinary;
 * 
 *   &#064;XmlMimeType(MediaType.APPLICATION_OCTET_STREAM)
 *   private DataHandler myDataHandler;
 * 
 *   // methods, other fields ...
 * }
 * </pre>
 * 
 * A REST Service method parameters/return value annotated with
 * {@link XopWithMultipartRelated} and consuming/producing
 * {@link org.jboss.resteasy.plugins.providers.multipart.MultipartConstants#MULTIPART_RELATED}:
 * 
 * <pre>
 * &#064;PUT
 * &#064;Path(&quot;xop&quot;)
 * &#064;Consumes(org.jboss.resteasy.plugins.providers.multipart.MultipartConstants.MULTIPART_RELATED)
 * public void putXopWithMultipartRelated(&#064;XopWithMultipartRelated Xop xop) {}
 * </pre>
 * 
 * <p>
 * More about XOP can be read here: <a
 * href="http://www.w3.org/TR/xop10">http://www.w3.org/TR/xop10</a>
 * </p>
 * 
 * @author Attila Kiraly
 * @version $Revision: 1 $
 */
@Target( { ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface XopWithMultipartRelated {

}
