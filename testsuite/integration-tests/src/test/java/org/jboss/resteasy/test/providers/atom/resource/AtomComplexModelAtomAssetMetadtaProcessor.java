package org.jboss.resteasy.test.providers.atom.resource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;

import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.spi.DecoratorProcessor;

public class AtomComplexModelAtomAssetMetadtaProcessor
        implements DecoratorProcessor<Marshaller, AtomComplexModelAtomAssetMetadataDecorators> {

    private static Logger logger = Logger.getLogger(AtomComplexModelAtomAssetMetadtaProcessor.class);

    @Override
    public Marshaller decorate(Marshaller target, AtomComplexModelAtomAssetMetadataDecorators annotation, Class type,
            Annotation[] annotations, MediaType mediaType) {
        Class[] classes = new Class[] { AtomAssetMetadata.class, Entry.class };
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
