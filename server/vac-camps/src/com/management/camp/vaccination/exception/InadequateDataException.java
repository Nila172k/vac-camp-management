//$Id$
package com.management.camp.vaccination.exception;

public class InadequateDataException extends Exception{
	
	public InadequateDataException(String message) {
		super("Data Inadequate. " +  message);
		
	}

}
