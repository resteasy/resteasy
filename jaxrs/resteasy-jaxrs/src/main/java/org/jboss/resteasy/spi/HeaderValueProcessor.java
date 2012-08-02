package org.jboss.resteasy.spi;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface HeaderValueProcessor
{
   /**
    * Convert an object to a header string.  First try StringConverter, then HeaderDelegate, then object.toString()
    *
    * @param object
    * @return
    */
   String toHeaderString(Object object);
}
