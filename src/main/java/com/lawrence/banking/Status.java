package com.lawrence.banking;


/**
 * @author lawrence
 *
 *	the status of a balance
 *
 */
public enum Status {

	CREDIT("credit"), DEBIT("debit");
	
	private final String status;
	
	private Status(String status) {
		this.status = status;
	}
	
	public String toString() {
		return status;
	}
}
