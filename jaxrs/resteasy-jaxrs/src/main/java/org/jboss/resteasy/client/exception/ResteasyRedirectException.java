package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyRedirectException extends ResteasyNonRepeatableRequestException
{
	private static final long serialVersionUID = -5711578608757689465L;
	
	public ResteasyRedirectException()
	{
	}

	public ResteasyRedirectException(String message)
	{
		super(message);
    }
	
    public ResteasyRedirectException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ResteasyRedirectException(Throwable cause)
    {
        super(cause);
    }
}
