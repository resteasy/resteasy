package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyCredentialsNotAvailableException extends ResteasyAuthenticationException
{
	private static final long serialVersionUID = -5711578608757689465L;
	
	public ResteasyCredentialsNotAvailableException()
	{
	}

	public ResteasyCredentialsNotAvailableException(String message)
	{
		super(message);
    }
	
    public ResteasyCredentialsNotAvailableException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ResteasyCredentialsNotAvailableException(Throwable cause)
    {
        super(cause);
    }
}
