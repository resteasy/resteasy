/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.providers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dev.resteasy.junit.extension.annotations.RequestPath;
import dev.resteasy.junit.extension.annotations.RestBootstrap;
import dev.resteasy.junit.extension.annotations.RestResource;

/**
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@RestBootstrap(ImageResourceTest.ImageResource.class)
class ImageResourceTest {
    @TempDir
    private static Path tempDir;

    private static Path tempFile;

    private static Path deepTempFile;

    private static Path overflowTempFile;

    private static Path thumbnailBombFile;

    private static Path smallPngFile;

    private static Path smallJpegWithThumbnailFile;

    @BeforeAll
    static void createFile() throws IOException {
        // Declare a huge image (20000 x 20000). At 4 bytes/pixel (RGBA) that is a ~1.6 GB raster
        // the server must allocate when it decodes the image -- the actual denial-of-service.
        tempFile = writeBomb(tempDir.resolve("bomb.png"), 20000, 20000, 8);
        // 6144 x 6144 is deliberately sized to straddle the 200 MB default threshold: assuming 4 bytes/pixel
        // (RGBA) it estimates to 144 MB and would be accepted. At 16 bits per sample the reader decodes to
        // 8 bytes/pixel, making the real raster 288 MB, which must be rejected. The size therefore has to be
        // estimated from the type the reader will actually decode to rather than assumed to be 8 bit RGBA.
        deepTempFile = writeBomb(tempDir.resolve("deep-bomb.png"), 6144, 6144, 16);
        // 600000000 x 600000000 is 3.6e17 pixels, which at 4 bytes/pixel (RGBA) is 1.15e19 bytes -- more than a long
        // can hold. Computed without saturating arithmetic the estimate wraps to -865843009213693951, a negative size
        // no threshold considers reached, so the guard passes the image on to be decoded. The reader then rejects it
        // itself -- every reader in the JDK refuses a pixel count above Integer.MAX_VALUE -- so the request fails
        // either way, but as a 500 raised by the reader rather than a 400 raised here. This asserts the estimate stays
        // saturated so the rejection remains ours to make, rather than depending on a limit inside the reader. The
        // declared dimensions are the only difference between this file and bomb.png, which is rejected either way.
        overflowTempFile = writeBomb(tempDir.resolve("overflow-bomb.png"), 600_000_000, 600_000_000, 8);
        thumbnailBombFile = writeThumbnailBomb(tempDir.resolve("thumb-bomb.jpg"));
        smallPngFile = tempDir.resolve("small.png");
        ImageIO.write(new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB), "png", smallPngFile.toFile());
        smallJpegWithThumbnailFile = writeJpegWithThumbnail(tempDir.resolve("small-with-thumbnail.jpg"));
    }

    @Test
    void image(@RestResource @RequestPath("/image") final WebTarget target) throws Exception {
        assertRejected(target, tempFile, "image/png");
    }

    @Test
    void deepImage(@RestResource @RequestPath("/image") final WebTarget target) throws Exception {
        assertRejected(target, deepTempFile, "image/png");
    }

    @Test
    void overflowImage(@RestResource @RequestPath("/image") final WebTarget target) throws Exception {
        assertRejected(target, overflowTempFile, "image/png");
    }

    @Test
    void thumbnailImage(@RestResource @RequestPath("/image") final WebTarget target) throws Exception {
        // The primary image in this file is 1x1, so a guard which measures only the primary image accepts the request
        // and readAll() then allocates the thumbnail's ~2 GB raster.
        assertRejected(target, thumbnailBombFile, "image/jpeg");
    }

    @Test
    void smallImage(@RestResource @RequestPath("/image") final WebTarget target) throws Exception {
        assertAccepted(target, smallPngFile, "image/png");
    }

    @Test
    void smallImageWithThumbnail(@RestResource @RequestPath("/image") final WebTarget target) throws Exception {
        // A genuine, correctly-sized thumbnail -- as most camera/phone JPEGs carry -- must not be
        // rejected. This is the positive-path counterpart to thumbnailImage(): it proves the guard
        // sums real thumbnail sizes into the budget without over-rejecting ordinary JPEGs.
        assertAccepted(target, smallJpegWithThumbnailFile, "image/jpeg");
    }

    private static void assertRejected(final WebTarget target, final Path file, final String mediaType) throws IOException {
        // POST the tiny file; decoding it server-side triggers the huge allocation.
        try (
                Response response = target.request()
                        .post(Entity.entity(Files.newInputStream(file), mediaType))) {
            assertEquals(400, response.getStatus(),
                    String.format("Expected a 400 status for exceeding the default image threshold, got %d: %s",
                            response.getStatus(), response.readEntity(String.class)));
        }
    }

    private static void assertAccepted(final WebTarget target, final Path file, final String mediaType) throws IOException {
        try (
                Response response = target.request()
                        .post(Entity.entity(Files.newInputStream(file), mediaType))) {
            final String body = response.readEntity(String.class);
            assertEquals(200, response.getStatus(),
                    String.format("Expected a 200 status for a legitimate image, got %d: %s", response.getStatus(), body));
            assertEquals(String.format("decoded %dx%d", 4, 4), body);
        }
    }

    // Writes one PNG chunk in its on-the-wire form: length (4B) + type (4B) + data + CRC (4B).
    private static void chunk(final OutputStream o, final String t, final byte[] d) throws IOException {
        // Length field: the number of DATA bytes only (big-endian); it excludes the type and CRC.
        o.write(new byte[] { (byte) (d.length >>> 24), (byte) (d.length >>> 16), (byte) (d.length >>> 8), (byte) d.length });
        try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            // The CRC covers type + data together, so gather those bytes contiguously first.
            b.write(t.getBytes(StandardCharsets.US_ASCII));
            b.write(d);
            final byte[] tc = b.toByteArray();
            o.write(tc); // emit the type + data bytes
            // Trailing checksum: CRC-32 of those same type + data bytes, written big-endian.
            final CRC32 c = new CRC32();
            c.update(tc);
            final long v = c.getValue();
            o.write(new byte[] { (byte) (v >>> 24), (byte) (v >>> 16), (byte) (v >>> 8), (byte) v });
        }
    }

    private static Path writeBomb(final Path file, final int width, final int height, final int bitDepth)
            throws IOException {
        // Build a minimal but well-formed PNG on disk. A PNG is an 8-byte signature followed by
        // a series of "chunks" (IHDR, IDAT, IEND).
        try (
                OutputStream o = Files.newOutputStream(file, StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
                ByteArrayOutputStream ihdr = new ByteArrayOutputStream()) {
            // The fixed 8-byte signature that every PNG file must start with.
            o.write(new byte[] { (byte) 137, 80, 78, 71, 13, 10, 26, 10 });
            // IHDR payload -- width as 4 big-endian bytes...
            ihdr.write(new byte[] { (byte) (width >>> 24), (byte) (width >>> 16), (byte) (width >>> 8), (byte) width });
            // ...height as 4 big-endian bytes...
            ihdr.write(new byte[] { (byte) (height >>> 24), (byte) (height >>> 16), (byte) (height >>> 8), (byte) height });
            // ...then the bit depth, color type 6 (RGBA), compression 0, filter 0, interlace 0.
            ihdr.write(new byte[] { (byte) bitDepth, 6, 0, 0, 0 });
            // Wrap that 13-byte payload as the IHDR chunk (adds length + type + CRC around it).
            chunk(o, "IHDR", ihdr.toByteArray());
            // IDAT holds the pixel data. We supply only a few compressed bytes, so the file stays
            // tiny even though IHDR claims a giant image -- that size mismatch is the "bomb".
            final Deflater d = new Deflater();
            d.setInput(new byte[16]); // 16 zero bytes standing in for pixel data
            d.finish();
            final byte[] buf = new byte[64];
            int n = d.deflate(buf); // zlib-compress those bytes into buf
            chunk(o, "IDAT", Arrays.copyOf(buf, n));
            // IEND marks the end of the PNG stream (no data).
            chunk(o, "IEND", new byte[0]);
        }
        return file;
    }

    // Builds a JPEG whose *primary* image is tiny (1x1) but which carries a JFIF/JFXX thumbnail
    // whose header lies about being 26000 x 26000. readAll() decodes thumbnails as well as the
    // primary image, so a size guard that only inspects the primary image's dimensions never sees
    // the ~2 GB raster the thumbnail allocation actually requires.
    private static Path writeThumbnailBomb(final Path file) throws IOException {
        final byte[] thumbnail = buildJpeg(8, 8);
        patchSofDimensions(thumbnail, 26000, 26000);
        final byte[] outer = buildJpeg(1, 1);
        final byte[] evil = withJfxxThumbnail(outer, thumbnail);
        Files.write(file, evil, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return file;
    }

    // Same construction as writeThumbnailBomb(), but the thumbnail's declared dimensions are left
    // alone -- a genuine 8x8 thumbnail on a genuine 4x4 primary image, both truthful.
    private static Path writeJpegWithThumbnail(final Path file) throws IOException {
        final byte[] thumbnail = buildJpeg(8, 8);
        final byte[] outer = buildJpeg(4, 4);
        final byte[] withThumbnail = withJfxxThumbnail(outer, thumbnail);
        Files.write(file, withThumbnail, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return file;
    }

    private static byte[] buildJpeg(final int width, final int height) throws IOException {
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", bos);
            return bos.toByteArray();
        }
    }

    // Overwrites the height/width fields of the first SOF marker (0xFFC0/0xFFC2) in place, leaving
    // every other byte -- quantization/Huffman tables, entropy-coded scan data -- untouched. The
    // mismatch between the declared size and the actual (tiny) scan data is the "bomb": readers
    // allocate the raster from the SOF dimensions before they ever look at the scan data.
    private static void patchSofDimensions(final byte[] jpeg, final int width, final int height) {
        int i = 2; // skip the SOI marker
        while (i < jpeg.length - 1) {
            final int marker = ((jpeg[i] & 0xFF) << 8) | (jpeg[i + 1] & 0xFF);
            if (marker == 0xFFC0 || marker == 0xFFC2) {
                jpeg[i + 5] = (byte) (height >>> 8);
                jpeg[i + 6] = (byte) height;
                jpeg[i + 7] = (byte) (width >>> 8);
                jpeg[i + 8] = (byte) width;
                return;
            }
            if (marker == 0xFFDA) {
                break;
            }
            final int len = ((jpeg[i + 2] & 0xFF) << 8) | (jpeg[i + 3] & 0xFF);
            i += 2 + len;
        }
        throw new IllegalStateException("No SOF marker found");
    }

    // Inserts a JFXX (THUMB_JPEG) extension segment carrying thumbnailJpeg as its payload,
    // immediately after the JFIF APP0 segment the JDK's JPEG writer always emits first.
    private static byte[] withJfxxThumbnail(final byte[] outerJpeg, final byte[] thumbnailJpeg) throws IOException {
        final int app0Length = ((outerJpeg[4] & 0xFF) << 8) | (outerJpeg[5] & 0xFF);
        final int insertAt = 2 + 2 + app0Length; // SOI + APP0 marker/length + APP0 payload
        try (ByteArrayOutputStream payload = new ByteArrayOutputStream()) {
            payload.write("JFXX".getBytes(StandardCharsets.US_ASCII));
            payload.write(0);
            payload.write(0x10); // THUMB_JPEG
            payload.write(thumbnailJpeg);
            final byte[] payloadBytes = payload.toByteArray();
            final int segmentLength = payloadBytes.length + 2; // length field covers itself
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                out.write(outerJpeg, 0, insertAt);
                out.write(0xFF);
                out.write(0xE0);
                out.write(segmentLength >>> 8);
                out.write(segmentLength);
                out.write(payloadBytes);
                out.write(outerJpeg, insertAt, outerJpeg.length - insertAt);
                return out.toByteArray();
            }
        }
    }

    @jakarta.ws.rs.Path("/image")
    public static class ImageResource {
        @POST
        @Consumes("image/*")
        @Produces(MediaType.TEXT_PLAIN)
        public String img(final IIOImage image) {
            return String.format("decoded %dx%d", image.getRenderedImage().getWidth(), image.getRenderedImage().getHeight());
        }
    }
}
