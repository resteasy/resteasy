/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.threads;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;



public class ThreadPoolTaskGroupHandler<T extends TaskGroup> implements TaskGroupHandler<T,SimpleExecutionController<T>>, Runnable{

	protected final ReentrantLock taskGroupRemoveLock = new ReentrantLock();
	protected final Condition taskGroupRemoveCondition = taskGroupRemoveLock.newCondition(); 
	
	protected final ReentrantLock taskGroupAddLock = new ReentrantLock();
	protected final Condition taskGroupAddCondition = taskGroupAddLock.newCondition(); 
	
	protected final CopyOnWriteArrayList<SimpleExecutionController<T>> taskGroupList = new CopyOnWriteArrayList<SimpleExecutionController<T>>();
	
	protected int taskGroupIndex = 0;
	
	protected Status status = Status.RUNNING;
	
	protected final ArrayList<Thread> threads = new ArrayList<Thread>();

	private final static Logger logger = Logger.getLogger(ThreadPoolTaskGroupHandler.class);
	
	public ThreadPoolTaskGroupHandler(int poolSize, boolean daemon){
		
		while(threads.size() < poolSize){
			
			Thread thread = new Thread(this, "TaskGroupHandler thread " + (threads.size() + 1));
			thread.setDaemon(daemon);
			
			threads.add(thread);
			
			thread.start();
		}
	}

	public void run() {
		
		Runnable task = null;
		
		while(true){
		
			while(!taskGroupList.isEmpty() && status != Status.SHUTDOWN){
				
				SimpleExecutionController<T> executionController;
				
				try {
					executionController = this.taskGroupList.get(this.getIndex());
					
				} catch (IndexOutOfBoundsException e) {

					continue;
				}
				
				task = executionController.getTaskQueue().poll();
				
				if(task == null){
					
					taskGroupRemoveLock.lock();
					
					try{
											
						if(executionController.getInitialTaskCount() == executionController.getCompletedTaskCount() && this.taskGroupList.contains(executionController)){
							
							this.taskGroupList.remove(executionController);
							this.taskGroupRemoveCondition.signalAll();
							executionController.executionComplete();	

						}else{
						
							break;
						}
									
					}finally{
					
						taskGroupRemoveLock.unlock();
					}				
					
				}else{
					
					try{
						task.run();
					
					}catch(Throwable e){
						
						logger.error(e.getMessage(), e);
						
					}finally{
						
						executionController.incrementCompletedTaskCount();
					}
				}
			}			
						
			taskGroupAddLock.lock();
						
			try{
				if(status == Status.TERMINATING){
					
					//Last thread sets shutdown status
					if(getLiveThreads() == 1){
						
						status = Status.SHUTDOWN;
					}
					
					return;
				}				
				
				if(this.isEmpty()){
					
					taskGroupAddCondition.await();
				}
				
			} catch (InterruptedException e) {
				
			}finally{
				taskGroupAddLock.unlock();
			}
		}
	}

	public boolean isEmpty() {

		if(this.taskGroupList.isEmpty()){
			return true;
		}
		
		for(SimpleExecutionController<T> controller : this.taskGroupList){
			
			if(!controller.getTaskQueue().isEmpty()){
				
				return false;
			}
		}	
		
		return true;
	}

	public int getLiveThreads(){
		
		int activeThreadCount = 0;
		
		for(Thread thread : threads){
			
			if(thread.isAlive()){
				
				activeThreadCount++;
			}
		}
		
		return activeThreadCount;
	}
	
	protected synchronized int getIndex() {

		taskGroupIndex++;
		
		if(taskGroupIndex >= taskGroupList.size()){
			
			taskGroupIndex = 0;
		}
		
		return taskGroupIndex;
	}
	
	void remove(SimpleExecutionController<T> executionController){
		
		taskGroupRemoveLock.lock();
		
		try{
						
			this.taskGroupList.remove(executionController);	
			this.taskGroupRemoveCondition.signalAll();
			
		}finally{
		
			taskGroupRemoveLock.unlock();
		}					
	}
	
	void add(SimpleExecutionController<T> executionController){
		
		taskGroupAddLock.lock();
		
		try{
		
			if(status == Status.RUNNING){
			
				this.taskGroupList.add(executionController);
				taskGroupAddCondition.signalAll();
				
			}else{
				
				throw new RejectedExecutionException("TaskGroupHandler status " + status);
			}
			
		}finally{
			
			taskGroupAddLock.unlock();
		}
	}

	public void abortAllTaskGroups() {

		taskGroupRemoveLock.lock();
		
		try{
	
			while(!this.taskGroupList.isEmpty()){
				
				for(SimpleExecutionController<T> executionController : taskGroupList){
					
					executionController.abort();
				}
			}
			
		}finally{
		
			taskGroupRemoveLock.unlock();
		}
		
	}

	public SimpleExecutionController<T> execute(T taskGroup) throws RejectedExecutionException{

		if(status == Status.RUNNING){
			
			return new SimpleExecutionController<T>(taskGroup, this);
			
		}else{
			
			throw new RejectedExecutionException("TaskGroupHandler status " + status);
		}	
	}

	public int getTaskGroupCount() {

		return this.taskGroupList.size();
	}

	public List<SimpleExecutionController<T>> getTaskGroups() {

		return new ArrayList<SimpleExecutionController<T>>(this.taskGroupList);
	}

	public int getTotalTaskCount() {

		int taskCount = 0;

		for(SimpleExecutionController<T> executionController : taskGroupList){
			
			taskCount += executionController.getTaskQueue().size();
		}
		
		return taskCount;
	}

	public Status getStatus(){
		
		return this.status;
	}
	
	public void awaitTermination() throws InterruptedException {

		taskGroupRemoveLock.lock();
		
		try{
	
			while(!this.taskGroupList.isEmpty()){
				
				taskGroupRemoveCondition.await();
			}
			
		}finally{
		
			taskGroupRemoveLock.unlock();
		}
	}		
	
	public void awaitTermination(long timeout) throws InterruptedException {

		taskGroupRemoveLock.lock();
		
		try{
	
			while(!this.taskGroupList.isEmpty()){
				
				taskGroupRemoveCondition.await(timeout,TimeUnit.MILLISECONDS);
			}
			
		}finally{
		
			taskGroupRemoveLock.unlock();
		}
	}	
	
	public void shutdown() {

		taskGroupAddLock.lock();
		
		try{
	
			if(this.status == Status.RUNNING){
				
				this.status = Status.TERMINATING;
				this.taskGroupAddCondition.signalAll();
			}	
			
		}finally{
		
			taskGroupAddLock.unlock();
		}
	}	
	
	public void shutdownNow() {
		
		this.shutdown();
		this.abortAllTaskGroups();
	}	
}
