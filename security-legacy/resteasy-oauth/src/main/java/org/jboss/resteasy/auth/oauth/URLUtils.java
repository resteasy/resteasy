package org.jboss.resteasy.auth.oauth;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * URL-encoding utility for each URL part according to the RFC specs
 * 
 * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">URI Generic Syntax</a>
 * @author <a href="mailto:stef@epardaud.fr">Stéphane Épardaud</a>
 */
public class URLUtils {

	/**
	 * gen-delims = ":" / "/" / "?" / "#" / "[" / "]" / "@"
	 */
	public final static BitSet GEN_DELIMS = new BitSet();
	static {
		GEN_DELIMS.set(':');
		GEN_DELIMS.set('/');
		GEN_DELIMS.set('?');
		GEN_DELIMS.set('#');
		GEN_DELIMS.set('[');
		GEN_DELIMS.set(']');
		GEN_DELIMS.set('@');
	}

	/**
	 * sub-delims = "!" / "$" / "{@literal &}" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
	 */
	public final static BitSet SUB_DELIMS = new BitSet();
	static {
		SUB_DELIMS.set('!');
		SUB_DELIMS.set('$');
		SUB_DELIMS.set('&');
		SUB_DELIMS.set('\'');
		SUB_DELIMS.set('(');
		SUB_DELIMS.set(')');
		SUB_DELIMS.set('*');
		SUB_DELIMS.set('+');
		SUB_DELIMS.set(',');
		SUB_DELIMS.set(';');
		SUB_DELIMS.set('=');
	}

	/**
	 * reserved = gen-delims | sub-delims
	 */
	public final static BitSet RESERVED = new BitSet();
	static {
		RESERVED.or(GEN_DELIMS);
		RESERVED.or(SUB_DELIMS);
	}

	/**
	 * lowalpha = "a" | "b" | "c" | "d" | "e" | "f" | "g" | "h" | "i" | "j" | "k" | "l" | "m" | "n" | "o" | "p" | "q" |
	 * "r" | "s" | "t" | "u" | "v" | "w" | "x" | "y" | "z"
	 */
	public final static BitSet LOW_ALPHA = new BitSet();
	static {
		LOW_ALPHA.set('a');
		LOW_ALPHA.set('b');
		LOW_ALPHA.set('c');
		LOW_ALPHA.set('d');
		LOW_ALPHA.set('e');
		LOW_ALPHA.set('f');
		LOW_ALPHA.set('g');
		LOW_ALPHA.set('h');
		LOW_ALPHA.set('i');
		LOW_ALPHA.set('j');
		LOW_ALPHA.set('k');
		LOW_ALPHA.set('l');
		LOW_ALPHA.set('m');
		LOW_ALPHA.set('n');
		LOW_ALPHA.set('o');
		LOW_ALPHA.set('p');
		LOW_ALPHA.set('q');
		LOW_ALPHA.set('r');
		LOW_ALPHA.set('s');
		LOW_ALPHA.set('t');
		LOW_ALPHA.set('u');
		LOW_ALPHA.set('v');
		LOW_ALPHA.set('w');
		LOW_ALPHA.set('x');
		LOW_ALPHA.set('y');
		LOW_ALPHA.set('z');
	}

	/**
	 * upalpha = "A" | "B" | "C" | "D" | "E" | "F" | "G" | "H" | "I" | "J" | "K" | "L" | "M" | "N" | "O" | "P" | "Q" |
	 * "R" | "S" | "T" | "U" | "V" | "W" | "X" | "Y" | "Z"
	 */
	public final static BitSet UP_ALPHA = new BitSet();
	static {
		UP_ALPHA.set('A');
		UP_ALPHA.set('B');
		UP_ALPHA.set('C');
		UP_ALPHA.set('D');
		UP_ALPHA.set('E');
		UP_ALPHA.set('F');
		UP_ALPHA.set('G');
		UP_ALPHA.set('H');
		UP_ALPHA.set('I');
		UP_ALPHA.set('J');
		UP_ALPHA.set('K');
		UP_ALPHA.set('L');
		UP_ALPHA.set('M');
		UP_ALPHA.set('N');
		UP_ALPHA.set('O');
		UP_ALPHA.set('P');
		UP_ALPHA.set('Q');
		UP_ALPHA.set('R');
		UP_ALPHA.set('S');
		UP_ALPHA.set('T');
		UP_ALPHA.set('U');
		UP_ALPHA.set('V');
		UP_ALPHA.set('W');
		UP_ALPHA.set('X');
		UP_ALPHA.set('Y');
		UP_ALPHA.set('Z');
	}

