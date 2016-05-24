package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyNTLMEngineException extends ResteasyAuthenticationException
{
	private static final long serialVersionUID = -5711578608757689465L;
	
	public ResteasyNTLMEngineException()
	{
	}

	public ResteasyNTLMEngineException(String message)
	{
		super(message);
    }
	
    public ResteasyNTLMEngineException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ResteasyNTLMEngineException(Throwable cause)
    {
        super(cause);
    }
}
