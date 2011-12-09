/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011 Oracle and/or its affiliates. All rights reserved.
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

import java.io.IOException;
import javax.ws.rs.ext.FilterContext.FilterAction;

/**
 * <p>Interface implemented by filters invoked at the <emph>PreMatch</emph>
 * extension point. Use a filter of this type to update the input to the
 * JAX-RS matching algorithm, e.g., the HTTP method, Accept header, etc. 
 * Otherwise, the use of a filter invoked at the <emph>Pre</emph> extension
 * point (after resource matching) is recommended. </p>
 * 
 * <p>Filters implementing
 * this interface MUST be annotated with {@link javax.ws.rs.ext.Provider}.
 * This type of filters is supported only as part of the Server API.</p>
 * 
 * @author Santiago Pericas-Geertsen
 * @since 2.0
 * @see RequestFilter
 */
public interface PreMatchRequestFilter {

    /**
     * Filter method called at the <emph>PreMatch</emph> extension point.
     * I.e., before resource matching as part of the Server API. This method
     * can return {@link javax.ws.rs.ext.FilterContext.FilterAction#NEXT}
     * to continue the execution of the filter chain, or 
     * {@link javax.ws.rs.ext.FilterContext.FilterAction#STOP} to 
     * abort the execution of the filter chain.
     *
     * @param context invocation context
     * @return filter action to continue or stop filter chain
     * @throws IOException 
     */
    FilterAction preMatchFilter(FilterContext context) throws IOException;
}
