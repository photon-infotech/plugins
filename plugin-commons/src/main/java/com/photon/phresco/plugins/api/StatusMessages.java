package com.photon.phresco.plugins.api;

import java.io.Serializable;

/**
 * @author arunachalam
 *
 */
public interface StatusMessages extends Serializable{

	/**
	 * @return
	 */
	Severity getSeverity();
	
	/**
	 * @return
	 */
	Object getMessage();
}