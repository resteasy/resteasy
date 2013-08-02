/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.threads;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLAttribute;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLGenerator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@XMLElement(name="ExecutionController")
public class SimpleExecutionController<T extends TaskGroup> implements ExecutionController<T>, Elementable{

	private final ReentrantLock globalLock = new ReentrantLock();
	private Condition finishedCondition = globalLock.newCondition();

	private T taskGroup;
	
	private BlockingQueue<? extends Runnable> taskQueue;
	private ThreadPoolTaskGroupHandler<T> threadPoolTaskHandler;

	@XMLAttribute
	private boolean started;
	
	@XMLAttribute
	private boolean aborted;
	
	@XMLAttribute
	private int initialTaskCount;
	
	@XMLAttribute
	private AtomicInteger completedTaskCount = new AtomicInteger();
	
	public SimpleExecutionController(T taskGroup, ThreadPoolTaskGroupHandler<T> threadPoolTaskHandler) {

		super();
		this.taskGroup = taskGroup;
		this.taskQueue = taskGroup.getTasks();
		this.threadPoolTaskHandler = threadPoolTaskHandler;
		initialTaskCount = taskQueue.size();
	}	
	
	public void abort() {

		globalLock.lock();
		
		try{
			
			if(this.threadPoolTaskHandler != null){
				
				if(started){
					threadPoolTaskHandler.remove(this);
				}
				
				this.aborted = true;
				
				this.executionComplete();
			}
			
		}finally{
			
			globalLock.unlock();
		}
	}

	public void awaitExecution(long timeout) throws InterruptedException {

		globalLock.lock();
		
		try{
			
			if(finishedCondition != null){
				
				finishedCondition.await(timeout, TimeUnit.MILLISECONDS);
			}
			
		}finally{
			
			globalLock.unlock();
		}

	}

	public void awaitExecution() throws InterruptedException {

		globalLock.lock();
		
		try{
			
			if(finishedCondition != null){
				
				finishedCondition.await();
			}
			
		}finally{
			
			globalLock.unlock();
		}

	}	
	
	void executionComplete(){
				
		globalLock.lock();
		
		try{
			
			if(finishedCondition != null){
				
				finishedCondition.signalAll();
				
				threadPoolTaskHandler = null;
				finishedCondition = null;
			}			
						
		}finally{
			
			globalLock.unlock();
		}		
	}
	
	public int getRemainingTaskCount() {

		return taskQueue.size();
	}

	
	BlockingQueue<? extends Runnable> getTaskQueue() {
	
		return taskQueue;
	}

	public void start() {

		globalLock.lock();
		
		try{
			if(!started && !aborted){
				
				this.threadPoolTaskHandler.add(this);
				this.started = true;
			}
			
		}finally{
			
			globalLock.unlock();
		}
	}

	
	public int getInitialTaskCount() {
	
		return initialTaskCount;
	}

	
	public int getCompletedTaskCount() {
	
		return completedTaskCount.get();
	}
	
	void incrementCompletedTaskCount(){
		
		this.completedTaskCount.incrementAndGet();
	}
	
	public boolean isStarted(){
		
		return started;
	}
	
	public boolean isAborted(){
		
		return aborted;
	}
	
	public boolean isFinished(){
		
		return started && !aborted && this.threadPoolTaskHandler == null;
	}

	
	public T getTaskGroup() {
	
		return taskGroup;
	}

	public Element toXML(Document doc) {

		return XMLGenerator.toXML(this, doc);
	}
}
