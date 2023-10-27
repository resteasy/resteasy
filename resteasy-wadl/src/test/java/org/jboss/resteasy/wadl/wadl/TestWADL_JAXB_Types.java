package org.jboss.resteasy.wadl.wadl;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import org.jboss.resteasy.wadl.jaxb.Application;
import org.jboss.resteasy.wadl.jaxb.ObjectFactory;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class TestWADL_JAXB_Types {

    @Test
    public void testA() throws Exception {
        ObjectFactory factory = new ObjectFactory();
        Application app = factory.createApplication();
        app.getResources();
        JAXBContext context = JAXBContext.newInstance(Application.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        //CHECKSTYLE.OFF: RegexpSinglelineJava
        marshaller.marshal(app, System.out);
        //CHECKSTYLE.ON: RegexpSinglelineJava
    }
}
