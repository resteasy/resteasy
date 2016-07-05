package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyIOException extends ResteasyClientException
{
	private static final long serialVersionUID = -5711578608757689465L;
	
	public ResteasyIOException()
	{
	}

	public ResteasyIOException(String message)
	{
		super(message);
    }
	
    public ResteasyIOException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ResteasyIOException(Throwable cause)
    {
        super(cause);
    }
}
