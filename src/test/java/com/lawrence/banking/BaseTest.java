/**
 * 
 */
package com.lawrence.banking;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author lawrence
 *
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseTest {

	@Mock protected Account acount01001;
	@Mock protected Account acount01002;
	@Mock protected Account acount01003;

	protected static final String ACCOUNT_01001 = "01001";
	protected static final String ACCOUNT_01002 = "01002";
	protected static final String ACCOUNT_01003 = "01003";
	
	@Spy protected BalanceImpl balance01001;
	@Spy protected BalanceImpl balance01002;
	@Spy protected BalanceImpl balance01003;
	
	protected static final String BALANCE_01001 = "2738.59";
	protected static final String BALANCE_01002 = "23.00";
	protected static final String BALANCE_01003 = "0.00";

	/*protected static final long BALANCE_01001 = "01001";
	protected static final long BALANCE_01002 = "01002";
	protected static final long BALANCE_01003 = "01003";*/

	protected Account[] accounts = {};
	
	@Before
	public void initAccounts() {
		
		when(acount01001.getNumber()).thenReturn(ACCOUNT_01001);
		when(acount01002.getNumber()).thenReturn(ACCOUNT_01002);
		when(acount01003.getNumber()).thenReturn(ACCOUNT_01003);
	}
	
	@Before
	public void initBalances() {
				
		when(balance01001.mainUnit()).thenReturn(2738l);
		when(balance01001.subUnit()).thenReturn(59);
		
		when(balance01002.mainUnit()).thenReturn(23l);
	}
}
