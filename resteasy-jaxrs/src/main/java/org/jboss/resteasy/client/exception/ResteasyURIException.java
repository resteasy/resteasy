package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyURIException extends ResteasyHttpException
{
	private static final long serialVersionUID = -5711578608757689465L;
	
	public ResteasyURIException()
	{
	}

	public ResteasyURIException(String message)
	{
		super(message);
    }
	
    public ResteasyURIException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ResteasyURIException(Throwable cause)
    {
        super(cause);
    }
}
