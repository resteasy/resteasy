package org.jboss.resteasy.plugins.providers.multipart;

import javax.ws.rs.core.MediaType;

/**
 * Represents a multipart/related (RFC2387) outgoing mime message. A
 * multipart/related message is used to hold a root or start part and other
 * parts which are referenced from the root part. All parts have a unique id.
 * The type and the id of the start part is presented in parameters in the
 * message content-type header.
 * 
 * Usage is the same as with {@link MultipartOutput}:
 * 
 * <code>
 * MultipartRelatedDataOutput mrdo = new MultipartRelatedDataOutput();
 * mrdo.addPart(...);
 * </code>
 * 
 * The first added part will be used as root. The root parts content-type will
 * be used as the type parameter of the content-type of the mime message.
 * 
 * For parts without Content-ID header a unique id will be generated during
 * serialization.
 * 
 * @author Attila Kiraly
 * @version $Revision: 1 $
 */
public class MultipartRelatedOutput extends MultipartOutput {
	private String startInfo;

	/**
	 * The part that will be used as the root.
	 * 
	 * @return the first in the list of added parts.
	 */
	public OutputPart getRootPart() {
		return getParts().get(0);
	}

	/**
	 * Used to add parts to the multipart output message.
	 * 
	 * @param entity
	 *            the object representing the part's body
	 * @param mediaType
	 *            the Content-Type of the part
	 * @param contentId
	 *            the Content-ID to be used as identification for the current
	 *            part, optional, if null one will be generated
	 * @param contentTransferEncoding
	 *            The value to be used for the Content-Transfer-Encoding header
	 *            field of the part. It's optional, if you don't want to set
	 *            this pass null. Example values are: "7bit",
	 *            "quoted-printable", "base64", "8bit", "binary"
	 * @return {@link OutputPart}
	 */
	public OutputPart addPart(Object entity, MediaType mediaType,
			String contentId, String contentTransferEncoding) {
		OutputPart outputPart = super.addPart(entity, mediaType);
		if (contentTransferEncoding != null)
			outputPart.getHeaders().putSingle("Content-Transfer-Encoding",
					contentTransferEncoding);
		if (contentId != null)
			outputPart.getHeaders().putSingle("Content-ID", contentId);
		return outputPart;
	}

	/**
	 * Returns the start-info parameter of the Content-Type. This is an optional
	 * parameter.
	 * 
	 * As described in RFC2387:
	 * 
	 * 3.3. The Start-Info Parameter
	 * 
	 * Additional information can be provided to an application by the
	 * start-info parameter. It contains either a string or points, via a
	 * content-ID, to another MIME entity in the message. A typical use might be
	 * to provide additional command line parameters or a MIME entity giving
	 * auxiliary information for processing the compound object.
	 * 
	 * Applications that use Multipart/Related must specify the interpretation
	 * of start-info. User Agents shall provide the parameter's value to the
	 * processing application. Processes can distinguish a start-info reference
	 * from a token or quoted-string by examining the first non-white-space
	 * character, "&gt;" indicates a reference.
	 * 
	 * @return the currently configured start-info
	 */
	public String getStartInfo() {
		return startInfo;
	}

	/**
	 * Sets the start-info parameter of the Content-Type. This is an optional
	 * parameter.
	 * 
	 * As described in RFC2387:
	 * 
	 * 3.3. The Start-Info Parameter
	 * 
	 * Additional information can be provided to an application by the
	 * start-info parameter. It contains either a string or points, via a
	 * content-ID, to another MIME entity in the message. A typical use might be
	 * to provide additional command line parameters or a MIME entity giving
	 * auxiliary information for processing the compound object.
	 * 
	 * Applications that use Multipart/Related must specify the interpretation
	 * of start-info. User Agents shall provide the parameter's value to the
	 * processing application. Processes can distinguish a start-info reference
	 * from a token or quoted-string by examining the first non-white-space
	 * character, "&gt;" indicates a reference.
	 * 
	 * @param startInfo the value to be set
	 */
	public void setStartInfo(String startInfo) {
		this.startInfo = startInfo;
	}
}