	/**
	 * alpha = lowalpha | upalpha
	 */
	public final static BitSet ALPHA = new BitSet();
	static {
		ALPHA.or(LOW_ALPHA);
		ALPHA.or(UP_ALPHA);
	}

	/**
	 * digit = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
	 */
	public final static BitSet DIGIT = new BitSet();
	static {
		DIGIT.set('0');
		DIGIT.set('1');
		DIGIT.set('2');
		DIGIT.set('3');
		DIGIT.set('4');
		DIGIT.set('5');
		DIGIT.set('6');
		DIGIT.set('7');
		DIGIT.set('8');
		DIGIT.set('9');
	}

	/**
	 * alphanum = alpha | digit
	 */
	public final static BitSet ALPHANUM = new BitSet();
	static {
		ALPHANUM.or(ALPHA);
		ALPHANUM.or(DIGIT);
	}

	/**
	 * unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"
	 */
	public final static BitSet UNRESERVED = new BitSet();
	static {
		UNRESERVED.or(ALPHA);
		UNRESERVED.or(DIGIT);
		UNRESERVED.set('-');
		UNRESERVED.set('.');
		UNRESERVED.set('_');
		UNRESERVED.set('~');
	}

	/**
	 * pchar = unreserved | escaped | sub-delims | ":" | "@"
	 * 
	 * Note: we don't allow escaped here since we will escape it ourselves, so we don't want to allow them in the
	 * unescaped sequences
	 */
	public final static BitSet PCHAR = new BitSet();
	static {
		PCHAR.or(UNRESERVED);
		PCHAR.or(SUB_DELIMS);
		PCHAR.set(':');
		PCHAR.set('@');
	}

	/**
	 * path_segment = pchar {@literal <without>} ";"
	 */
	public final static BitSet PATH_SEGMENT = new BitSet();
	static {
		PATH_SEGMENT.or(PCHAR);
		// deviate from the RFC in order to disallow the path param separator
		PATH_SEGMENT.clear(';');
	}

	/**
	 * path_param_name = pchar {@literal <without>} ";" | "="
	 */
	public final static BitSet PATH_PARAM_NAME = new BitSet();
	static {
		PATH_PARAM_NAME.or(PCHAR);
		// deviate from the RFC in order to disallow the path param separators
		PATH_PARAM_NAME.clear(';');
		PATH_PARAM_NAME.clear('=');
	}

	/**
	 * path_param_value = pchar {@literal <without>} ";"
	 */
	public final static BitSet PATH_PARAM_VALUE = new BitSet();
	static {
		PATH_PARAM_VALUE.or(PCHAR);
		// deviate from the RFC in order to disallow the path param separator
		PATH_PARAM_VALUE.clear(';');
	}

	/**
	 * query = pchar / "/" / "?"
	 */
	public final static BitSet QUERY = new BitSet();
	static {
		QUERY.or(PCHAR);
		QUERY.set('/');
		QUERY.set('?');
		// deviate from the RFC to disallow separators such as "=", "@" and the famous "+" which is treated as a space
		// when decoding
		QUERY.clear('=');
		QUERY.clear('&');
		QUERY.clear('+');
	}

	/**
	 * fragment = pchar / "/" / "?"
	 */
	public final static BitSet FRAGMENT = new BitSet();
	static {
		FRAGMENT.or(PCHAR);
		FRAGMENT.set('/');
		FRAGMENT.set('?');
	}

