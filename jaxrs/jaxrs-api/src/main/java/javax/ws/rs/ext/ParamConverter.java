/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 Oracle and/or its affiliates. All rights reserved.
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
package javax.ws.rs.ext;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.DefaultValue;

/**
 * Defines a contract for a delegate responsible for converting between a
 * {@code String} form of a message parameter value and the corresponding custom
 * Java type {@code T}. Conversion of message parameter values injected via
 * {@link javax.ws.rs.PathParam &#64;PathParam}, {@link javax.ws.rs.QueryParam &#64;QueryParam},
 * {@link javax.ws.rs.MatrixParam &#64;MatrixParam}, {@link javax.ws.rs.FormParam &#64;FormParam},
 * {@link javax.ws.rs.CookieParam &#64;CookieParam} and {@link javax.ws.rs.HeaderParam &#64;HeaderParam}
 * is supported.
 * <p>
 * By default, when used for injection of parameter values, a selected {@code ParamConverter}
 * instance MUST be used eagerly by a JAX-RS runtime to convert any {@link DefaultValue
 * default value} in the resource or provider model, that is during the application deployment,
 * before any value &ndash; default or otherwise &ndash; is actually required.
 * This conversion strategy ensures that any errors in the default values are reported
 * as early as possible.
 * This default behavior may be overridden by annotating the {@code ParamConverter}
 * implementation class with a {@link Lazy &#64;Lazy} annotation. In such case any default
 * value conversion delegated to the {@code @Lazy}-annotated converter will be deferred
 * to a latest possible moment (i.e. until the injection of such default value is required).
 * </p>
 * <p>
 * NOTE: A service implementing this contract is not recognized as a registrable
 * JAX-RS extension provider. Instead, a {@link ParamConverterProvider} instance
 * responsible for providing {@code ParamConverter} instances has to be registered
 * as one of the JAX-RS extension providers.
 * </p>
 *
 * @param <T> the supported Java type convertible to/from a {@code String} format.
 * @author Marek Potociar
 */
public interface ParamConverter<T> {

    /**
     * Mandates that a conversion of any {@link DefaultValue default value} delegated
     * to a {@link ParamConverter parameter converter} annotated with {@code @Lazy}
     * annotation SHOULD occur only once the value is actually required (e.g. to be
     * injected for the first time).
     */
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public static @interface Lazy {}

    /**
     * Parse the supplied value and create an instance of {@code T}.
     *
     * @param value the string value.
     * @return the newly created instance of {@code T}.
     * @throws IllegalArgumentException if the supplied string cannot be
     *                                  parsed or is {@code null}.
     */
    public T fromString(String value);

    /**
     * Convert the supplied value to a String.
     * <p>
     * This method is reserved for future use. Proprietary JAX-RS extensions may leverage the method.
     * Users should be aware that any such support for the method comes at the expense of producing
     * non-portable code.
     * </p>
     *
     * @param value the value of type {@code T}.
     * @return a String representation of the value.
     * @throws IllegalArgumentException if the supplied object cannot be
     *                                  serialized or is {@code null}.
     */
    public String toString(T value);
}
