package org.jboss.resteasy.plugins.providers.multipart;

import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface MultipartInput {

	List<InputPart> getParts();

	String getPreamble();

   /**
    * Call this method to delete any temporary files created from unmarshalling this multipart message
    * Otherwise they will be deleted on Garbage Collection or JVM exit.
    */
   void close();
}
