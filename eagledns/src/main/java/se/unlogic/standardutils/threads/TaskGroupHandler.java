/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.threads;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;


public interface TaskGroupHandler<Task extends TaskGroup,Controller extends ExecutionController<Task>> {

	public Controller execute(Task taskGroup) throws RejectedExecutionException;
	
	public int getTotalTaskCount();
	
	public int getTaskGroupCount();
	
	public void abortAllTaskGroups();
	
	public List<Controller> getTaskGroups();
	
	public Status getStatus();
	
	public void awaitTermination(long timeout) throws InterruptedException;
	
	public void awaitTermination() throws InterruptedException;
	
	public void shutdown();
	
	public void shutdownNow();
}
