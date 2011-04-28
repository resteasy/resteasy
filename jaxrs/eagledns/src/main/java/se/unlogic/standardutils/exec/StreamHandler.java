/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.exec;

import java.io.InputStream;

/**
 * Abstract class for handling output from {@link InputStream}'s.<p>
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 *
 */
public interface StreamHandler{

	public void handleStream(InputStream inputStream);
	
	public boolean isFinished();
	
	public void awaitFinish();

	public boolean isTerminated();

	public void terminate();

}
