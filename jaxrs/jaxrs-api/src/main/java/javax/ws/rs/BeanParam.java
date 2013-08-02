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
 * The annotation that may be used to inject custom JAX-RS "parameter aggregator" value object
 * into a resource class field, property or resource method parameter.
 * <p>
 * The JAX-RS runtime will instantiate the object and inject all it's fields and properties annotated
 * with either one of the {@code @XxxParam} annotation ({@link PathParam &#64;PathParam},
 * {@link FormParam &#64;FormParam} ...) or the {@link javax.ws.rs.core.Context &#64;Context}
 * annotation. For the POJO classes same instantiation and injection rules apply as in case of instantiation
 * and injection of request-scoped root resource classes.
 * </p>
 * <p>
 * For example:
 * <pre>
 * public class MyBean {
 *   &#64;FormParam("myData")
 *   private String data;
 *
 *   &#64;HeaderParam("myHeader")
 *   private String header;
 *
 *   &#64;PathParam("id")
 *   public void setResourceId(String id) {...}
 *
 *   ...
 * }
 *
 * &#64;Path("myresources")
 * public class MyResources {
 *   &#64;POST
 *   &#64;Path("{id}")
 *   public void post(&#64;BeanParam MyBean myBean) {...}
 *
 *   ...
 * }
 * </pre>
 * </p>
 * <p>
 * Because injection occurs at object creation time, use of this annotation on resource
 * class fields and bean properties is only supported for the default per-request resource
 * class lifecycle. Resource classes using other lifecycles should only use this annotation
 * on resource method parameters.
 * </p>
 *
 * @author Marek Potociar
 * @since 2.0
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeanParam {
}
