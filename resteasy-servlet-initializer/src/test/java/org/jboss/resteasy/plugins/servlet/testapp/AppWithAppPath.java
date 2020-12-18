package org.jboss.resteasy.plugins.servlet.testapp;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/myAppPath")
public class AppWithAppPath extends Application {
}