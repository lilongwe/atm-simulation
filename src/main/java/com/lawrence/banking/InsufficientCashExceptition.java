/**
 * 
 */
package com.lawrence.banking;

/**
 * @author lawrence
 * 
 * to be thrown when there is insufficient cash in the store
 *
 */
public class InsufficientCashExceptition extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 227740808256072790L;

	/**
	 * @param message
	 */
	public InsufficientCashExceptition(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InsufficientCashExceptition(String message, Throwable cause) {
		super(message, cause);
	}
}
