package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyConnectionClosedException extends ResteasyIOException
{
	private static final long serialVersionUID = -5711578608757689465L;
	
	public ResteasyConnectionClosedException()
	{
	}

	public ResteasyConnectionClosedException(String message)
	{
		super(message);
    }
	
    public ResteasyConnectionClosedException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ResteasyConnectionClosedException(Throwable cause)
    {
        super(cause);
    }
}
