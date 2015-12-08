/**
 * 
 */
package com.lawrence.banking;

/**
 * @author lawrence
 *
 *	to be thrown when the user does not have enough funds in 
 *  their account
 *
 */
public class InsufficientFundsException extends Exception {



	/**
	 * 
	 */
	private static final long serialVersionUID = 9155584148935856907L;

	/**
	 * @param message
	 */
	public InsufficientFundsException(String message) {
		super(message);
	}

}
