/**
 * 
 */
package com.lawrence.banking;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lawrence.banking.Dispenser.Status;

import static org.mockito.Matchers.*;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;


/**
 * @author lawrence
 *
 *	yes yes this can be refactored quite a bit
 *
 */
public class ATMServiceTest  extends BaseTest {

	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Rule public ExpectedException thrown = ExpectedException.none();
	
	@Mock AccountService accountService;
	@Mock Dispenser<GBP> dispenser;
	@Mock Map<GBP,Integer> cashStore;
	@InjectMocks ATMServiceImpl<GBP> atmService;
	
	Map<GBP,Integer> dummyCashStore = new TreeMap<GBP,Integer>();

	
	@Before
	public void setUpCashStore() {

		dummyCashStore.put(GBP.FIFTY, 125);
		dummyCashStore.put(GBP.TWENTY, 250);
		dummyCashStore.put(GBP.TEN, 523);
		dummyCashStore.put(GBP.FIVE, 1050);
	}

	
	@Before
	public void setUpAccountService() {
		when(accountService.getAccount(ACCOUNT_01001)).thenReturn(acount01001);
		when(accountService.getBalance(acount01001)).thenReturn(balance01001);

		when(accountService.getAccount(ACCOUNT_01002)).thenReturn(acount01002);
		when(accountService.getBalance(acount01002)).thenReturn(balance01002);

		when(accountService.getAccount(ACCOUNT_01003)).thenReturn(acount01003);
		when(accountService.getBalance(acount01003)).thenReturn(balance01003);
	}
	
	@Test
	public void testGetAccount() {
				
		//when(accountService.getAccount(ACCOUNT_01001)).thenReturn(acount01001);
						
		Account account = atmService.getAccount(ACCOUNT_01001);
		
		assertEquals(ACCOUNT_01001, account.getNumber());
		
		verify(accountService).getAccount(ACCOUNT_01001);		
	}
	
	@Test
	public void testCheckBalance() {
						
		Account account = atmService.getAccount(ACCOUNT_01002);
		
		Balance balance = atmService.checkBalance(account);
		
		assertEquals(balance.amount(), BALANCE_01002);
		
		verify(accountService).getBalance(account);
	}
	
	@Test
	public void testWithdrawWithNullAccount() throws InsufficientFundsException, InsufficientCashExceptition, ATMException, InsufficientDenominationException {
				
		thrown.expect(IllegalArgumentException.class);
		
		atmService.withdraw(null, 0);
	}
	
	@Test
	public void testWithdrawLessThan20() throws InsufficientFundsException, InsufficientCashExceptition, ATMException, InsufficientDenominationException {
				
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(ATMServiceImpl.MINIMUM_WITHDRAWAL_EXCEPTION_MESSAGE);
		
		Account account = atmService.getAccount(ACCOUNT_01002);
		
		atmService.withdraw(account, 19);
	}
	
	@Test
	public void testWithdrawNegativeNumber() throws InsufficientFundsException, InsufficientCashExceptition, ATMException, InsufficientDenominationException {
				
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(ATMServiceImpl.MINIMUM_WITHDRAWAL_EXCEPTION_MESSAGE);
		
		Account account = atmService.getAccount(ACCOUNT_01002);
		
		atmService.withdraw(account, -10);
	}
	
	@Test
	public void testWithdrawNotMultipleOf5() throws InsufficientFundsException, InsufficientCashExceptition, ATMException, InsufficientDenominationException {
				
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(ATMServiceImpl.MINIMUM_WITHDRAWAL_EXCEPTION_MESSAGE);
		
		Account account = atmService.getAccount(ACCOUNT_01002);
		
		atmService.withdraw(account, 7);
	}
	
	@Test
	public void testWithdrawInsufficientFunds() throws InsufficientFundsException, InsufficientCashExceptition, ATMException, InsufficientDenominationException {
		
		thrown.expect(InsufficientFundsException.class);
		
		Account account = atmService.getAccount(ACCOUNT_01002);
		
		Balance balance = atmService.checkBalance(account);
		
		atmService.withdraw(account, balance.mainUnit() * 5);
	}
	
