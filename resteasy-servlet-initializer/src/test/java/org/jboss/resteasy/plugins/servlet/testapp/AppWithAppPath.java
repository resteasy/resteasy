package org.jboss.resteasy.plugins.servlet.testapp;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/myAppPath")
public class AppWithAppPath extends Application {
}
