package org.jboss.resteasy.microprofile.client.header;

import java.util.List;

/**
 * Used to generate header values for Rest Client
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * 2020-07-10
 */
public interface HeaderFiller {
    /**
     *
     * @return list of header values
     */
    List<String> generateValues();
}
