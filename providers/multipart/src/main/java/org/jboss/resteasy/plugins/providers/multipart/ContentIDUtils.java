package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.plugins.providers.multipart.i18n.LogMessages;
import org.jboss.resteasy.plugins.providers.multipart.i18n.Messages;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Utility class to help generate, convert RFC compliant Content-ID and cid.
 * 
 * @author Attila Kiraly
 *
 */
public class ContentIDUtils {
	public static final String CID_URL_SCHEME = "cid:";

	/**
	 * Calls {@link #generateContentIDFromAddrSpec(String)} with
	 * {@link #generateRFC822AddrSpec()} as parameter.
	 * 
	 * @return the generated Content-ID
	 */
	public static String generateContentID() {
		return generateContentIDFromAddrSpec(generateRFC822AddrSpec());
	}

	/**
	 * Helper method to generate a standards-compliant Content-ID header value
	 * from the supplied addrSpec.
	 * 
	 * Used rfc-s: RFC2045, RFC822
	 * 
	 * @param addrSpec addrSpec
	 * @return the generated Content-ID
	 */
	public static String generateContentIDFromAddrSpec(String addrSpec) {
		return "<" + addrSpec + ">";
	}

	/**
	 * Helper method to generate a standards-compliant, random addr-spec as
	 * described in RFC822.
	 * 
	 * @return the generated addrSpec
	 */
	public static String generateRFC822AddrSpec() {
		return UUID.randomUUID().toString() + "@resteasy-multipart";
	}

	/**
	 * Helper method to generate a standards-compliant cid url from the supplied
	 * addrSpec. This implementation URL encodes everything without considering
	 * if it is needed or not.
	 * 
	 * Used rfc-s: RFC2392, RFC822
	 * 
	 * @param addrSpec addrSpec
	 * @return the generated Content-ID
	 */
	public static String generateCidFromAddrSpec(String addrSpec) {
		String cid = CID_URL_SCHEME;
		try {
			cid += URLEncoder.encode(addrSpec, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
		   LogMessages.LOGGER.error(Messages.MESSAGES.urlEncoderDoesNotSupportUtf8(), e);
		}
		return cid;
	}

	/**
	 * @param cid
	 *            the RFC2392 compliant cid
	 * @return the RFC822 defined addr-spec decoded from the cid
	 */
	public static String parseAddrSpecFromCid(String cid) {
		String addrSpec = cid.trim();
		if (addrSpec.startsWith(CID_URL_SCHEME))
			addrSpec = addrSpec.substring(CID_URL_SCHEME.length()).trim();

		try {
			addrSpec = URLDecoder.decode(addrSpec, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
		   LogMessages.LOGGER.error(Messages.MESSAGES.urlDecoderDoesNotSupportUtf8(), e);
		}

		return addrSpec;
	}

	/**
	 * @param contentID
	 *            the RFC2045 compliant Content-ID
	 * @return the RFC822 defined addr-spec decoded from the contentID
	 */
	public static String parseAddrSpecFromContentID(String contentID) {
		String addrSpec = contentID.trim();

		if (addrSpec.startsWith("<") && addrSpec.endsWith(">"))
			addrSpec = addrSpec.substring(1, addrSpec.length() - 1).trim();

		return addrSpec;
	}

	/**
	 * @param cid
	 *            the RFC2392 compliant cid
	 * @return the RFC2045 compliant Content-ID representing the cid
	 */
	public static String convertCidToContentID(String cid) {
		return generateContentIDFromAddrSpec(parseAddrSpecFromCid(cid));
	}

	/**
	 * @param contentID
	 *            the RFC2045 compliant Content-ID
	 * @return the RFC2392 compliant cid representing the contentID
	 */
	public static String convertContentIDToCid(String contentID) {
		return generateCidFromAddrSpec(parseAddrSpecFromContentID(contentID));
	}
}
