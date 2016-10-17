package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyNoHttpResponseException extends ResteasyIOException
{
	private static final long serialVersionUID = -5711578608757689465L;
	
	public ResteasyNoHttpResponseException()
	{
	}

	public ResteasyNoHttpResponseException(String message)
	{
		super(message);
    }
	
    public ResteasyNoHttpResponseException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ResteasyNoHttpResponseException(Throwable cause)
    {
        super(cause);
    }
}
