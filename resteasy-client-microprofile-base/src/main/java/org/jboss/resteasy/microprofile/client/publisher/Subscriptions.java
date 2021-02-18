package org.jboss.resteasy.microprofile.client.publisher;

import java.util.concurrent.atomic.AtomicLong;

public class Subscriptions {

    /**
     * Atomically adds the positive value n to the requested value in the AtomicLong and
     * caps the result at Long.MAX_VALUE and returns the previous value.
     *
     * @param requested the AtomicLong holding the current requested value
     * @param requests the value to add, must be positive (not verified)
     * @return the original value before the add
     */
    public static long add(AtomicLong requested, long requests) {
        for (;;) {
            long r = requested.get();
            if (r == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }
            long u = add(r, requests);
            if (requested.compareAndSet(r, u)) {
                return r;
            }
        }
    }

    /**
     * Adds two long values and caps the sum at Long.MAX_VALUE.
     *
     * @param a the first value
     * @param b the second value
     * @return the sum capped at Long.MAX_VALUE
     */
    public static long add(long a, long b) {
        long u = a + b;
        if (u < 0L) {
            return Long.MAX_VALUE;
        }
        return u;
    }


    /**
     * Concurrent subtraction bound to 0, mostly used to decrement a request tracker by
     * the amount produced by the operator.
     *
     * @param requested the atomic long keeping track of requests
     * @param amount delta to subtract
     * @return value after subtraction or zero
     */
    public static long produced(AtomicLong requested, long amount) {
        long r;
        long u;
        do {
            r = requested.get();
            if (r == 0 || r == Long.MAX_VALUE) {
                return r;
            }
            u = subOrZero(r, amount);
        } while (!requested.compareAndSet(r, u));

        return u;
    }

    /**
     * Cap a subtraction to 0
     *
     * @param a left operand
     * @param b right operand
     * @return Subtraction result or 0 if overflow
     */
    public static long subOrZero(long a, long b) {
        long res = a - b;
        if (res < 0L) {
            return 0;
        }
        return res;
    }


}
