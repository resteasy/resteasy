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

	Controller execute(Task taskGroup) throws RejectedExecutionException;
	
	int getTotalTaskCount();
	
	int getTaskGroupCount();
	
	void abortAllTaskGroups();
	
	List<Controller> getTaskGroups();
	
	Status getStatus();
	
	void awaitTermination(long timeout) throws InterruptedException;
	
	void awaitTermination() throws InterruptedException;
	
	void shutdown();
	
	void shutdownNow();
}
