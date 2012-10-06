package com.xeentech.tent.model;

public class Location {
	public String type = "Point";
	public double[] coordinates;
	
	public Location (double d, double e) {
		this.coordinates = new double[] { d, e };
	}
}
