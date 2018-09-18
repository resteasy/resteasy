package org.jboss.resteasy.annotations.providers.img;

import javax.imageio.ImageWriteParam;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * An annotation that a resource class can use to pass parameters
 * to the {@link org.jboss.resteasy.plugins.providers.IIOImageProvider}.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: $
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ImageWriterParams
{

   /**
    * Specifies the compression quality of the image being written. By
    * default, the highest compression level is used. A float value
    * between 0.0f and 1.0f are acceptable. The default value is 1.0f;
    *
    * @return compression quality
    */
   float compressionQuality() default 1.0f;

   /**
    * Specifies the compression mode for the output image. By default,
    * it uses {@link javax.imageio.ImageWriteParam#MODE_COPY_FROM_METADATA}.
    *
    * @return compression mode
    */
   int compressionMode() default ImageWriteParam.MODE_COPY_FROM_METADATA;

}
