/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.resteasy.extension.systemproperties.container;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;
import org.jboss.resteasy.extension.systemproperties.SystemProperties;

/**
 * SystemPropertiesLoader
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 * @version $Revision: $
 */
public class SystemPropertiesLoader {
	public void setProperties(@Observes BeforeSuite event) {
		Properties props = load(SystemProperties.FILE_NAME);
		if (props != null) {
			for (Map.Entry<Object, Object> entry : props.entrySet()) {
				System.setProperty(entry.getKey().toString(), entry.getValue()
						.toString());
			}
		}
	}

	public void unsetProperties(@Observes AfterSuite event) {
		Properties props = load(SystemProperties.FILE_NAME);
		if (props != null) {
			for (Map.Entry<Object, Object> entry : props.entrySet()) {
				System.clearProperty(entry.getKey().toString());
			}
		}
	}

	private Properties load(String resource) {
		InputStream propsStream = Thread.currentThread()
				.getContextClassLoader().getResourceAsStream(resource);
		if (propsStream != null) {
			Properties props = new Properties();
			try {
				props.load(propsStream);
				return props;
			} catch (Exception e) {
				throw new RuntimeException("Could not load properties", e);
			}
		}
		return null;
	}
}