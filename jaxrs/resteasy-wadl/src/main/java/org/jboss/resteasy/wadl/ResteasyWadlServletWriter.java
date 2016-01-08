package org.jboss.resteasy.wadl;

import org.jboss.resteasy.wadl.jaxb.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.jboss.resteasy.wadl.ResteasyWadlMethodParamMetaData.MethodParamType.*;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class ResteasyWadlServletWriter extends ResteasyWadlWriter {

    public void writeWadl(String base, HttpServletResponse resp, Map<String, ResteasyWadlServiceRegistry> serviceRegistries)
            throws IOException {
        try {
            ServletOutputStream output = resp.getOutputStream();

            byte[] bytes = getBytes(base, serviceRegistries);
            resp.setContentLength(bytes.length);
            output.write(bytes);
            output.flush();
            output.close();
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }


    public void writeWadl(String base, HttpServletRequest req, HttpServletResponse resp, Map<String, ResteasyWadlServiceRegistry> serviceRegistries)
            throws IOException {
        writeWadl(base, resp, serviceRegistries);
    }


}
