/**
 * 
 */
package com.lawrence.banking;

/**
 * @author lawrence
 *
 *generic atm exception when we don't know what went wrong
 */
public class ATMException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5491122255828563769L;

	/**
	 * @param message
	 */
	public ATMException(String message) {
		super(message);
	}
}
