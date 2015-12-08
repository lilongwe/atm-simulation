/**
 * 
 */
package com.lawrence.banking;

/**
 * @author lawrence
 * 
 * this would have some sort of DAO injected into it at runtime
 * and some sort of transaction management handler
 *
 */
public interface AccountService {

	public Balance getBalance(Account account);
	
	public Transaction getTransaction(Account account);
	
	public Transaction debit(Account account, long amount);
	
	public Transaction credit(Account account, long amount);
	
	public Account getAccount(String number);
	
	/*
	private final BalanceService balanceService;
	private final TransactionService transactionService;
	*/
}
