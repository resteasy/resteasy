package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.logging.Logger;

import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XmlJavaTypeAdapterAlienAdapter extends XmlAdapter<XmlJavaTypeAdapterHuman, XmlJavaTypeAdapterAlien> {
    public static AtomicInteger marshalCounter = new AtomicInteger(0);
    public static AtomicInteger unmarshalCounter = new AtomicInteger(0);
    private static Logger logger = Logger.getLogger(XmlJavaTypeAdapterResource.class.getName());

    public static void reset() {
        marshalCounter.set(0);
        unmarshalCounter.set(0);
        logger.info("reset()");
    }

    @Override
    public XmlJavaTypeAdapterHuman marshal(XmlJavaTypeAdapterAlien alien) throws Exception {
        logger.info("Entering AlienAdapter.marshal()");
        marshalCounter.incrementAndGet();
        XmlJavaTypeAdapterHuman human = new XmlJavaTypeAdapterHuman();
        human.setName(reverse(alien.getName()));
        return human;
    }

    @Override
    public XmlJavaTypeAdapterAlien unmarshal(XmlJavaTypeAdapterHuman human) throws Exception {
        logger.info("Entering AlienAdapter.unmarshal()");
        unmarshalCounter.incrementAndGet();
        XmlJavaTypeAdapterAlien alien = new XmlJavaTypeAdapterAlien();
        alien.setName(reverse(human.getName()));
        return alien;
    }

    protected static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }
}
