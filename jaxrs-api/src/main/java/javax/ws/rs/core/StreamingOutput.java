/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.php
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

/*
 * StreamingOutput.java
 * 
 * Created on March 3, 2008, 4:00 PM
 * 
 */

package javax.ws.rs.core;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A type that may be used as a resource method return value or as the entity
 * in a {@link Response} when the application wishes to stream the output.
 * This is a lightweight alternative to a
 * {@link javax.ws.rs.ext.MessageBodyWriter}.
 *
 * @see javax.ws.rs.ext.MessageBodyWriter
 * @see javax.ws.rs.core.Response
 */
public interface StreamingOutput
{
   /**
    * Called to write the message body.
    *
    * @param output the OutputStream to write to.
    * @throws java.io.IOException if an IO error is encountered
    * @throws javax.ws.rs.WebApplicationException
    *                             if a specific
    *                             HTTP error response needs to be produced. Only effective if thrown prior
    *                             to any bytes being written to output.
    */
   void write(OutputStream output) throws IOException, WebApplicationException;
}
