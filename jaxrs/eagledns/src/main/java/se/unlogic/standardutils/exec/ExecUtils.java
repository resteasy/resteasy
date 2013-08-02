/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.exec;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Utility class for executing processes and handling the output from them.
 * 
 * @author Robert "Unlogic" Olofsson (unlogic@unlogic.se)
 *
 */
public class ExecUtils {

	/**
	 * Executes the given command and waits for the resulting processes to terminate and all data written to standard out and error out to be handled.<p>
	 * 
	 * All data written to standard out is piped to System.out with the given prefix<p>
	 * 
	 * All data written to error out is piped to System.err with the given prefix<p>
	 * 
	 * @param command the command to executed
	 * @param timeout an optional timeout that controls how long the process i allowed to run before it is killed
	 * @return the exit value of the process. By convention, 0 indicates normal termination.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int execAndWait(String command, Long timeout) throws IOException, InterruptedException {

		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(command);

		try{
		
			StreamHandler errorOutHandler = new StreamPrinter(System.err);
			StreamHandler stdOutHandler = new StreamPrinter();

			errorOutHandler.handleStream(proc.getErrorStream());
			stdOutHandler.handleStream(proc.getInputStream());

			return waitForProcessAndStreams(proc,stdOutHandler,errorOutHandler, timeout);			
			
		}finally{
			
			if(proc != null){
				proc.destroy();	
			}
		}
	}

	/**
	 * Executes the given command and waits for the resulting processes to terminate and all data written to standard out and error out to be handled.<p>
	 * 
	 * @param command the command to executed
	 * @param stdOutHandler the {@link StreamHandler} to handle all output the process writes on standard out
	 * @param errorOutHandler the {@link StreamHandler} to handle all output the process writes on error out
	 * @param timeout an optional timeout that controls how long the process i allowed to run before it is killed
	 * @return the exit value of the process. By convention, 0 indicates normal termination.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int execAndWait(String command, StreamHandler stdOutHandler, StreamHandler errorOutHandler, Long timeout) throws IOException, InterruptedException {

		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(command);

		try{
			errorOutHandler.handleStream(proc.getErrorStream());
			stdOutHandler.handleStream(proc.getInputStream());
	
			return waitForProcessAndStreams(proc,stdOutHandler,errorOutHandler,timeout);
			
		}finally{
			
			if(proc != null){
				proc.destroy();	
			}
		}
	}

	/**
	 * Executes the given command and waits for the resulting processes to terminate and all data written to standard out and error out to be handled.<p>
	 * 
	 * All data written to standard out and error out is piped to the given {@link OutputStream}<p>
	 * 
	 * @param command the command to executed
	 * @param outputStream the {@link OutputStream} to handle all output of the process
	 * @param timeout an optional timeout that controls how long the process i allowed to run before it is killed
	 * @return the exit value of the process. By convention, 0 indicates normal termination.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int execAndWait(String command, OutputStream outputStream, Long timeout) throws IOException, InterruptedException {

		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(command);

		try{
		
			StreamHandler errorOutHandler = new StreamPrinter(outputStream);
			StreamHandler stdOutHandler = new StreamPrinter(outputStream);

			errorOutHandler.handleStream(proc.getErrorStream());
			stdOutHandler.handleStream(proc.getInputStream());
			
			return waitForProcessAndStreams(proc,stdOutHandler,errorOutHandler, timeout);			
			
		}finally{
			
			if(proc != null){
				proc.destroy();	
			}
		}
	}
	
	/**
	 * Executes the given command and waits for the resulting processes to terminate and all data written to standard out and error out to be handled.<p>
	 * 
	 * All data written to standard out and error out is piped to the given {@link OutputStream}<p>
	 * 
	 * @param builder the ProcessBuilder to get to start the Process through
	 * @param outputStream the {@link OutputStream} to handle all output of the process
	 * @param timeout an optional timeout that controls how long the process i allowed to run before it is killed
	 * @return the exit value of the process. By convention, 0 indicates normal termination.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int execAndWait(ProcessBuilder builder, OutputStream outputStream, Long timeout) throws IOException, InterruptedException {

		Process proc = builder.start();

		try{
			
			StreamHandler errorOutHandler = new StreamPrinter(outputStream);
			StreamHandler stdOutHandler = new StreamPrinter(outputStream);

			errorOutHandler.handleStream(proc.getErrorStream());
			stdOutHandler.handleStream(proc.getInputStream());
			
			return waitForProcessAndStreams(proc,stdOutHandler,errorOutHandler, timeout);
			
		}finally{
			
			if(proc != null){
				proc.destroy();	
			}
		}
	}	
	
	/**
	 * Waits for the given processes to terminate and all data written to standard out and error out to be handled.<p>
	 * 
	 * All data written to standard out and error out is piped to the given {@link OutputStream}<p>
	 * 
	 * @param command the command to executed
	 * @param outputStream the {@link OutputStream} to handle all output of the process
	 * @return the exit value of the process. By convention, 0 indicates normal termination.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	
	public static int waitForProcessAndStreams(Process proc, StreamHandler stdOutHandler, StreamHandler errorOutHandler, Long timeout) throws InterruptedException {

		int exitVal;
		
		if(timeout != null){
		
			long elapsedTimeout = 0;
			
			while(true){
				
				if(elapsedTimeout > timeout){
					proc.destroy();					
				}
				
				Thread.sleep(100);
				
				try{
					exitVal = proc.exitValue();
					break;
					
				}catch(IllegalThreadStateException e){
					
					elapsedTimeout += 100;
				}
			}
			
			exitVal = proc.exitValue();
			
			stdOutHandler.terminate();
			errorOutHandler.terminate();
			
		}else{
		
			exitVal = proc.waitFor();
		}
		
		if(!stdOutHandler.isFinished()){
			stdOutHandler.awaitFinish();
		}

		if(errorOutHandler != null){
			
			if(!errorOutHandler.isFinished()){
				errorOutHandler.awaitFinish();
			}			
		}		
		
		return exitVal;		
	}
}