	/**
	 * Encodes a string to be a valid path parameter name, which means it can contain PCHAR* without "=" or ";". Uses
	 * UTF-8.
	 * @param pathParamName path parameter name
	 * @return encoded path parameter name
	 */
	public static String encodePathParamName(final String pathParamName) {
		try {
			return encodePart(pathParamName, StandardCharsets.UTF_8.name(), PATH_PARAM_NAME);
		}
		catch (final UnsupportedEncodingException e) {
			// should not happen
			throw new RuntimeException(e);
		}
	}

	/**
	 * Encodes a string to be a valid path parameter value, which means it can contain PCHAR* without ";". Uses UTF-8.
	 * @param pathParamValue path parameter value
	 * @return encoded path parameter value
	 */
	public static String encodePathParamValue(final String pathParamValue) {
		try {
			return encodePart(pathParamValue, StandardCharsets.UTF_8.name(), PATH_PARAM_VALUE);
		}
		catch (final UnsupportedEncodingException e) {
			// should not happen
			throw new RuntimeException(e);
		}
	}

	/**
	 * Encodes a string to be a valid query, which means it can contain PCHAR* | "?" | "/" without "=" | "{@literal &}" | "+". Uses
	 * UTF-8.
	 * @param queryNameOrValue query name/value
	 * @return encoded query name/value
	 */
	public static String encodeQueryNameOrValue(final String queryNameOrValue) {
		try {
			return encodePart(queryNameOrValue, StandardCharsets.UTF_8.name(), QUERY);
		}
		catch (final UnsupportedEncodingException e) {
			// should not happen
			throw new RuntimeException(e);
		}
	}

	/**
	 * Encodes a string to be a valid query with no parenthesis, which means it can contain PCHAR* | "?" | "/" without
	 * "=" | "{@literal &}" | "+" | "(" | ")". It strips parenthesis. Uses UTF-8.
	 * @param queryNameOrValueNoParen query name/value
	 * @return encoded query name/value
	 */
	public static String encodeQueryNameOrValueNoParen(final String queryNameOrValueNoParen) {
		try {
			String query = encodePart(queryNameOrValueNoParen, StandardCharsets.UTF_8.name(), QUERY);
			query = query.replace("(", "");
			return query.replace(")", "");
		}
		catch (final UnsupportedEncodingException e) {
			// should not happen
			throw new RuntimeException(e);
		}
	}

	/**
	 * Encodes a string to be a valid path segment, which means it can contain PCHAR* only (do not put path parameters or
	 * they will be escaped. Uses UTF-8.
	 * @param pathSegment path segment
	 * @return encoded path segment
	 */
	public static String encodePathSegment(final String pathSegment) {
		try {
			return encodePart(pathSegment, StandardCharsets.UTF_8.name(), PATH_SEGMENT);
		}
		catch (final UnsupportedEncodingException e) {
			// should not happen
			throw new RuntimeException(e);
		}
	}

	/**
	 * Encodes a string to be a valid URI part, with the given characters allowed. The rest will be encoded.
	 * @param part part
	 * @param charset charset
	 * @param allowed allowed characters
	 * @return encoded part
	 * @throws UnsupportedEncodingException if encoding is not supported
	 */
	public static String encodePart(final String part, final String charset, final BitSet allowed) throws UnsupportedEncodingException {
		if (part == null) {
			return null;
		}
		// start at *3 for the worst case when everything is %encoded on one byte
		final StringBuffer encoded = new StringBuffer(part.length() * 3);
		final char[] toEncode = part.toCharArray();
		for (final char c : toEncode) {
			if (allowed.get(c)) {
				encoded.append(c);
			}
			else {
				final byte[] bytes = String.valueOf(c).getBytes(charset);
				for (final byte b : bytes) {
					// make it unsigned
					final int u8 = b & 0xFF;
					encoded.append(String.format("%%%1$02X", u8));
				}
			}
		}
		return encoded.toString();
	}
}
