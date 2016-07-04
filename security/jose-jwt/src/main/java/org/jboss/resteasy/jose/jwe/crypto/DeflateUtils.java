package org.jboss.resteasy.jose.jwe.crypto;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;


/**
 * Deflate (RFC 1951) utilities.
 *
 * @author Vladimir Dzhuvinov
 * @version $version$ (2013-04-16)
 */
public class DeflateUtils
{


	/**
	 * Omit headers and CRC fields from output, as specified by RFC 1950.
	 * Note that the Deflater JavaDocs are incorrect, see
	 * http://stackoverflow.com/questions/11076060/decompressing-gzipped-data-with-inflater-in-java
	 */
	private static final boolean NOWRAP = true;


	/**
	 * Compresses the specified byte array according to the DEFLATE 
	 * specification (RFC 1951).
	 *
	 * @param bytes The byte array to compress. Must not be {@code null}.
	 *
	 * @return The compressed bytes.
	 *
	 * @throws java.io.IOException If compression failed.
	 */
	public static byte[] compress(final byte[] bytes)
		throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		DeflaterOutputStream def = new DeflaterOutputStream(out, new Deflater(Deflater.DEFLATED, NOWRAP));
		def.write(bytes);
		def.close();

		return out.toByteArray();
	}


	/**
	 * Decompresses the specified byte array according to the DEFLATE
	 * specification (RFC 1951).
	 *
	 * @param bytes The byte array to decompress. Must not be {@code null}.
	 *
	 * @return The decompressed bytes.
	 *
	 * @throws java.io.IOException If decompression failed.
	 */
	public static byte[] decompress(final byte[] bytes)
		throws IOException {

		InflaterInputStream inf = new InflaterInputStream(new ByteArrayInputStream(bytes), new Inflater(NOWRAP));
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		// Transfer bytes from the compressed array to the output
		byte[] buf = new byte[1024];

		int len;

		while ((len = inf.read(buf)) > 0) {

			out.write(buf, 0, len);
		}

		inf.close();
		out.close();

		return out.toByteArray();
	}


	/**
	 * Prevents public instantiation.
	 */
	private DeflateUtils() {

	}
}
