package org.jboss.resteasy.plugins.providers.multipart;

import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface MultipartInput {

	List<InputPart> getParts();

	String getPreamble();
}
