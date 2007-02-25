/**
 *
 */
package com.damnhandy.resteasy.handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;

import com.damnhandy.resteasy.RespresentationHandlerException;


/**
 * @author Ryan J. McDonough
 * @since 1.0
 */
public class JAXBRepresentationHandler implements RepresentationHandler {
    private static final Logger logger = Logger.getLogger(JAXBRepresentationHandler.class);
    private Map<Package,JAXBContext> contextCache = new ConcurrentHashMap<Package,JAXBContext>();
    private static final String OBJECT_FACTORY_NAME = ".ObjectFactory";
    
    
    /**
     * 
     * @param response
     * @param c
     * @see com.damnhandy.resteasy.handler.RepresentationHandler#handleResponse(javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    public void handleResponse(OutputStream out,Object result)
    throws RespresentationHandlerException {
    	Object returnValue = null;
    	try {
            if(result instanceof Source) {
            	TransformerFactory tf = TransformerFactory.newInstance();
                Transformer t = tf.newTransformer();
                t.transform((Source) result, new StreamResult(out));
            } else {
            	Class<?> c = result.getClass();
                JAXBContext ctx = JAXBContext.newInstance(c);
                Marshaller m = ctx.createMarshaller();
                if(c.isAnnotationPresent(XmlRootElement.class)) {
                   returnValue = result;
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
                         returnValue = current.invoke(factory,new Object[] {result});
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
   
    /**
     * 
     * @param request
     * @param c
     * @see com.damnhandy.resteasy.handler.RepresentationHandler#handleRequest(javax.servlet.http.HttpServletRequest, java.lang.Class)
     */
    public Object handleRequest(InputStream in,Class c)
    throws RespresentationHandlerException {
        try {
            Unmarshaller um = findJAXBContext(c).createUnmarshaller();
            Object input = um.unmarshal(in);
            if(input instanceof JAXBElement) {
                return ((JAXBElement) input).getValue();
            }
            return input;
        } catch (JAXBException e) {
            throw new RespresentationHandlerException("",e);
        } 
    }
    
    /**
     *
     * @param c
     * @return
     * @throws JAXBException
     */
    private JAXBContext findJAXBContext(Class c) throws JAXBException {
        JAXBContext ctx = contextCache.get(c.getPackage());
        if(ctx == null) {
            ctx = JAXBContext.newInstance(c.getPackage().getName());
            contextCache.put(c.getPackage(), ctx);
        }
        return ctx;
    }
   
}
