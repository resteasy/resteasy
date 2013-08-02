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

	public void start() throws RejectedExecutionException;
	
	public void abort();
	
	public int getRemainingTaskCount();
	
	public void awaitExecution() throws InterruptedException;
	
	public void awaitExecution(long timeout) throws InterruptedException;

	public boolean isStarted();

	public boolean isAborted();

	public boolean isFinished();
	
	public Task getTaskGroup();
}
