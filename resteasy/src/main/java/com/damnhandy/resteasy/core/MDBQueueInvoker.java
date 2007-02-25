/**
 * 
 */
package com.damnhandy.resteasy.core;

import java.io.Serializable;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.damnhandy.resteasy.HttpMethodInvocationException;

/**
 * A ResourceInvoker that publishes the representation to a JMS queue. 
 * 
 * 
 * @author Ryan J. McDonough
 * Feb 13, 2007
 *
 */
public class MDBQueueInvoker extends ResourceInvoker {

	private String queueName;
	private InitialContext ctx;
	
	public MDBQueueInvoker(Class<?> targetClass,String queueName, InitialContext ctx) {
		this.queueName = queueName;
		this.ctx = ctx;
		this.setTargetClass(targetClass);
	}

	/**
	 * 
	 *
	 * @see com.damnhandy.resteasy.core.ResourceInvoker#invoke(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
    public void invoke(HttpServletRequest request, HttpServletResponse response)
			throws HttpMethodInvocationException {
		MethodMapping mapping = findMethodMapping(request);
		Map<String,Object> inputs = extractInputsFromRequest(request,mapping);
		QueueConnection conn = null;
		QueueSender sender = null;
		QueueSession session = null;
		Queue queue = null;
		QueueConnectionFactory factory = null;
		try {
			queue = (Queue) ctx.lookup(queueName);
			factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
			conn = factory.createQueueConnection();
		    session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		    ObjectMessage message = createObjectMessage(mapping, inputs, session);
		    sender = session.createSender(queue);
		    sender.send(message);
		} catch (NamingException e) {
			throw new HttpMethodInvocationException("",e);
		} catch (JMSException e) {
			throw new HttpMethodInvocationException("",e);
		} finally {
			closeSession(session);
			closeConnection(conn);
			mapping = null;
			inputs.clear();
		}
	}
    
    /**
     * 
     * @param session
     * @throws HttpMethodInvocationException
     */
    private void closeSession(QueueSession session) throws HttpMethodInvocationException {
    	if(session != null) {
    		try {
				session.close();
			} catch (JMSException e) {
				throw new HttpMethodInvocationException("",e);
			}
    	}
    }
    
    /**
     * 
     * @param conn
     * @throws HttpMethodInvocationException
     */
    private void closeConnection(QueueConnection conn) throws HttpMethodInvocationException {
    	if(conn != null) {
    		try {
				conn.close();
			} catch (JMSException e) {
				throw new HttpMethodInvocationException("",e);
			}
    	}
    }
	
    /**
     * Extracts the input representaton from the request and Marshalls to a Java Object 
     * and create an ObjectMessage to carry the message.
     * 
     * @param mapping
     * @param inputs
     * @param session
     * @return the object message
     * @throws HttpMethodInvocationException If the marshalled type is not Serializable, and HttpMethodInvocationException is thrown.
     */
	protected ObjectMessage createObjectMessage(MethodMapping mapping,
												Map<String,Object> inputs,
												QueueSession session) 
		throws HttpMethodInvocationException {
		
		ObjectMessage message = null;
		try {
			Object object = inputs.get(mapping.getRequestRespresentationId());
			if(object instanceof Serializable) {
				Serializable messageBody = (Serializable) object;
				message = session.createObjectMessage();
				message.setObject(messageBody);
			} else {
				throw new HttpMethodInvocationException("The type "+object.getClass().getSimpleName()+
						" is not a Serizable instance and cannot be placed in a JMS message.",500);
			}
		} catch (JMSException e) {
			throw new HttpMethodInvocationException("",e);
		}
		return message;
	}

	@Override
	public Object getTargetInstance() {
		/*
		 * There is no target instance need for this type
		 */
		return null;
	}
}
