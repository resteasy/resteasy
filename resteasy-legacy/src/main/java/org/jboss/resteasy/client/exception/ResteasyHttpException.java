package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyHttpException extends ResteasyClientException
{
	private static final long serialVersionUID = -5711578608757689465L;
	
	public ResteasyHttpException()
	{
	}

	public ResteasyHttpException(String message)
	{
		super(message);
    }
	
    public ResteasyHttpException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ResteasyHttpException(Throwable cause)
    {
        super(cause);
    }
}
