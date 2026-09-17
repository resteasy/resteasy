package org.jboss.resteasy.plugins.providers;

import java.awt.image.DataBuffer;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Variant;

import org.jboss.resteasy.annotations.providers.img.ImageWriterParams;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.config.Options;
import org.jboss.resteasy.spi.config.SizeUnit;
import org.jboss.resteasy.spi.config.Threshold;
import org.jboss.resteasy.spi.util.FindAnnotation;

/**
 * A IIOImageProviderHelper.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public final class IIOImageProviderHelper {

    private IIOImageProviderHelper() {
    }

    /**
     * FIXME Comment this
     *
     * @param mediaType media type
     * @return image writer
     */
    public static ImageWriter getImageWriterByMediaType(MediaType mediaType) {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(mediaType.toString());
        ImageWriter writer = writers.next();
        if (writer == null) {
            Response response = Response.serverError().entity("").build();
            throw new WebApplicationException(response);
        }
        return writer;
    }

    /**
     * Reads an image from the input stream with dimension validation to prevent decompression bomb attacks.
     *
     * @param in         input stream
     * @param reader     image reader
     * @param imageIndex index
     * @return {@link IIOImage}
     * @throws IOException if I/O error occurred
     */
    public static IIOImage readImage(InputStream in, ImageReader reader, int imageIndex)
            throws IOException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(in)) {
            reader.setInput(iis, false);

            // Read dimensions from header before allocating memory for the raster
            final int width = reader.getWidth(imageIndex);
            final int height = reader.getHeight(imageIndex);

            // Validate dimensions before the expensive readAll() call
            validateImageDimensions(reader, imageIndex, width, height);

            return reader.readAll(imageIndex, null);
        }
    }

    /**
     * FIXME Comment this
     *
     * @param mediaType media type
     * @return image reader
     */
    public static ImageReader getImageReaderByMediaType(MediaType mediaType) {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(mediaType.toString());

        ImageReader reader = null;
        while (readers.hasNext()) {
            reader = (ImageReader) readers.next();
        }

        if (reader == null) {
            String[] availableTypes = ImageIO.getReaderMIMETypes();
            LogMessages.LOGGER.readerNotFound(mediaType, availableTypes);
            List<Variant> variants = ProviderHelper.getAvailableVariants(availableTypes);
            Response response = Response.notAcceptable(variants).status(Status.NOT_ACCEPTABLE).build();
            throw new WebApplicationException(response);
        }
        return reader;
    }

    /**
     * FIXME Comment this
     *
     * @param annotations array of annotations
     * @param mediaType   media type
     * @param writer      image writer
     * @param out         output stream
     * @param image       {@link IIOImage}
     * @throws IOException if I/O error occurred
     */
    public static void writeImage(Annotation[] annotations,
            MediaType mediaType,
            ImageWriter writer,
            OutputStream out,
            IIOImage image)
            throws IOException {
        ImageWriteParam param;
        if (mediaType.equals(MediaType.valueOf("image/jpeg"))) {
            param = new JPEGImageWriteParam(Locale.US);
        } else {
            param = writer.getDefaultWriteParam();
        }

        /*
         * If the image output type supports compression, set it to the highest
         * maximum
         */
        ImageWriterParams writerParams = FindAnnotation.findAnnotation(annotations, ImageWriterParams.class);
        if (writerParams != null) {
            if (param.canWriteCompressed()) {
                param.setCompressionMode(writerParams.compressionMode());
                param.setCompressionQuality(writerParams.compressionQuality());
            }
        } else if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(1.0f);
        }
        BufferedOutputStream buff = new BufferedOutputStream(out, 2048);
        ImageOutputStream ios = ImageIO.createImageOutputStream(buff);
        try {
            writer.setOutput(ios);
            writer.write(null, image, param);
        } finally {
            writer.dispose();
        }
    }

    /**
     * Validates image dimensions to prevent decompression bomb attacks.
     * <p>
     * {@code readAll()} decodes the primary image <em>and</em> every thumbnail reported by
     * {@link ImageReader#getNumThumbnails(int)}, so the size budget must include them as well -- otherwise a tiny
     * primary image carrying a thumbnail with a falsified size bypasses the check entirely.
     * </p>
     * <p>
     * The estimate is computed with saturating arithmetic. A wrapped {@code long} could be negative, or could wrap far
     * enough to come back around to a small positive size. {@link Threshold#reached(long)} considers neither to be
     * reached, so an overflow would let the largest declared images through the check untouched.
     * </p>
     * <p>
     * Measuring the thumbnails assumes a reader whose {@link ImageReader#getThumbnailWidth(int, int)} and
     * {@link ImageReader#getThumbnailHeight(int, int)} answer from the image metadata, as the readers in the JDK do.
     * The implementations inherited from {@link ImageReader} decode the thumbnail in order to report its size, so a
     * reader which overrides neither allocates the thumbnail here instead of being measured. Nothing is lost by that,
     * as {@code readAll()} would decode the same thumbnail moments later, but no limit is enforced on the thumbnails
     * of such a reader either.
     * </p>
     *
     * @param reader     the reader the image will be read with
     * @param imageIndex index
     * @param width      image width in pixels
     * @param height     image height in pixels
     * @throws IOException                       if an I/O error occurs determining the thumbnail count or thumbnail dimensions
     * @throws jakarta.ws.rs.BadRequestException if dimensions are invalid or exceed configured limits
     */
    private static void validateImageDimensions(final ImageReader reader, final int imageIndex, final int width,
            final int height) throws IOException {
        final Threshold threshold = Options.MAX_IMAGE_THRESHOLD.getValue();
        // A disabled threshold opts out of the validation entirely. The deployment has taken responsibility for the
        // images it accepts, so no limit is imposed here, not even one which could not be decoded regardless.
        if (threshold.toBytes() == -1L) {
            return;
        }
        long calculatedSize = bytesForPixels(pixelCount(width, height), bitsPerPixel(reader, imageIndex));
        // Reject an oversized primary image before the reader is asked about its thumbnails
        checkThreshold(threshold, width, height, calculatedSize);
        final int numThumbnails = reader.getNumThumbnails(imageIndex);
        for (int i = 0; i < numThumbnails; i++) {
            final long thumbnailPixels = thumbnailPixelCount(reader.getThumbnailWidth(imageIndex, i),
                    reader.getThumbnailHeight(imageIndex, i));
            // Thumbnails are decoded to RGB rasters, 24 bits per pixel, regardless of the primary image's type.
            calculatedSize = saturatedAdd(calculatedSize, bytesForPixels(thumbnailPixels, 24L));
        }
        checkThreshold(threshold, width, height, calculatedSize);
    }

    /**
     * Rejects the image if the estimated size has reached the threshold.
     *
     * @param threshold      the threshold the estimate is measured against
     * @param width          the primary image width in pixels, for the error message
     * @param height         the primary image height in pixels, for the error message
     * @param calculatedSize the estimated size in bytes
     * @throws jakarta.ws.rs.BadRequestException if the estimate has reached the threshold
     */
    private static void checkThreshold(final Threshold threshold, final int width, final int height,
            final long calculatedSize) {
        if (threshold.reached(calculatedSize)) {
            throw Messages.MESSAGES.imageThresholdExceeded(width, height, SizeUnit.toHumanReadable(calculatedSize), threshold);
        }
    }

    /**
     * Determines how many bits each pixel requires once the image is decoded.
     * <p>
     * The image is read with a {@code null} {@link javax.imageio.ImageReadParam}, which decodes it to the first type
     * reported by {@link ImageReader#getImageTypes(int)}. That is also the type the {@link ImageReader} contract
     * describes as the most natural one for the image, so the estimate is based on it rather than on any of the other
     * types the reader offers.
     * </p>
     *
     * @param reader     the reader the image will be read with
     * @param imageIndex index
     * @return the number of bits per pixel, falling back to 32 bits if the reader cannot describe the image type
     */
    private static long bitsPerPixel(final ImageReader reader, final int imageIndex) {
        try {
            final Iterator<ImageTypeSpecifier> imageTypes = reader.getImageTypes(imageIndex);
            if (imageTypes.hasNext()) {
                return bitsPerPixel(imageTypes.next().getSampleModel());
            }
        } catch (IOException | RuntimeException e) {
            // The image type could not be determined, fall back to assuming an 8 bit per sample RGBA image
            LogMessages.LOGGER.debugf(e, "Could not determine the image type for image %d, assuming 32 bits per pixel.",
                    imageIndex);
        }
        return 32L;
    }

    /**
     * Determines how many bits each pixel of the sample model requires.
     *
     * @param sampleModel the sample model of the type the image will be decoded to
     * @return the number of bits per pixel
     */
    private static long bitsPerPixel(final SampleModel sampleModel) {
        // Sub-byte formats, e.g. bilevel, pack several pixels into one data element. The size of the element would
        // overstate those by up to a factor of eight, so use the stride of a single pixel instead.
        if (sampleModel instanceof MultiPixelPackedSampleModel) {
            final MultiPixelPackedSampleModel packed = (MultiPixelPackedSampleModel) sampleModel;
            return packed.getPixelBitStride();
        }
        return (long) DataBuffer.getDataTypeSize(sampleModel.getDataType()) * sampleModel.getNumDataElements();
    }

    /**
     * Converts a pixel count to the number of bytes required to store it, rounding up.
     *
     * @param pixels       the number of pixels
     * @param bitsPerPixel the number of bits each pixel requires
     * @return the number of bytes required
     */
    private static long bytesForPixels(final long pixels, final long bitsPerPixel) {
        return saturatedAdd(saturatedMultiply(pixels, bitsPerPixel), 7L) / 8L;
    }

    /**
     * Returns the number of pixels the dimensions describe.
     * <p>
     * Non-positive dimensions are rejected rather than tolerated. A negative dimension would contribute a negative
     * amount to the estimate, shrinking the total and masking an image which should have been rejected.
     * </p>
     *
     * @param width  the width in pixels
     * @param height the height in pixels
     * @return the total number of pixels
     * @throws jakarta.ws.rs.BadRequestException if the dimensions are not positive
     */
    private static long pixelCount(final int width, final int height) {
        if (width <= 0 || height <= 0) {
            throw Messages.MESSAGES.invalidImageDimensions(width, height);
        }
        return (long) width * height;
    }

    /**
     * Returns the number of pixels the thumbnail dimensions describe.
     * <p>
     * Rejected on the same terms as the primary image, but reported as a thumbnail. A JFIF thumbnail whose dimensions
     * are absent is reported as 0x0, and quoting that back to a client which sent, say, a 4000x3000 photograph only
     * invites the wrong investigation.
     * </p>
     *
     * @param width  the thumbnail width in pixels
     * @param height the thumbnail height in pixels
     * @return the total number of pixels
     * @throws jakarta.ws.rs.BadRequestException if the dimensions are not positive
     */
    private static long thumbnailPixelCount(final int width, final int height) {
        if (width <= 0 || height <= 0) {
            throw Messages.MESSAGES.invalidThumbnailDimensions(width, height);
        }
        return (long) width * height;
    }

    /**
     * Multiplies the two values, returning {@link Long#MAX_VALUE} rather than wrapping on overflow.
     *
     * @param value      the value to multiply
     * @param multiplier the multiplier
     * @return the product, or {@link Long#MAX_VALUE} if the product does not fit in a {@code long}
     */
    private static long saturatedMultiply(final long value, final long multiplier) {
        try {
            return Math.multiplyExact(value, multiplier);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    /**
     * Adds the two values, returning {@link Long#MAX_VALUE} rather than wrapping on overflow.
     *
     * @param value  the value to add to
     * @param augend the value to add
     * @return the sum, or {@link Long#MAX_VALUE} if the sum does not fit in a {@code long}
     */
    private static long saturatedAdd(final long value, final long augend) {
        try {
            return Math.addExact(value, augend);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }
}