	@Test
	public void testWithdrawInsufficientCash() throws InsufficientFundsException, InsufficientCashExceptition, ATMException, InsufficientDenominationException {
		
		thrown.expect(InsufficientCashExceptition.class);
		
		when(dispenser.calulate(cashStore)).thenReturn(0l);

		Account account = atmService.getAccount(ACCOUNT_01002);
		
		Balance balance = atmService.checkBalance(account);
				
		atmService.withdraw(account, ((balance.mainUnit() - 3) / 5) * 5);
		
		verify(dispenser).calulate(cashStore);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testWithdrawInsufficientDenomination() throws InsufficientFundsException, InsufficientCashExceptition, ATMException, InsufficientDenominationException {
		
		thrown.expect(InsufficientDenominationException.class);
		
		when(dispenser.calulate(anyMap())).thenReturn(Long.MAX_VALUE);

		Account account = atmService.getAccount(ACCOUNT_01002);
		
		Balance balance = atmService.checkBalance(account);
		
		Long amount = ((balance.mainUnit() - 3) / 5) * 5;
		
		when(dispenser.dispense(anyMap(),anyInt())).thenReturn(new Dispenser.Result<GBP>() {

			/* (non-Javadoc)
			 * @see com.lawrence.banking.Dispenser.Result#status()
			 */
			@Override
			public Optional<? extends Status> status() {
				return Optional.of(Dispenser.Status.InsufficientDenomination);
			}});
				
		atmService.withdraw(account, amount);
		
		verify(dispenser).dispense(anyMap(),anyInt());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testWithdrawl() throws InsufficientFundsException, InsufficientCashExceptition, ATMException, InsufficientDenominationException {
				
		when(dispenser.calulate(anyMap())).thenReturn(Long.MAX_VALUE);

		Account account = atmService.getAccount(ACCOUNT_01002);
		
		Balance balance = atmService.checkBalance(account);
		
		Long amount = ((balance.mainUnit() - 3) / 5) * 5;
		
		when(dispenser.dispense(anyMap(),anyInt())).thenReturn(new Dispenser.Result<GBP>() {

			@Override
			public boolean success() {
				return true;
			}});
				
		Map<GBP,Integer> notes = atmService.withdraw(account, amount);
		
		verify(accountService).debit(account, amount);
		
		assertNotNull(notes);
	}
	
	@Test
	public void testWithdrawlReducesCashStore() throws InsufficientFundsException, InsufficientCashExceptition, ATMException, InsufficientDenominationException {
		
		atmService = new ATMServiceImpl<GBP>(accountService, dispenser, dummyCashStore);
		
		when(dispenser.calulate(dummyCashStore)).thenReturn(Long.MAX_VALUE);

		Account account = atmService.getAccount(ACCOUNT_01002);
		
		Balance balance = atmService.checkBalance(account);
		
		Long amount = ((balance.mainUnit() - 3) / 5) * 5;
		
		when(dispenser.dispense(dummyCashStore,amount)).thenReturn(new Dispenser.Result<GBP>() {

			@Override
			public boolean success() {
				return true;
			}

			@Override
			public Map<GBP, Integer> notes() {
				TreeMap<GBP,Integer> toDispense = new TreeMap<GBP,Integer>();
				toDispense.put(GBP.TWENTY, 1);
				
				return toDispense;
			}	
		
		});
				
		Map<GBP,Integer> notes = atmService.withdraw(account, amount);
		
		for(Entry<GBP,Integer> entry : notes.entrySet()) {
			
			assertEquals(atmService.getCashStore().get(entry.getKey()).intValue(), dummyCashStore.get(entry.getKey()) - entry.getValue());
		}
	}
	
	@Test
	public void testReplenish() {
				
		atmService = new ATMServiceImpl<GBP>(accountService, dispenser, dummyCashStore);
		
		Map<GBP,Integer> replenishCashStore = new TreeMap<GBP,Integer>();

		replenishCashStore.put(GBP.FIFTY, 500);
		replenishCashStore.put(GBP.TWENTY, 50);
		replenishCashStore.put(GBP.TEN, 1002);
		replenishCashStore.put(GBP.FIVE, 2000);
		
		atmService.replenish(replenishCashStore);
				
		assertEquals(atmService.getCashStore().get(GBP.FIFTY), Integer.valueOf(625));
		assertEquals(atmService.getCashStore().get(GBP.TWENTY), Integer.valueOf(300));
		assertEquals(atmService.getCashStore().get(GBP.TEN), Integer.valueOf(1525));
		assertEquals(atmService.getCashStore().get(GBP.FIVE), Integer.valueOf(3050));
	}
}
