/**
 * 
 */
package com.lawrence.banking;

/**
 * @author lawrence
 *
 *	a transaction on an account
 *
 */
public interface Transaction {

	public String amount();
	
	public long subUnit();
	
	public long mainUnit();
	
	public boolean isPending();
	
	/* yes this could be a reference to the Account object but without knowing
	 * the full scope of the application this is simpler
	 */
	public long accountNumber();
	
	public TransactionType type();
}
