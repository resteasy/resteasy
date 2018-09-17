package org.jboss.resteasy.spi;

import java.lang.annotation.Annotation;

/**
 * Similar to StringConverter except specific to a parameter injection only.  It is annotation sensitive.
 * <p>
 * Instances of this class are created per parameter injection.
 * setAnnotations() is called when the object is instantiated
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @see StringConverter
 * @see org.jboss.resteasy.annotations.StringParameterUnmarshallerBinder
 */
public interface StringParameterUnmarshaller<T>
{
   void setAnnotations(Annotation[] annotations);

   T fromString(String str);
}
