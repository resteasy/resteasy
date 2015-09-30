package org.jboss.resteasy.jose.jwe.crypto;


import org.jboss.resteasy.jose.i18n.Messages;
import org.jboss.resteasy.jose.jwe.CompressionAlgorithm;

/**
 * Deflate (RFC 1951) helper methods, intended for use by JWE encrypters and
 * decrypters.
 *
 * @author Vladimir Dzhuvinov
 * @version $version$ (2013-04-16)
 */
class DeflateHelper
{


	/**
	 * Applies compression to the specified plain text if requested.
	 *
	 * @param compressionAlg .
	 * @param bytes             The plain text bytes. Must not be 
	 *                          {@code null}.
	 *
	 * @return The bytes to encrypt.
	 *
	 * @throws RuntimeException If compression failed or the requested 
	 *                       compression algorithm is not supported.
	 */
	public static byte[] applyCompression(final CompressionAlgorithm compressionAlg, final byte[] bytes)
		throws RuntimeException {


		if (compressionAlg == null) {

			return bytes;

		} else if (compressionAlg.equals(CompressionAlgorithm.DEF)) {

			try {
				return DeflateUtils.compress(bytes);

			} catch (Exception e) {

	         throw new RuntimeException(Messages.MESSAGES.couldntCompressPlainText(e.getLocalizedMessage()), e);
			}

		} else {

		   throw new RuntimeException(Messages.MESSAGES.unsupportedCompressionAlgorithm(compressionAlg));
		}
	}


	/**
	 * Applies decompression to the specified plain text if requested.
	 *
	 * @param compressionAlg
	 * @param bytes             The plain text bytes. Must not be 
	 *                          {@code null}.
	 *
	 * @return The output bytes, decompressed if requested.
	 *
	 * @throws RuntimeException If decompression failed or the requested 
	 *                       compression algorithm is not supported.
	 */
	public static byte[] applyDecompression(final CompressionAlgorithm compressionAlg, final byte[] bytes)
		throws RuntimeException {


		if (compressionAlg == null) {

			return bytes;

		} else if (compressionAlg.equals(CompressionAlgorithm.DEF)) {

			try {
				return DeflateUtils.decompress(bytes);

			} catch (Exception e) {

			   throw new RuntimeException(Messages.MESSAGES.couldntDecompressPlainText(e.getLocalizedMessage()), e);
			}

		} else {

		   throw new RuntimeException(Messages.MESSAGES.unsupportedCompressionAlgorithm(compressionAlg));
		}
	}
}