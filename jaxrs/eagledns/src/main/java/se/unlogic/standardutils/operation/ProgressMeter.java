/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.operation;

import se.unlogic.standardutils.time.TimeUtils;


public class ProgressMeter {
	private long start;
	private long finish;
	private long currentPosition;
	private long startTime;
	private long endTime;
	public ProgressMeter(){};

	public ProgressMeter(long start, long finish){
		this.start = start;
		this.finish = finish;
	}

	public ProgressMeter(boolean setStartTime){
		if(setStartTime){
			this.setStartTime();
		}
	}

	public ProgressMeter(int start, int finish, int currentPosition){
		this.start = start;
		this.finish = finish;
		this.currentPosition = currentPosition;
	}

	public long getCurrentPosition() {
		return currentPosition;
	}

	public synchronized void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}

	public synchronized void incrementCurrentPosition(){
		this.currentPosition++;
	}

	public synchronized void decrementCurrentPosition(){
		this.currentPosition--;
	}

	public long getFinish() {
		return finish;
	}

	public void setFinish(long finish) {
		this.finish = finish;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public int getPercentComplete(){
		if(this.finish > this.start){
			return (int)(((float)(this.currentPosition - this.start)/(float)(this.finish - this.start))*100f);
		}else if(this.finish < this.start){
			return (int)(((float)(this.start - this.currentPosition)/(float)(this.start - this.finish))*100f);
		}else{
			return -1;
		}
	}

	public int getPercentRemaining(){
		if(this.finish > this.start){
			if(this.finish - this.currentPosition != 0){
				return (int)(((float)(this.finish - this.currentPosition)/(float)(this.finish - this.start))*100f);
			}else{
				return 0;
			}
		}else if(this.finish < this.start){
			if(this.currentPosition - this.finish != 0){
				return (int)(((float)(this.currentPosition - this.finish)/(float)(this.start - this.finish))*100f);
			}else{
				return 0;
			}
		}else{
			return -1;
		}
	}

	public long getIntervalSize(){
		if(this.finish > this.start){
			return this.finish - this.start;
		}else if(this.finish < this.start){
			return this.start - this.finish;
		}else{
			return 0;
		}
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime() {
		this.startTime = System.currentTimeMillis();
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime() {
		this.endTime = System.currentTimeMillis();
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getTimeSpent(){
		if(this.startTime != 0){
			if(this.endTime == 0){
				return System.currentTimeMillis() - this.startTime;
			}else{
				return this.endTime - this.startTime;
			}
		}else{
			return 0;
		}
	}

	public void incrementCurrentPosition(long value) {

		this.currentPosition += value;
	}

	public String getTimeSpentString() {

		return TimeUtils.millisecondsToString(getTimeSpent());
	}
}
