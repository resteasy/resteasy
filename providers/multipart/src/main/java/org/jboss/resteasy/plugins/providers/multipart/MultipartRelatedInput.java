package org.jboss.resteasy.plugins.providers.multipart;

import java.util.Map;

/**
 * Represents a multipart/related (RFC2387) incoming mime message. A
 * multipart/related message is used to hold a root or start part and other
 * parts which are referenced from the root part. All parts have a unique id.
 * The type and the id of the start part is presented in parameters in the
 * message content-type header.
 * 
 * @author Attila Kiraly
 * @version $Revision: 1 $
 */
public interface MultipartRelatedInput extends MultipartInput {

	/**
	 * The type parameter as it was read from the content-type header of the
	 * multipart/related message. A well formed multipart/related message always
	 * has this parameter. This is the type of the root part of the message. If
	 * a content-type header is presented in the root part as well it should
	 * hold the same value.
	 * 
	 * @return the type parameter of the content-type header of the message,
	 *         null if there was no such parameter
	 */
	String getType();

	/**
	 * A start parameter is not mandatory in a message. If it is presented it
	 * holds the id of the root part.
	 * 
	 * @return the start parameter of the content-type header of the message,
	 *         null if there was no such parameter
	 */
	String getStart();

	/**
	 * Optional.
	 * 
	 * @return the start-info parameter of the content-type header of the
	 *         message, null if there was no such parameter
	 */
	String getStartInfo();

	/**
	 * @return the root part of the message. If a start parameter was set in the
	 *         message header the part with that id is returned. If no start
	 *         parameter was set the first part is returned.
	 */
	InputPart getRootPart();

	/**
	 * @return a map holding all parts with their unique id-s as keys. The root
	 *         part and the related parts too.
	 */
	Map<String, InputPart> getRelatedMap();
}
