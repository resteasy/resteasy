/**
 * 
 */
package com.damnhandy.resteasy.representation;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import com.damnhandy.resteasy.exceptions.RespresentationHandlerException;
import org.apache.log4j.Logger;
/**
 * @author ryan
 *
 */
public class JAXBRepresentation<T> extends AbstractRepresentation<T> {
	private static final String OBJECT_FACTORY_NAME = ".ObjectFactory";
	private static final Logger logger = Logger.getLogger(JAXBRepresentation.class);
	//private JAXBContext context;
	
	/**
	 * 
	 * @param content
	 * @param context
	 */
	public JAXBRepresentation(T content) {
		this.setContent(content);
		this.setMediaType("application/xml");
	}
	
	/* (non-Javadoc)
	 * @see com.damnhandy.resteasy.representation.AbstractRepresentation#writeTo(java.io.OutputStream)
	 */
	public void writeTo(OutputStream out) throws IOException {
		Object returnValue = null;
    	try {
            if(getContent() instanceof Source) {
            	TransformerFactory tf = TransformerFactory.newInstance();
                Transformer t = tf.newTransformer();
                t.transform((Source) getContent(), new StreamResult(out));
            } else {
            	Class<?> c = getContent().getClass();
                JAXBContext ctx = JAXBContext.newInstance(c);
                Marshaller m = ctx.createMarshaller();
                if(c.isAnnotationPresent(XmlRootElement.class)) {
                   returnValue = getContent();
                } else {
                  StringBuilder b = new StringBuilder(c.getPackage().getName());
                  b.append(OBJECT_FACTORY_NAME);
                  Class factoryClass = Class.forName(b.toString());
                  Object factory = factoryClass.newInstance();
                  Method[] method = factoryClass.getDeclaredMethods();
                  for(int i = 0; i < method.length; i++) {
                      Method current = method[i];
                      if(current.getParameterTypes().length == 1 &&
                         current.getParameterTypes()[0].equals(c) &&
                         current.getName().startsWith("create")) {
                         returnValue = current.invoke(factory,new Object[] {getContent()});
                         break;
                      }
                  }
                }
                if(returnValue != null) {
                    m.marshal(returnValue, out); 
                } else {
                   throw new RespresentationHandlerException("Could not marshall response.",null);
                }
            }
        } catch (JAXBException e) {
        	logger.error("JAXB Could not Marshall the repsonse", e);
            throw new RespresentationHandlerException("",e);
        } catch (Exception e) {
            throw new RespresentationHandlerException("",e);
        }
	}
}
