/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.threads;

import java.util.concurrent.RejectedExecutionException;


public interface ExecutionController<Task extends TaskGroup> {

	void start() throws RejectedExecutionException;
	
	void abort();
	
	int getRemainingTaskCount();
	
	void awaitExecution() throws InterruptedException;
	
	void awaitExecution(long timeout) throws InterruptedException;

	boolean isStarted();

	boolean isAborted();

	boolean isFinished();
	
	Task getTaskGroup();
}
