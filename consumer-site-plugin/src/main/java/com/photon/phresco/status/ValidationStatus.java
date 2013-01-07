package com.photon.phresco.status;

public class ValidationStatus implements IStatus {
	boolean status;
	String message;

	public ValidationStatus() {
		super();
	}

	public ValidationStatus(boolean status, String message) {
		this.status = status;
		this.message = message;
	}

	public ValidationStatus(String message) {
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
