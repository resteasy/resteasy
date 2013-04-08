/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.hddtemp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLGenerator;

@XMLElement
public class Drive implements Elementable{

	@XMLElement
	private Integer temp;
	
	@XMLElement
	private String type;
	
	@XMLElement
	private String device;
	
	@XMLElement
	private DriveState driveState;
	
	public Drive(Integer temp, String type, String device)
	{
		this.temp = temp;
		this.device = device;
		this.type = type;
	}
	
	public Drive(){}
	
	public Integer getTemp() {
		return temp;
	}
	public void setTemp(Integer temp) {
		this.temp = temp;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;	}

	@Override
	public String toString() {

		return device + " " + temp + "-" + "(" + type + ")";
	}

	public Element toXML(Document doc) {
		
		return XMLGenerator.toXML(this, doc);
	}

	
	public DriveState getDriveState() {
	
		return driveState;
	}

	
	public void setDriveState(DriveState driveState) {
	
		this.driveState = driveState;
	}
}
