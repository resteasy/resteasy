package org.jboss.resteasy.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import jakarta.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderHelper {
    private static final Pattern DEFAULT_PATTERN = Pattern.compile(",");
    private static final Cache PATTERN_CACHE = new Cache(10);

    /**
     * Checks if the value of a header contains an entry separated by the {@code valueSeparatorRegex} parameter. If the
     * parameter is {@code null}, a &quot;,&quot; separator is used.
     *
     * @param value               the header value to check, if {@code null} {@code false} will be returned
     * @param valueSeparatorRegex the regex value used to parse the header string value into parts, if {@code null} a comma is
     *                            used
     * @param valuePredicate      the predicate used to check the values, cannot be {@code null}
     *
     * @return {@code true} if the header value exists and the predicate matches the whitespace-trimmed value.
     *
     * @see jakarta.ws.rs.core.HttpHeaders#containsHeaderString(String, String, Predicate)
     */
    public static boolean containsHeaderString(final String value, final String valueSeparatorRegex,
            final Predicate<String> valuePredicate) {
        if (value == null) {
            return false;
        }
        final Stream<String> parts;
        if (valueSeparatorRegex == null) {
            parts = Stream.of(value);
        } else if (",".equals(valueSeparatorRegex)) {
            parts = DEFAULT_PATTERN.splitAsStream(value);
        } else {
            parts = PATTERN_CACHE.computeIfAbsent(valueSeparatorRegex, (v) -> Pattern.compile(valueSeparatorRegex))
                    .splitAsStream(value);
        }
        return parts
                .map(String::trim)
                .anyMatch(Objects.requireNonNull(valuePredicate));
    }

    public static void setAllow(MultivaluedMap<String, Object> headers, String[] methods) {
        if (methods == null) {
            headers.remove("Allow");
            return;
        }
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (String l : methods) {
            if (isFirst) {
                isFirst = false;
            } else {
                builder.append(", ");
            }
            builder.append(l);
        }
        headers.putSingle("Allow", builder.toString());
    }

    public static void setAllow(MultivaluedMap<String, Object> headers, Set<String> methods) {
        if (methods == null) {
            headers.remove("Allow");
            return;
        }
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (String l : methods) {
            if (isFirst) {
                isFirst = false;
            } else {
                builder.append(", ");
            }
            builder.append(l);
        }
        headers.putSingle("Allow", builder.toString());
    }

    private static class Cache extends LinkedHashMap<String, Pattern> {
        private final int limit;
        private final ReadWriteLock lock;

        Cache(final int limit) {
            super(limit + 1);
            this.limit = limit;
            this.lock = new ReentrantReadWriteLock();
        }

        @Override
        protected boolean removeEldestEntry(final Map.Entry<String, Pattern> eldest) {
            return size() > limit;
        }

        @Override
        public Pattern get(final Object key) {
            lock.readLock().lock();
            try {
                return super.get(key);
            } finally {
                lock.readLock().unlock();
            }
        }

        @Override
        public Pattern put(final String key, final Pattern value) {
            lock.writeLock().lock();
            try {
                return super.put(key, value);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

}
