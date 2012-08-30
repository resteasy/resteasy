package org.jboss.resteasy.client.exception;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public class ResteasyMalformedChunkCodingException extends ResteasyIOException
{
	private static final long serialVersionUID = -5711578608757689465L;
	
	public ResteasyMalformedChunkCodingException()
	{
	}

	public ResteasyMalformedChunkCodingException(String message)
	{
		super(message);
    }
	
    public ResteasyMalformedChunkCodingException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public ResteasyMalformedChunkCodingException(Throwable cause)
    {
        super(cause);
    }
}
