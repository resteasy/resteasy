package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyTunnelRefusedException extends ResteasyProtocolException
{
	private static final long serialVersionUID = -5711578608757689465L;
	
	public ResteasyTunnelRefusedException()
	{
	}

	public ResteasyTunnelRefusedException(String message)
	{
		super(message);
    }
	
    public ResteasyTunnelRefusedException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ResteasyTunnelRefusedException(Throwable cause)
    {
        super(cause);
    }
}
