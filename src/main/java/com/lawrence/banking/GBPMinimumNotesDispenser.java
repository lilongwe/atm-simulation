/**
 * 
 */
package com.lawrence.banking;

import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lawrence
 * 
 * a concrete implementation of Dispenser that dispenses GBP notes
 *
 */
public class GBPMinimumNotesDispenser implements Dispenser<GBP> {

	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected static final String insufficientNotesStatus  = "Not enough notes of the correct denomination: Notes - ";
	protected static final String insufficientDenominationStatus  = "No notes avaialble: Notes - ";

	@Override
	public Dispenser.Result<GBP> dispense(Map<GBP, Integer> cashStore, long amount) {

		if (cashStore == null) {
			logger.error("Cash store is null");
			
			throw new IllegalArgumentException("Cash cannot be null");
		}
		
		if (amount < 0) {
			logger.error("Amount is negative '{}'", amount);
			
			throw new IllegalArgumentException("Amount cannot be negative: " + amount);
		}
		
		//if amount is 0 don't treat as an error
		//not now anyway
		if (amount == 0) {
			logger.warn("Request to dispense {} notes - Why?");
			
			return new Dispenser.Result<GBP>() {};
		}
		
		Long totalCash = calulate(cashStore);
		
		if (totalCash <= 0) {
			
			logger.debug("Insufficient cash available {}", totalCash);
			
			return new Dispenser.Result<GBP>() {

				@Override
				public String reason() {
					return new String(insufficientDenominationStatus + cashStore);
				}

				@Override
				public Optional<? extends Status> status() {

					return Optional.of(Dispenser.Status.InsufficientCash);
				}
			};			
		}
		
		
		//as it is a tree map the first will be the lowest denominator 
		//due to the comparator
		Optional<Entry<GBP,Integer>> optional = cashStore.entrySet().parallelStream()
																		.filter(a -> a.getValue() > 0)
																		.findFirst();
		
		Entry<GBP,Integer> entry = optional.get();
			
		int value = entry.getKey().getValue();
		
		logger.debug("Lowest denomination is {}", value);
		
		//if we can't mod the minimum value then we can't dispense
		if (amount % value != 0) {
			
			logger.debug("Not enough denomination {} for {}", cashStore, amount);
			
			return new Dispenser.Result<GBP>() {

				@Override
				public String reason() {
					return new String(insufficientNotesStatus + cashStore + " : Amount - " + amount);
				}

				@Override
				public Optional<? extends Status> status() {

					return Optional.of(Dispenser.Status.InsufficientDenomination);
				}
			};	
		}

		//reverse the map as we want highest notes first
		Map<GBP, Integer> availableNotes = new TreeMap<GBP, Integer>(Note.COMPARATOR.reversed());
		
		availableNotes.putAll(cashStore);
		
		Map<GBP, Integer> notesToDispense = new TreeMap<GBP, Integer>(Note.COMPARATOR);
		
		final Map<GBP, Integer> toDispense = new TreeMap<GBP,Integer>();
		
		try {
			toDispense.putAll(doDispense(availableNotes, notesToDispense, amount));
		}
		catch (NoSuchElementException e) {
			
			logger.error("{} Exception for {} and amount {}", e.getMessage(), availableNotes, amount);
			
			return new Dispenser.Result<GBP>() {

				@Override
				public String reason() {
					return new String(insufficientNotesStatus + cashStore + " : Amount - " + amount);
				}
				
				@Override
				public Optional<? extends Status> status() {

					return Optional.of(Dispenser.Status.InsufficientDenomination);
				}
				
			};
		}
		
		//we need to dispense at least one 5 is possible
		if (!toDispense.containsKey(GBP.FIVE) && availableNotes.containsKey(GBP.FIVE)) {
			
			logger.info("Adding 5 notes");
			
			//find lowest denomination
			Integer lowest = toDispense.entrySet().parallelStream()
												.unordered()
												.flatMapToInt(e -> IntStream.of(e.getKey().getValue()))
												.sorted()
												.findFirst()
												.getAsInt();
			
			GBP lowestNote = GBP.noteOfValue(lowest);
			
			Integer fivesNeeded = lowestNote.getValue() / GBP.FIVE.getValue();
			
			logger.debug("Lowest dispensing is {} so we need {} fives", lowestNote.getValue(), fivesNeeded);
			
			//check we have enough notes to dispense the fives
			if (availableNotes.get(GBP.FIVE) >= fivesNeeded) {
				toDispense.put(GBP.FIVE, fivesNeeded);
				availableNotes.put(lowestNote, availableNotes.get(lowestNote)  - 1);
				
				logger.debug("Substitued {} fives for a {}", fivesNeeded, lowestNote);
			}
		}
		
		
		Dispenser.Result<GBP> result = new Result<GBP>() {
								
											@Override
											public Map<GBP, Integer> notes() {
												return toDispense;
											}
										}; 
		
		return result;
	}

	/* this could be a lot more efficient but time is of essence - refactor later */
	protected Map<GBP, Integer> doDispense(Map<GBP, Integer> availableNotes, Map<GBP, Integer> notesToDispense, long amount) {
		
		if (calulate(notesToDispense) == amount || amount == 0) {
			logger.debug("Done dispensing");
			
			return notesToDispense;
		}
		
		Optional<Entry<GBP,Integer>> optionalNote = seed(availableNotes, amount);
		
		GBP note = optionalNote.get().getKey();
		
		logger.debug("Adding note {} to dispense", note);
		
		if (notesToDispense.containsKey(note)) {
			notesToDispense.put(note, notesToDispense.get(note) + 1);
		}
		else
		{
			notesToDispense.put(note, 1);
		}
		
		availableNotes.put(note, availableNotes.get(note) - 1);
		
		logger.debug("Removing note {} from cash store", note);
		
		return doDispense(availableNotes, notesToDispense, amount - note.getValue());
	}
	
	protected Optional<Entry<GBP,Integer>> seed(Map<GBP, Integer> notes, long amount) {
		
		Optional<Entry<GBP,Integer>> optional = notes.entrySet().parallelStream()
																	.filter(a -> a.getValue() > 0 && a.getKey().getValue() <= amount)
																	.findFirst();
		
		logger.debug("Seeding note {}", optional);
		
		return optional;
	}

	@Override
	public Long calulate(Map<GBP, Integer> notes) {

		if (notes == null) {
			logger.error("Can't calculate null notes");
			
			throw new IllegalArgumentException("Notes cannot be null");
		}

		if (notes.isEmpty()) {
			logger.debug("Notes are empty so not calulating");
			
			return Long.valueOf(0);
		}
		
		checkForNegatives(notes);
		
		Long result = notes.entrySet().parallelStream()
										.flatMapToLong(entry -> LongStream.of(entry.getKey().getValue() * entry.getValue()))
										.sum();

		return result;
	}
	
	//this could be a static utility method or in a base class but that's for refactoring
	protected boolean checkForNegatives(Map<GBP, Integer> notes) {
		
		boolean result = notes.values().parallelStream()
											.filter(n-> n < 0)
											.count() > 0;		
		if (result) {
			logger.error("Negative notes found {}", notes);
			
			throw new IllegalArgumentException("Negative amount of notes error: " + notes);
		}
		
		return result;
	}
}
