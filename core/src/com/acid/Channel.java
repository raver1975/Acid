package com.acid;
import java.util.ArrayList;

class Channel { 

	String name;
	int drawColor;
	String description;
	boolean graphMe;
	boolean relative;
	int maxValue;
	int minValue;
	ArrayList points;
	boolean allowGlobal;
		


	Channel(String _name, int _drawColor, String _description) {
		name = _name;
		drawColor = _drawColor;
		description = _description;
		allowGlobal = true;
		points = new ArrayList();
	}
	
	
	void addDataPoint(int value) {
		
		//long time = System.currentTimeMillis();
		
		//if(value > maxValue) maxValue = value;
		//if(value < minValue) minValue = value;
		
		points.add(value);
		
		// tk max length handling
	}
	
	int getLatestPoint() {
		if(points.size() > 0) {
			return (Integer) points.get(points.size() - 1);
		}
		else {
			return -1;
		}
	}


}