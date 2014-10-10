/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/
package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Header;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.stream.BodyDescriptor;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.stream.RawField;
import org.apache.james.mime4j.util.ByteArrayBuffer;
import org.apache.james.mime4j.util.ByteSequence;

// Imports added due to repackaging
import org.apache.james.mime4j.message.BodyFactory;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.HeaderImpl;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.message.MultipartImpl;


/**
 * A <code>ContentHandler</code> for building an <code>Entity</code> to be
 * used in conjunction with a {@link org.apache.james.mime4j.parser.MimeStreamParser}.
 * 
 * This class was copied nearly verbatim from org.apache.james.mime4j.message.EntityBuilder
 * It was duplicated because multipart-providers always wants a BinaryBody and never a TextBody.
 * Unfortunately, there's no easy to get this in apache-mime4j. Further, the
 * logical place to extend and override, "EntityBuilder", is a package-private class.
 * <p>
 * Therefore we duplicate EntityBuilder here.
 * All code is identical except:
 * <ul>
 * <li>package changed</li>
 * <li>class renamed</li>
 * <li>constructor renamed</li>
 * <li>additional imports added due to repackaging of the class</li>
 * <li>body(BodyDescriptor bd, final InputStream is) - Method which unilaterally returns a BinaryBody.</li>
 * </ul>
 * <p>
 * This file may not follow RESTEasy formatting standards. This is to make it easier to diff against the original EntityBuilder in the future when mime4j is updated again.
 */
class Mime4jWorkaroundBinaryEntityBuilder implements ContentHandler {

    private final Entity entity;
    private final BodyFactory bodyFactory;
    private final Stack<Object> stack;

    Mime4jWorkaroundBinaryEntityBuilder(
            final Entity entity,
            final BodyFactory bodyFactory) {
        this.entity = entity;
        this.bodyFactory = bodyFactory;
        this.stack = new Stack<Object>();
    }

    private void expect(Class<?> c) {
        if (!c.isInstance(stack.peek())) {
            throw new IllegalStateException("Internal stack error: "
                    + "Expected '" + c.getName() + "' found '"
                    + stack.peek().getClass().getName() + "'");
        }
    }

    /**
     * @see org.apache.james.mime4j.parser.ContentHandler#startMessage()
     */
    public void startMessage() throws MimeException {
        if (stack.isEmpty()) {
            stack.push(this.entity);
        } else {
            expect(Entity.class);
            Message m = new MessageImpl();
            ((Entity) stack.peek()).setBody(m);
            stack.push(m);
        }
    }

    /**
     * @see org.apache.james.mime4j.parser.ContentHandler#endMessage()
     */
    public void endMessage() throws MimeException {
        expect(Message.class);
        stack.pop();
    }

    /**
     * @see org.apache.james.mime4j.parser.ContentHandler#startHeader()
     */
    public void startHeader() throws MimeException {
        stack.push(new HeaderImpl());
    }

    /**
     * @see org.apache.james.mime4j.parser.ContentHandler#field(RawField)
     */
    public void field(Field field) throws MimeException {
        expect(Header.class);
        ((Header) stack.peek()).addField(field);
    }

    /**
     * @see org.apache.james.mime4j.parser.ContentHandler#endHeader()
     */
    public void endHeader() throws MimeException {
        expect(Header.class);
        Header h = (Header) stack.pop();
        expect(Entity.class);
        ((Entity) stack.peek()).setHeader(h);
    }

    /**
     * @see org.apache.james.mime4j.parser.ContentHandler#startMultipart(org.apache.james.mime4j.stream.BodyDescriptor)
     */
    public void startMultipart(final BodyDescriptor bd) throws MimeException {
        expect(Entity.class);

        final Entity e = (Entity) stack.peek();
        final String subType = bd.getSubType();
        final Multipart multiPart = new MultipartImpl(subType);
        e.setBody(multiPart);
        stack.push(multiPart);
    }

    /**
     * @see org.apache.james.mime4j.parser.ContentHandler#body(org.apache.james.mime4j.stream.BodyDescriptor, java.io.InputStream)
     */
    public void body(BodyDescriptor bd, final InputStream is) throws MimeException, IOException {
        expect(Entity.class);

        // NO NEED TO MANUALLY RUN DECODING.
        // The parser has a "setContentDecoding" method. We should
        // simply instantiate the MimeStreamParser with that method.

        // final String enc = bd.getTransferEncoding();

        final Body body;

        /*
        final InputStream decodedStream;
        if (MimeUtil.ENC_BASE64.equals(enc)) {
            decodedStream = new Base64InputStream(is);
        } else if (MimeUtil.ENC_QUOTED_PRINTABLE.equals(enc)) {
            decodedStream = new QuotedPrintableInputStream(is);
        } else {
            decodedStream = is;
        }
        */

        //Code change here to unilaterally use binaryBody
        //Code left commented out here to make diffs easy in the future when apache-mime4j updates.
        //if (bd.getMimeType().startsWith("text/")) {
        //    body = bodyFactory.textBody(is, bd.getCharset());
        //} else {
            body = bodyFactory.binaryBody(is);
        //}

        Entity entity = ((Entity) stack.peek());
        entity.setBody(body);
    }

    /**
     * @see org.apache.james.mime4j.parser.ContentHandler#endMultipart()
     */
    public void endMultipart() throws MimeException {
        stack.pop();
    }

    /**
     * @see org.apache.james.mime4j.parser.ContentHandler#startBodyPart()
     */
    public void startBodyPart() throws MimeException {
        expect(Multipart.class);

        BodyPart bodyPart = new BodyPart();
        ((Multipart) stack.peek()).addBodyPart(bodyPart);
        stack.push(bodyPart);
    }

    /**
     * @see org.apache.james.mime4j.parser.ContentHandler#endBodyPart()
     */
    public void endBodyPart() throws MimeException {
        expect(BodyPart.class);
        stack.pop();
    }

    /**
     * @see org.apache.james.mime4j.parser.ContentHandler#epilogue(java.io.InputStream)
     */
    public void epilogue(InputStream is) throws MimeException, IOException {
        expect(MultipartImpl.class);
        ByteSequence bytes = loadStream(is);
        ((MultipartImpl) stack.peek()).setEpilogueRaw(bytes);
    }

    /**
     * @see org.apache.james.mime4j.parser.ContentHandler#preamble(java.io.InputStream)
     */
    public void preamble(InputStream is) throws MimeException, IOException {
        expect(MultipartImpl.class);
        ByteSequence bytes = loadStream(is);
        ((MultipartImpl) stack.peek()).setPreambleRaw(bytes);
    }

    /**
     * Unsupported.
     * @see org.apache.james.mime4j.parser.ContentHandler#raw(java.io.InputStream)
     */
    public void raw(InputStream is) throws MimeException, IOException {
        throw new UnsupportedOperationException("Not supported");
    }

    private static ByteSequence loadStream(InputStream in) throws IOException {
        ByteArrayBuffer bab = new ByteArrayBuffer(64);

        int b;
        while ((b = in.read()) != -1) {
            bab.append(b);
        }

        return bab;
    }

}
