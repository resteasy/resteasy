package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyClientProtocolException extends ResteasyIOException
{
	private static final long serialVersionUID = -5711578608757689465L;
	
	public ResteasyClientProtocolException()
	{
	}

	public ResteasyClientProtocolException(String message)
	{
		super(message);
    }
	
    public ResteasyClientProtocolException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ResteasyClientProtocolException(Throwable cause)
    {
        super(cause);
    }
}
