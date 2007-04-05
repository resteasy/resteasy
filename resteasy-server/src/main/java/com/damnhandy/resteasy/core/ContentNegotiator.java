/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package com.damnhandy.resteasy.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ContentNegotiator {
	public static final String DEFAULT_TYPE = "text/plain";

	/**
	 * Parses an HTTP Accept header using he following format:
	 * <pre>
	 * application/xml;q=1.0,application/json;q=0.8,text/plain;q=0.01
	 * </pre>
	 * 
	 * @param acceptHeader
	 * @return
	 */
	public static QualityValue[] parseAcceptHeader(String acceptHeader) {
		if (acceptHeader == null || acceptHeader.length() == 0) {
			return new QualityValue[0];
		}
		String[] acceptValues = acceptHeader.split(",");
		QualityValue[] qValues = new QualityValue[acceptValues.length];
		for (int i = 0; i < acceptValues.length; i++) {
			String acceptValue = acceptValues[i];
			String[] qvalues = acceptValue.trim().split(";");
			String type = qvalues[0];
			if (qvalues.length > 1) {
				for (int n = 1; n < qvalues.length; n++) {
					String[] qValuePair = qvalues[n].trim().split("=");
					if (qValuePair[0].trim().equals("q")) {
						float qValue = Float.valueOf(qValuePair[1]);
						qValues[i] = new QualityValue(type, qValue);
						break;
					}
				}
			}
			if (qValues[i] == null) {
				qValues[i] = new QualityValue(type, 1.0f);
			}
		}
		Arrays.sort(qValues);
		return qValues;
	}
	

	
	/**
	 * Returns the media type that is best match between the client an server. This method will 
	 * default to the server type with the highest qs value
	 * @param acceptHeader the HTTP Accept header. This value maybe left null
	 * @param qualityOfSource the Quality of Source as defined by the service
	 * @return the media type that is the best match.
	 */
	public static String negotiateMediaType(final String acceptHeader,final QualityValue[] qualityOfSource) {
		QualityValue[] clientValues = parseAcceptHeader(acceptHeader);
		QualityValue[] serverValues = qualityOfSource;
		if(clientValues.length > 0) {
			QualityValue[][] result = eliminateUnacceptableTypes(serverValues,clientValues);
			clientValues = result[0];
			serverValues = result[1];
		}
		
		/*
		 * If only one type matches, us that one
		 */
		if(serverValues.length == 1 && clientValues.length == 1) {
			return serverValues[0].getMediaType();
		} 
		/*
		 * If multple matches are found, determine the best one
		 */
		else if(clientValues.length > 1) {
			return getBestMatch(serverValues,clientValues).getMediaType();
		}
		/*
		 * Otherwise, use the preferred server type
		 */
		return serverValues[0].getMediaType();
	}
	
	/**
	 * 
	 * @param serverValues
	 * @param clientValues
	 * @return
	 */
	private static QualityValue[][] eliminateUnacceptableTypes(QualityValue[] serverValues,
												   			   QualityValue[] clientValues) {
		Set<QualityValue> clientMatches = new HashSet<QualityValue>();
		Set<QualityValue> serverMatches = new HashSet<QualityValue>();
		for(int i = 0; i < serverValues.length; i++) {
			for(int c = 0; c < clientValues.length; c++) {
				if(clientValues[c].getMediaType().equals(serverValues[i].getMediaType())) {
					System.out.println("Server: "+serverValues[i]+" Client: "+clientValues[c]);
					serverMatches.add(serverValues[i]);
					clientMatches.add(clientValues[c]);
				}
			}
		}
		QualityValue[][] result = new QualityValue[2][];
		result[0] = clientMatches.toArray(new QualityValue[clientMatches.size()]);
		result[1] = serverMatches.toArray(new QualityValue[serverMatches.size()]);
		return result;
	}
	
	/**
	 * 
	 * @param serverValues
	 * @param clientValues
	 * @return
	 */
	private static QualityValue getBestMatch(QualityValue[] serverValues,
										    QualityValue[] clientValues) {
		QualityValue[] results = new QualityValue[serverValues.length];
		for(int i = 0; i < serverValues.length; i++) {
			float qValue = (clientValues[i].getFactor() * serverValues[i].getFactor());
			QualityValue value = new QualityValue(serverValues[i].getMediaType(),qValue);
			results[i] = value;
		}
		Arrays.sort(results);
		return results[0];
	}
}
