package org.jboss.resteasy.test.plugins.servlet;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/myAppPath")
public class AppWithAppPath extends Application {
}