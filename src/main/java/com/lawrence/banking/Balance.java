/**
 * 
 */
package com.lawrence.banking;

import java.text.DecimalFormat;

/**
 * @author lawrence
 * 
 * balance of the account with defaults as we are not implementing it right now
 *
 */
public interface Balance {
	
	default public long mainUnit() { return 0l;};
	
	default public int subUnit() { return 0;};
	
	default public String amount() { 
		return mainUnit() + "." + new DecimalFormat("00").format(subUnit());
	};
	
	default public Status status() {return Status.CREDIT;};
	
	default public boolean hasPending() {return false;};
}
