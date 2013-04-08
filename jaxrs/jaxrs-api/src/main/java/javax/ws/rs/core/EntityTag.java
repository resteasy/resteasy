/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

/**
 * An abstraction for the value of a HTTP Entity Tag, used as the value
 * of an ETag response header.
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.11">HTTP/1.1 section 3.11</a>
 * @since 1.0
 */
public class EntityTag {

    private static final HeaderDelegate<EntityTag> HEADER_DELEGATE =
            RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class);
    private String value;
    private boolean weak;

    /**
     * Creates a new instance of a strong {@code EntityTag}.
     *
     * @param value the value of the tag, quotes not included.
     * @throws IllegalArgumentException if value is {@code null}.
     */
    public EntityTag(final String value) {
        this(value, false);
    }

    /**
     * Creates a new instance of an {@code EntityTag}.
     *
     * @param value the value of the tag, quotes not included.
     * @param weak  {@code true} if this represents a weak tag, {@code false} otherwise.
     * @throws IllegalArgumentException if value is {@code null}.
     */
    public EntityTag(final String value, final boolean weak) {
        if (value == null) {
            throw new IllegalArgumentException("value==null");
        }
        this.value = value;
        this.weak = weak;
    }

    /**
     * Creates a new instance of {@code EntityTag} by parsing the supplied string.
     *
     * @param value the entity tag string.
     * @return the newly created entity tag.
     * @throws IllegalArgumentException if the supplied string cannot be parsed
     *                                  or is {@code null}.
     */
    public static EntityTag valueOf(final String value) {
        return HEADER_DELEGATE.fromString(value);
    }

    /**
     * Check the strength of an {@code EntityTag}.
     *
     * @return {@code true} if this represents a weak tag, {@code false} otherwise.
     */
    public boolean isWeak() {
        return weak;
    }

    /**
     * Get the value of an {@code EntityTag}.
     *
     * @return the value of the tag.
     */
    public String getValue() {
        return value;
    }

    /**
     * Compares {@code obj} to this tag to see if they are the same considering
     * weakness and value.
     *
     * @param obj the object to compare to.
     * @return {@code true} if the two tags are the same, {@code false} otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EntityTag)) {
            return super.equals(obj);
        }
        EntityTag other = (EntityTag) obj;
        if (value.equals(other.getValue()) && weak == other.isWeak()) {
            return true;
        }
        return false;
    }

    /**
     * Generate hashCode based on value and weakness.
     *
     * @return the entity tag hash code.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 17 * hash + (this.weak ? 1 : 0);
        return hash;
    }

    /**
     * Convert the entity tag to a string suitable for use as the value of the
     * corresponding HTTP header.
     *
     * @return a string version of the entity tag.
     */
    @Override
    public String toString() {
        return HEADER_DELEGATE.toString(this);
    }
}
