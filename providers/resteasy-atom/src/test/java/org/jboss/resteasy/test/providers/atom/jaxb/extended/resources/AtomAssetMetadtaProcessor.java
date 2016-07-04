package org.jboss.resteasy.test.providers.atom.jaxb.extended.resources;

import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.spi.interception.DecoratorProcessor;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.lang.annotation.Annotation;

/**
 * 10 19 2012
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class AtomAssetMetadtaProcessor implements DecoratorProcessor<Marshaller, AtomAssetMetadataDecorators> {

    @Override
    public Marshaller decorate(Marshaller target, AtomAssetMetadataDecorators annotation, Class type, Annotation[] annotations, MediaType mediaType) {
        Class[] classes = new Class[]{AtomAssetMetadata.class, Entry.class};
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            return jaxbContext.createMarshaller();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
