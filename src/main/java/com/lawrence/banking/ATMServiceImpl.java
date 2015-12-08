/**
 * 
 */
package com.lawrence.banking;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lawrence
 *
 */
public class ATMServiceImpl<T extends Note> implements ATMService<T> {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String MINIMUM_WITHDRAWAL_EXCEPTION_MESSAGE = "Withdrawals must be at least 20 and in multiples of 5 and a maximum of 250: amount = ";
	
	private final Map<T,Integer> cashStore = new TreeMap<T,Integer>(Note.COMPARATOR);
	private final AccountService accountService;
	private final Dispenser<T> dispenser;
	
	
	public ATMServiceImpl(AccountService accountService, Dispenser<T> dispenser, Map<T,Integer> cashStore) {
		this.accountService = accountService;
		this.dispenser = dispenser;
		this.cashStore.putAll(cashStore);
	}
	
	
	/* (non-Javadoc)
	 * @see com.lawrence.banking.ATMService#getAccount(int)
	 */
	@Override
	public Account getAccount(String number) {

		logger.debug("Getting account '{}'", number);
		
		return accountService.getAccount(number);
	}

	/* (non-Javadoc)
	 * @see com.lawrence.banking.ATMService#checkBalance(com.lawrence.banking.Account)
	 */
	//@Override
	public Balance checkBalance(Account account) {

		logger.debug("Getting balance for account '{}' , '{}", account.getNumber(), account.getSortCode());
				
		return accountService.getBalance(account);
	}

	/* (non-Javadoc)
	 * @see com.lawrence.banking.ATMService#withdraw(com.lawrence.banking.Account, int)
	 */
	//@Override
	public Map<T,Integer> withdraw(Account account, long amount) throws InsufficientFundsException, InsufficientCashExceptition, ATMException, InsufficientDenominationException {
		
		if (null == account) {
			logger.error("Null account");
			
			throw new IllegalArgumentException("Account cannot be null");
		}
		
		if (amount < 20 || amount % 5 != 0 || amount > 250) {
			logger.error("Incorrect withdrawal amount");
			
			throw new IllegalArgumentException(MINIMUM_WITHDRAWAL_EXCEPTION_MESSAGE + amount);
		}
		
		Balance balance = checkBalance(account);
		
		/* fail early if we can */
		if (amount > balance.mainUnit() ) {
			logger.debug("Insufficient funds in account - '{} - balance {}' for amount '{}'", account.getNumber(), balance.amount(), amount);
			
			throw new InsufficientFundsException("Only " + balance.amount() + " funds available");
		}
		
		Map<T,Integer> availableCash = getCashStore();
		
		Long totalCash = dispenser.calulate(availableCash);
		
		if (amount > totalCash) {
			
			logger.debug("Amount '{}' is > the cash '{}' in ATM", amount, totalCash);
			
			throw new InsufficientCashExceptition("Please choose an amount equal to or less than " + totalCash);
		}
		
		logger.info("Dispensing cash '{}'", amount);
		
		Dispenser.Result<T> result = dispenser.dispense(availableCash, amount);
				
		if (!result.success()) {
			
			Dispenser.Status status = result.status().get();
			
			logger.info("Failed to dispense with Status:'{}'", status);
			
			switch (status) {
				case InsufficientCash:
				
					throw new InsufficientFundsException(status.toString());
					
				case InsufficientDenomination:
					
					throw new InsufficientDenominationException(status.toString());

				default:
					
					logger.warn("Unknow error occurred");
					
					throw new ATMException(result.reason());
			}
		} else{
			dispenseCash(result.notes());
			
			accountService.debit(account, amount);
			
			logger.info("Dispensed {} from account {}", result.notes(), account);
		}

		return result.notes();
	}
	
	protected void dispenseCash(Map<T,Integer> notes) {
		
		logger.info("Dispensing {} from cash store", notes);
		
		//no need to be defensive here as the dispenser 
		// has already checked that we can dispense this amount
		notes.forEach((note, amount) -> cashStore.compute(note,
				(key, value) -> value - amount));
		
		logger.info("Cash store is now {}", cashStore);
	}

	/* (non-Javadoc)
	 * @see com.lawrence.banking.ATMService#replenish(java.util.Map)
	 */
	@Override
	public void replenish(Map<T,Integer> notes) {
		
		
		logger.info("Replenishing {} to cash store", notes);
		
		//should probably be in a utility class or delegated to a shared dispenser/replenisher class

		notes.forEach((note, amount) -> cashStore.compute(note,
				(key, value) -> value == null ? amount : value + amount));
		
		logger.info("Cash store is now {}", cashStore);
	}
	
	protected Map<T,Integer> getCashStore() {
		
		return Collections.unmodifiableMap(cashStore);
	}

}
