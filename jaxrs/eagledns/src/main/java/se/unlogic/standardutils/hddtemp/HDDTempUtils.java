/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.hddtemp;

import se.unlogic.standardutils.net.SocketUtils;
import se.unlogic.standardutils.numbers.NumberUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class HDDTempUtils {

	private static final String delims = "[|]+";

	public static ArrayList<Drive> getHddTemp(String host, int port, int timeout) throws IOException {

		Socket socket = null;

		try {
			socket = SocketUtils.getSocket(host, port, timeout);

			BufferedReader bfrRd = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			int waitCount = 0;
			
			while(!bfrRd.ready()){
				
				if(waitCount >= timeout){
					
					throw new SocketTimeoutException("No HDD temp response received");
				}
				
				try {
					waitCount += 200;
					Thread.sleep(200);
				} catch (InterruptedException e) {}
			}
			
			String response = new String(bfrRd.readLine());

			String[] tokens = response.split(delims);

			if((tokens.length - 1) % 4 != 0){
				
				throw new IOException("Invalid data received: " + response);
			}
			
			ArrayList<Drive> drives = new ArrayList<Drive>();
			
			for (int i = 1; i < tokens.length; i = i + 4) {
				
				Drive drive = new Drive();
				
				drive.setDevice(tokens[i]);
				drive.setType(tokens[i+1]);
				drive.setTemp(NumberUtils.toInt(tokens[i+2]));
				
				if(drive.getTemp() != null){
					
					drive.setDriveState(DriveState.OK);
					
				}else if(tokens[i+2].equals("SLP")){
					
					drive.setDriveState(DriveState.SLEEPING);
					
				}else if(tokens[i+2].equals("ERR")){
					
					drive.setDriveState(DriveState.ERROR);
				
				}else{
					
					drive.setDriveState(DriveState.UNKNOWN);
				}
				
				drives.add(drive);
			}
			
			return drives;

		} finally {

			if (socket != null) {
				socket.close();
			}
		}
	}
}
