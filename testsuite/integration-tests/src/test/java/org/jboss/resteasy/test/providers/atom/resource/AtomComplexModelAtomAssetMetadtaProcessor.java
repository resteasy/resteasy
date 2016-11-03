package org.jboss.resteasy.test.providers.atom.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.spi.DecoratorProcessor;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;

public class AtomComplexModelAtomAssetMetadtaProcessor implements DecoratorProcessor<Marshaller, AtomComplexModelAtomAssetMetadataDecorators> {

    private static Logger logger = Logger.getLogger(AtomComplexModelAtomAssetMetadtaProcessor.class);

    @Override
    public Marshaller decorate(Marshaller target, AtomComplexModelAtomAssetMetadataDecorators annotation, Class type, Annotation[] annotations, MediaType mediaType) {
        Class[] classes = new Class[]{AtomAssetMetadata.class, Entry.class};
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(classes);
            return jaxbContext.createMarshaller();
        } catch (Exception e) {

            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            logger.error(errors.toString());
        }
        return null;
    }
}
