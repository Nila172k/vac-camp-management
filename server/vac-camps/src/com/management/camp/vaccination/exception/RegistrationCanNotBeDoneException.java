//$Id$
package com.management.camp.vaccination.exception;

public class RegistrationCanNotBeDoneException extends Exception{
	
	public RegistrationCanNotBeDoneException(String message) {
		super("Registration Can not be done. " + message);
	}

}
