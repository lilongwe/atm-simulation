/**
 * 
 */
package com.lawrence.banking;

import java.util.Map;

/**
 * @author lawrence
 * 
 * no concurrency needed for the service itself as only one person can be using it at any one time
 * 
 * i assume the application container will manage any creation and usage of instances
 * 
 * cash machine can physically only hold Integer.MAX_VALUE number of notes
 *
 */
public interface ATMService<T extends Note> {
		
	public Account getAccount(String number);

	public Balance checkBalance(Account account);
	
	public Map<T,Integer> withdraw(Account account, long amount) throws InsufficientFundsException, InsufficientCashExceptition, ATMException, InsufficientDenominationException;
	
	public void replenish(Map<T,Integer> notes);
	
	/*
	 * having some sort of status would be useful
	 * to know if there are no 5's or 10's left etc
	 * before making a choice how much to dispense
	 * but not in the spec
	public ATMStatus status();
	*/
}
