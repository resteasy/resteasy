package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyAuthChallengeException extends ResteasyAuthenticationException
{
	private static final long serialVersionUID = -5711578608757689465L;
	
	public ResteasyAuthChallengeException()
	{
	}

	public ResteasyAuthChallengeException(String message)
	{
		super(message);
    }
	
    public ResteasyAuthChallengeException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ResteasyAuthChallengeException(Throwable cause)
    {
        super(cause);
    }
}
