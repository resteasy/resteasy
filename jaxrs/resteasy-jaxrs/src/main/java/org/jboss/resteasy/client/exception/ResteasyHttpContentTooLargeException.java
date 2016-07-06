package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 * 
 * @deprecated The JAX-RS 2.0 client proxy framework in resteasy-client module
 *             does not use org.jboss.resteasy.client.exception.mapper.ClientExceptionMapper. 
 */
@Deprecated
public class ResteasyHttpContentTooLargeException extends ResteasyHttpException
{
	private static final long serialVersionUID = -5711578608757689465L;
	
	public ResteasyHttpContentTooLargeException()
	{
	}

	public ResteasyHttpContentTooLargeException(String message)
	{
		super(message);
    }
	
    public ResteasyHttpContentTooLargeException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ResteasyHttpContentTooLargeException(Throwable cause)
    {
        super(cause);
    }
}
