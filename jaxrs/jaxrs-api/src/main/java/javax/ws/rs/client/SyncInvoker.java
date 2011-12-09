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
package javax.ws.rs.client;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.TypeLiteral;

/**
 * TODO javadoc.
 *
 * @author Marek Potociar
 * @since 2.0
 */
public interface SyncInvoker {
    // GET
    Response get() throws InvocationException;

     <T> T get(Class<T> responseType) throws InvocationException;

     <T> T get(TypeLiteral<T> responseType) throws InvocationException;

    // PUT
    Response put(Entity<?> entity) throws InvocationException;

     <T> T put(Entity<?> entity, Class<T> responseType) throws InvocationException;

     <T> T put(Entity<?> entity, TypeLiteral<T> responseType) throws InvocationException;

    // POST
    Response post(Entity<?> entity) throws InvocationException;

     <T> T post(Entity<?> entity, Class<T> responseType) throws InvocationException;

     <T> T post(Entity<?> entity, TypeLiteral<T> responseType) throws InvocationException;

    // DELETE
    Response delete() throws InvocationException;

     <T> T delete(Class<T> responseType) throws InvocationException;

     <T> T delete(TypeLiteral<T> responseType) throws InvocationException;

    // HEAD
    Response head() throws InvocationException;

     <T> T head(Class<T> responseType) throws InvocationException;

     <T> T head(TypeLiteral<T> responseType) throws InvocationException;

    // OPTIONS
    Response options() throws InvocationException;

     <T> T options(Class<T> responseType) throws InvocationException;

     <T> T options(TypeLiteral<T> responseType) throws InvocationException;

    // TRACE
    Response trace(Entity<?> entity) throws InvocationException;

     <T> T trace(Entity<?> entity, Class<T> responseType) throws InvocationException;

     <T> T trace(Entity<?> entity, TypeLiteral<T> responseType) throws InvocationException;

    // ARBITRARY METHOD
    Response method(String name) throws InvocationException;

     <T> T method(String name, Class<T> responseType) throws InvocationException;

     <T> T method(String name, TypeLiteral<T> responseType) throws InvocationException;

    Response method(String name, Entity<?> entity) throws InvocationException;

     <T> T method(String name, Entity<?> entity, Class<T> responseType) throws InvocationException;

     <T> T method(String name, Entity<?> entity, TypeLiteral<T> responseType) throws InvocationException;
}
