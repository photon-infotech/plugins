package com.photon.phresco.status;

public class ConversionStatus implements IStatus {
	boolean status;
	String message;

	public ConversionStatus(boolean status, String message) {

		this.status = status;
		this.message = message;
	}

	public boolean isStatus() {
		return status;
	}

	public String getMessage() {
		return message + " \n";
	}

	public String toString() {
		return message + " \n";
	}
}
