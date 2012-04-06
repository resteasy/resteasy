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
 * UriBuilderException.java
 *
 * Created on August 22, 2007, 4:41 PM
 *
 */

package javax.ws.rs.core;

/**
 * A runtime exception thrown by {@link UriBuilder#build} methods when a {@link
 * java.net.URI} cannot be constructed based on the current state of the
 * builder.
 */
public class UriBuilderException extends java.lang.RuntimeException
{
   private static final long serialVersionUID = 956255913370721193L;

   /**
    * Creates a new instance of <code>UriBuilderException</code> without detail message.
    */
   public UriBuilderException()
   {
   }


   /**
    * Constructs an instance of <code>UriBuilderException</code> with the specified detail message.
    *
    * @param msg the detail message (which is saved for later retrieval by the Throwable.getMessage() method).
    */
   public UriBuilderException(String msg)
   {
      super(msg);
   }

   /**
    * Constructs an instance of <code>UriBuilderException</code> with the specified detail message and cause.
    * <p>Note that the detail message associated with cause is not automatically incorporated in this exception's detail message.
    *
    * @param msg   the detail message (which is saved for later retrieval by the Throwable.getMessage() method).
    * @param cause the cause (which is saved for later retrieval by the Throwable.getCause() method). (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
    */
   public UriBuilderException(String msg, Throwable cause)
   {
      super(msg, cause);
   }

   /**
    * Constructs a new exception with the specified cause and a detail message
    * of (<code>cause==null ? null : cause.toString()</code>) (which typically contains
    * the class and detail message of cause). This constructor is useful
    * for exceptions that are little more than wrappers for other throwables.
    *
    * @param cause the original exception
    */
   public UriBuilderException(Throwable cause)
   {
      super(cause);
   }
}
