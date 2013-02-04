package com.photon.phresco.plugins.api;

import java.io.Serializable;

/**
 *
 */
public interface ExecutionStatus extends Serializable{
	
	/**
	 * 
	 */
	int SUCCESS = 0;
	
	/**
	 * 
	 */
	int FAILURE = 1;
	
	/**
	 * @return
	 */
	int getStatus();
	
	/**
	 * @return
	 */
	Object getMessages();

}