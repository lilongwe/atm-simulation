/**
 * 
 */
package com.lawrence.banking;

import java.time.LocalDate;

/**
 * @author lawrence
 * 
 * a very basic account interface but we only need the number so the rest are just for 
 * a bit of completeness 
 *
 */
interface Account {

	/*
	 * This is a string because the specification wants the format 01001
	 * Why I am not sure as any decent system can just take a whole number
	 * i.e. 1001 or just 1 and work it out from there even if there are 
	 * accounts 345627 etc
	 */
	public String getNumber();
	
	public int getSortCode();
	
	public String getFirstName();
	
	public String getLastName();
	
	public LocalDate getBirthDate();
	
	/* yes i know it is more complicated than just a string ......*/
	public String getAddress();
	
	/*public void addTransaction();
	
	public Collection<Transaction> getTransactions();
	
	public void credit(long amount);
	
	public void debit(long amount);*/
}
