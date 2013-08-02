/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.timer;

import java.util.TimerTask;

public class RunnableTimerTask extends TimerTask {

	private final Runnable runnable;

	public RunnableTimerTask(Runnable runnable) {
		super();
		this.runnable = runnable;
	}

	@Override
	public void run() {
		this.runnable.run();
	}
}
