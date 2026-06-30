package org.jboss.resteasy.plugins.providers;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Predicate;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Variant;
import jakarta.ws.rs.core.Variant.VariantListBuilder;

import org.jboss.resteasy.spi.AsyncOutputStream;

/**
 * A utility class to provide supporting functionality to various
 * entity providers.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: $
 */
public final class ProviderHelper {

    private ProviderHelper() {

    }

    /**
     * @param in input stream
     * @return string data
     * @throws IOException if I/O error occurred
     */
    public static String readString(InputStream in) throws IOException {
        char[] buffer = new char[1024];
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        int wasRead = 0;
        do {
            wasRead = reader.read(buffer, 0, 1024);
            if (wasRead > 0) {
                builder.append(buffer, 0, wasRead);
            }
        } while (wasRead > -1);

        return builder.toString();
    }

    /**
     * @param in        input stream
     * @param mediaType media type
     * @return string data
     * @throws IOException if I/O error occurred
     */
    public static String readString(InputStream in, MediaType mediaType) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream builder = new ByteArrayOutputStream();
        int wasRead = 0;
        do {
            wasRead = in.read(buffer, 0, 1024);
            if (wasRead > 0) {
                builder.write(buffer, 0, wasRead);
            }
        } while (wasRead > -1);
        byte[] bytes = builder.toByteArray();

        String charset = mediaType.getParameters().get("charset");
        if (charset != null)
            return new String(bytes, charset);
        else
            return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * @param mediaTypes string array of media types
     * @return list of media types
     */
    public static List<MediaType> getAvailableMediaTypes(String[] mediaTypes) {
        List<MediaType> types = new ArrayList<MediaType>();
        for (String mediaType : mediaTypes) {
            types.add(MediaType.valueOf(mediaType));
        }
        return types;
    }

    /**
     * @param mediaTypes string array of media types
     * @return list of {@link Variant}
     */
    public static List<Variant> getAvailableVariants(String[] mediaTypes) {
        return getAvailableVariants(getAvailableMediaTypes(mediaTypes));
    }

    /**
     * @param mediaTypes list of media types
     * @return list of {@link Variant}
     */
    public static List<Variant> getAvailableVariants(List<MediaType> mediaTypes) {
        VariantListBuilder builder = Variant.VariantListBuilder.newInstance();
        MediaType[] types = mediaTypes.toArray(new MediaType[mediaTypes.size()]);
        builder.mediaTypes(types);
        return builder.build();
    }

    /**
     * @param in  input stream
     * @param out output stream
     * @throws IOException if I/O error occurred
     */
    public static void writeTo(final InputStream in, final OutputStream out) throws IOException {
        int read;
        final byte[] buf = new byte[2048];
        while ((read = in.read(buf)) != -1) {
            out.write(buf, 0, read);
        }
    }

    /**
     * @param in  input stream
     * @param out output stream
     * @throws IOException if I/O error occurred
     */
    public static CompletionStage<Void> writeToAndCloseInput(final InputStream in, final AsyncOutputStream out) {
        return writeTo(in, out).whenComplete((v, t) -> {
            try {
                in.close();
            } catch (IOException x) {
                throw new RuntimeException(x);
            }
        });
    }

    /**
     * @param in  input stream
     * @param out output stream
     * @throws IOException if I/O error occurred
     */
    public static CompletionStage<Void> writeTo(final InputStream in, final AsyncOutputStream out) {
        final byte[] buf = new byte[2048];
        return asyncWhile(
                read -> read != -1,
                read -> out.asyncWrite(buf, 0, read).thenApply(v -> asyncRead(in, buf)),
                asyncRead(in, buf)).thenApply(v -> null);
    }

    public static int asyncRead(InputStream in, byte[] buf) {
        try {
            return in.read(buf);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public static int asyncRead(InputStream in, byte[] buf, int offset, int length) {
        try {
            return in.read(buf, offset, length);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public static CompletionStage<Void> completedException(Throwable t) {
        CompletableFuture<Void> ret = new CompletableFuture<>();
        ret.completeExceptionally(t);
        return ret;
    }

    /**
     * Executes an asynchronous while-loop that evaluates a condition and processes iterations sequentially without
     * blocking the calling thread.
     * <p>
     * This method optimizes for performance by processing synchronous completions inline using a flat, non-recursive
     * loop layout. If an iteration completes asynchronously (e.g., waiting on network I/O), the current call stack is
     * immediately unwound, and subsequent iterations resume on the thread that completes the asynchronous boundary.
     * </p>
     * <p>
     * <strong>Threading and Context Note:</strong> Because synchronous paths execute on the initial calling thread,
     * this utility preserves {@link ThreadLocal} contexts for all immediate/fast-path operations.
     * </p>
     *
     * @param <T>       the type of the state/accumulator variable being processed through the loop
     * @param condition a predicate evaluated before each iteration to determine if the loop should continue
     * @param body      a function representing the loop body, accepting the current state and returning a
     *                  {@link CompletionStage} that resolves to the next iteration's state
     * @param initial   the starting value to pass into the first condition evaluation and loop iteration
     *
     * @return a {@link CompletionStage} that completes with the final state value when the {@code condition} evaluates
     *         to {@code false}, or completes exceptionally if any iteration or condition throws an exception
     */
    static <T> CompletionStage<T> asyncWhile(
            final Predicate<T> condition,
            final Function<T, CompletionStage<T>> body,
            final T initial) {

        final CompletableFuture<T> result = new CompletableFuture<>();

        // Simple mutable state tracker to wrap our variable inside the closure
        class LoopState {
            T current = initial;

            void run() {
                try {
                    // If the loop handles data synchronously, this flat 'while' loop keeps it on a single stack frame.
                    while (condition.test(current)) {
                        final CompletionStage<T> stage = body.apply(current);
                        final CompletableFuture<T> future = stage.toCompletableFuture();

                        if (future.isDone() && !future.isCompletedExceptionally()) {
                            // Fast Path: Data was written synchronously. Advance state, stay in while-loop.
                            current = future.join();
                        } else {
                            // Slow Path: Blocked/async, safely pause and register callback.
                            stage.thenAccept(next -> {
                                this.current = next;
                                run(); // Unwinds the old execution context and recurses on a completely fresh stack.
                            }).exceptionally(ex -> {
                                result.completeExceptionally(ex);
                                return null;
                            });
                            return; // Immediately drop out of the current stack frame.
                        }
                    }
                    result.complete(current);
                } catch (Throwable t) {
                    result.completeExceptionally(t);
                }
            }
        }
        // Kick off the loop
        new LoopState().run();
        return result;
    }
}
