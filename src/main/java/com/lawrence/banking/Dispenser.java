/**
 * 
 */
package com.lawrence.banking;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lawrence
 * 
 * handles dispensing of notes in the right denomination
 * given an amount and a bunch of notes to dispense from
 * 
 * also declares the static class Result and the enum Status
 *
 */
public interface Dispenser<T extends Note> {
	
	final Logger logger = LoggerFactory.getLogger(Dispenser.class);
	
	public enum Status {
		InsufficientCash("Insufficient Cash"),
		InsufficientDenomination("Insufficient Denomination"),
		OK("OK");
		
		private final String status;
		
		Status(String status) {
			this.status = status;
		}
		
		public String toString() {
			return status;
		}
	}
	
	public interface Result<F extends Note> {

		default public Map<F,Integer> notes() {
			logger.debug("Returning empty map");
			return Collections.emptyMap();
		};
		default public String reason() {return "";};
		default public Optional<? extends Dispenser.Status> status() {return Optional.of(Status.OK);};
		default public boolean success() { 
			if (status().get() == Status.OK) { 
				return true;
			}
			else {
				return false;
			}
		};
	}

	public Result<T> dispense(Map<T,Integer> cashStore, long amount);
	
	public Long calulate(Map<T,Integer>notes);
	
}
