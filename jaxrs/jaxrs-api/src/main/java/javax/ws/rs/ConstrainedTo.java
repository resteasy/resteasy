/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
package javax.ws.rs;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the run-time context in which an annotated JAX-RS provider
 * is applicable. If a {@code @ConstrainedTo} annotation is not
 * present on a JAX-RS provider type declaration, the declared provider
 * may be used in any run-time context. If such a annotation is present,
 * the JAX-RS runtime will enforce the specified usage restriction.
 * <p>
 * The following example illustrates restricting a {@link javax.ws.rs.ext.MessageBodyReader}
 * provider implementation to run only as part of a {@link RuntimeType#CLIENT JAX-RS client run-time}:
 * </p>
 * <pre>
 *  &#064;ConstrainedTo(RuntimeType.CLIENT)
 *  public class MyReader implements MessageBodyReader {
 *      ...
 *  }
 * </pre>
 * <p>
 * The following example illustrates restricting a {@link javax.ws.rs.ext.WriterInterceptor}
 * provider implementation to run only as part of a {@link RuntimeType#SERVER JAX-RS server run-time}:
 * </p>
 * <pre>
 *  &#064;ConstrainedTo(RuntimeType.SERVER)
 *  public class MyWriterInterceptor implements WriterInterceptor {
 *      ...
 *  }
 * </pre>
 * <p>
 * It is a configuration error to constraint a JAX-RS provider implementation to
 * a run-time context in which the provider cannot be applied. In such case a JAX-RS
 * runtime SHOULD inform a user about the issue and ignore the provider implementation in further
 * processing.
 * </p>
 * <p>
 * For example, the following restriction of a {@link javax.ws.rs.client.ClientRequestFilter}
 * to run only as part of a JAX-RS server run-time would be considered invalid:
 * </p>
 * <pre>
 *  // reported as invalid and ignored by JAX-RS runtime
 *  &#064;ConstrainedTo(RuntimeType.SERVER)
 *  public class MyFilter implements ClientRequestFilter {
 *      ...
 *  }
 * </pre>
 *
 * @author Marek Potociar
 * @since 2.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConstrainedTo {

    /**
     * Define the {@link RuntimeType constraint type} to be placed on a JAX-RS provider.
     */
    RuntimeType value();
}

