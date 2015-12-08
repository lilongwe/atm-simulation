/**
 * 
 */
package com.lawrence.banking;

/**
 * @author lawrence
 *
 * to be thrown when the right denomination of notes cannot be dispensed
 * regardless of whether there is enough total cash
 * 
 * e.g. user wants 30 but notes available are 20 and 50
 *
 */
public class InsufficientDenominationException extends Exception {



	/**
	 * 
	 */
	private static final long serialVersionUID = 8072732512994290391L;

	/**
	 * @param message
	 */
	public InsufficientDenominationException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InsufficientDenominationException(String message, Throwable cause) {
		super(message, cause);
	}
}
