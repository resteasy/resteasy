package org.resteasy.spi;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.HeaderProvider;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ProviderFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyProviderFactory extends ProviderFactory {

    private List<ObjectFactory> instanceFactories = new ArrayList<ObjectFactory>();
    private List<HeaderProvider> headerProviders = new ArrayList<HeaderProvider>();
    private List<MessageBodyReader> messageBodyReaders = new ArrayList<MessageBodyReader>();
    private List<MessageBodyWriter> messageBodyWriters = new ArrayList<MessageBodyWriter>();


    public void addObjectFactory(ObjectFactory factory) {
        instanceFactories.add(factory);
    }

    public void addHeaderProvider(HeaderProvider provider) {
        headerProviders.add(provider);
    }

    public void addMessageBodyReader(MessageBodyReader provider) {
        messageBodyReaders.add(provider);
    }

    public void addMessageBodyWriter(MessageBodyWriter provider) {
        messageBodyWriters.add(provider);
    }

    public <T> T createInstance(Class<T> type) {
        for (ObjectFactory<T> factory : instanceFactories) {
            if (factory.supports(type)) return factory.create(type);
        }
        return null;
    }

    public <T> HeaderProvider<T> createHeaderProvider(Class<T> type) {
        for (HeaderProvider<T> factory : headerProviders) {
            if (factory.supports(type)) return factory;
        }
        return null;
    }

    public <T> MessageBodyReader<T> createMessageBodyReader(Class<T> type, MediaType mediaType) {
        for (MessageBodyReader<T> factory : messageBodyReaders) {
            ConsumeMime consumeMime = factory.getClass().getAnnotation(ConsumeMime.class);
            boolean compatible = false;
            for (String consume : consumeMime.value()) {
                if (mediaType.isCompatible(MediaType.parse(consume))) {
                    compatible = true;
                    break;
                }
            }
            if (!compatible) continue;

            if (factory.isReadable(type)) {
                return factory;
            }
        }
        return null;
    }

    public <T> MessageBodyWriter<T> createMessageBodyWriter(Class<T> type, MediaType mediaType) {
        for (MessageBodyWriter<T> factory : messageBodyWriters) {
            ProduceMime produceMime = factory.getClass().getAnnotation(ProduceMime.class);
            boolean compatible = false;
            for (String produce : produceMime.value()) {
                if (mediaType.isCompatible(MediaType.parse(produce))) {
                    compatible = true;
                    break;
                }
            }
            if (!compatible) continue;

            if (factory.isWriteable(type)) {
                return factory;
            }
        }
        return null;
    }
}
