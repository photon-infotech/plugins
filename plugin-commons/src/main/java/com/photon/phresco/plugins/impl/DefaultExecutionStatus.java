package com.photon.phresco.plugins.impl;

import com.photon.phresco.plugins.api.ExecutionStatus;

public class DefaultExecutionStatus implements ExecutionStatus{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int getStatus() {
		return ExecutionStatus.SUCCESS;
	}

	public Object getMessages() {
		return null;
	}

}
