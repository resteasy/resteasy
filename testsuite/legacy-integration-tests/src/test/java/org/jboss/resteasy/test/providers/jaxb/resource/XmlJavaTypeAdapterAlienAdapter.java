package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.logging.Logger;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class XmlJavaTypeAdapterAlienAdapter extends XmlAdapter<XmlJavaTypeAdapterHuman, XmlJavaTypeAdapterAlien> {
    public static int marshalCounter;
    public static int unmarshalCounter;
    private static Logger logger = Logger.getLogger(XmlJavaTypeAdapterResource.class.getName());

    public static void reset() {
        marshalCounter = 0;
        unmarshalCounter = 0;
        logger.info("reset()");
    }

    @Override
    public XmlJavaTypeAdapterHuman marshal(XmlJavaTypeAdapterAlien alien) throws Exception {
        logger.info("Entering AlienAdapter.marshal()");
        marshalCounter++;
        XmlJavaTypeAdapterHuman human = new XmlJavaTypeAdapterHuman();
        human.setName(reverse(alien.getName()));
        return human;
    }

    @Override
    public XmlJavaTypeAdapterAlien unmarshal(XmlJavaTypeAdapterHuman human) throws Exception {
        logger.info("Entering AlienAdapter.unmarshal()");
        unmarshalCounter++;
        XmlJavaTypeAdapterAlien alien = new XmlJavaTypeAdapterAlien();
        alien.setName(reverse(human.getName()));
        return alien;
    }

    protected static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }
}
