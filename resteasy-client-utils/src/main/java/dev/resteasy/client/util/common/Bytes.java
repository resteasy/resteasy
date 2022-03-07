/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.resteasy.client.util.common;

/**
 * Simple byte utilities.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class Bytes {

    private static final char[] HEX_TABLE = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * Converts to the byte array into a hexadecimal string.
     *
     * @param bytes the bytes to convert
     *
     * @return the bytes in a hexadecimal string
     */
    public static String bytesToHexString(final byte[] bytes) {
        final StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            builder.append(HEX_TABLE[b >> 4 & 0x0f]).append(HEX_TABLE[b & 0x0f]);
        }
        return builder.toString();
    }

    /**
     * Converts the character array into a byte array.
     *
     * @param chars the characters to convert
     *
     * @return the characters as a byte array.
     */
    public static byte[] charToBytes(final char[] chars) {
        final ByteStringBuilder builder = new ByteStringBuilder(chars.length);
        for (char c : chars) {
            builder.append(c);
        }
        return builder.toArray();
    }
}
