package com.example.radr;

import android.location.Location;

public class AleppoLocation {
	private final double lat;
	private final double lon;
	
	public AleppoLocation(Location location){
		this.lat = location.getLatitude();
		this.lon = location.getLongitude();
	}
	public AleppoLocation(double lat, double lon){
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}
	
	@Override
	public String toString(){
		return "[ " + String.valueOf(lat) + ", " + String.valueOf(lon) + " ]";
	}
}
