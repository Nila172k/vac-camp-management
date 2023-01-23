//$Id$
package com.management.camp.vaccination.exception;

public class DuplicateDataFoundException extends Exception{
	
	public DuplicateDataFoundException(String message) {
		super("Duplicate Data Found. " + message);
	}

}
