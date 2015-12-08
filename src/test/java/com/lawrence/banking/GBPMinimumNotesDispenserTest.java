/**
 * 
 */
package com.lawrence.banking;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * @author lawrence
 *
 */
public class GBPMinimumNotesDispenserTest extends BaseTest {


	GBPMinimumNotesDispenser dispenser = new GBPMinimumNotesDispenser();
	
	@Rule public ExpectedException thrown = ExpectedException.none();


	@Test
	public void testCalculateWithNull() {
		
		thrown.expect(IllegalArgumentException.class);

		dispenser.calulate(null);		
	}

	@Test
	public void testCalculateWithNoNotes() {

		Map<GBP,Integer> notes = Collections.emptyMap();
		
		Long total = dispenser.calulate(notes);
		
		assertEquals(total.intValue(), 0);
	}
	
	@Test
	public void testCalculate() {
				
		Map<GBP,Integer> notes = new TreeMap<GBP,Integer>();
		
		notes.put(GBP.FIFTY, 50);
		notes.put(GBP.TWENTY, 20);
		notes.put(GBP.TEN, 10);
		notes.put(GBP.FIVE, 5);		
		
		Long total1 = dispenser.calulate(notes);
		
		assertEquals(total1.intValue(), 3025);
		
		notes.put(GBP.FIVE, 0);		
		
		Long total2 = dispenser.calulate(notes);
		
		assertEquals(total2.intValue(), 3000);
	}

	@Test
	public void testCheckForNegativeNotes() {
		
		thrown.expect(IllegalArgumentException.class);
		
		Map<GBP,Integer> notes = new TreeMap<GBP,Integer>();
		
		notes.put(GBP.FIFTY, 10);
		notes.put(GBP.FIVE, 50);
		
		boolean negatives = dispenser.checkForNegatives(notes);
		
		assertEquals(negatives, false);
		
		notes.put(GBP.TWENTY, -10);
		
		dispenser.checkForNegatives(notes);
	}
	
	@Test
	public void testDispenseWithEmptyNotes() {
				
		Map<GBP,Integer> notes = new TreeMap<GBP,Integer>();
		
		notes.put(GBP.FIFTY, 0);
		notes.put(GBP.TWENTY, 0);
		
		Dispenser.Result<GBP> result = dispenser.dispense(notes, 30);
		
		assertFalse(result.success());
		assertTrue(result.notes().isEmpty());
		assertEquals(result.status().get(), Dispenser.Status.InsufficientCash);
	}
	
	@Test
	public void testDispenseWithNull() {
		
		thrown.expect(IllegalArgumentException.class);
				
		dispenser.dispense(null, 30);
	}
	
	@Test
	public void testDispenseWithNegativeAmount() {
		
		thrown.expect(IllegalArgumentException.class);
		
		Map<GBP,Integer> notes = new TreeMap<GBP,Integer>();
		
		notes.put(GBP.FIFTY, 5);
		notes.put(GBP.TWENTY, 5);
		
		dispenser.dispense(notes, -1);
	}
	
	@Test
	public void testDispenseZero() {
				
		Map<GBP,Integer> notes = new TreeMap<GBP,Integer>();
		
		notes.put(GBP.FIFTY, 5);
		notes.put(GBP.TWENTY, 5);
		
		Dispenser.Result<GBP> result = dispenser.dispense(notes, 0);
		
		assertTrue(result.success());
		assertTrue(result.notes().isEmpty());
		assertEquals(result.status().get(), Dispenser.Status.OK);
	}
	
	@Test
	public void testDispense() {
		
		Map<GBP,Integer> notes = new TreeMap<GBP,Integer>();

		notes.put(GBP.FIFTY, 50);
		notes.put(GBP.TWENTY, 20);
		notes.put(GBP.TEN, 10);
		notes.put(GBP.FIVE, 5);	
				
		Dispenser.Result<GBP> result = dispenser.dispense(notes, 240);
		
		Map<GBP,Integer> dispensed = result.notes();
		
		assertTrue(result.success());
		assertEquals(dispensed.get(GBP.FIFTY).longValue(), 4);
		assertEquals(dispensed.get(GBP.TWENTY).longValue(), 2);
		assertEquals(dispensed.get(GBP.FIVE).longValue(), 4);
	}
	
	@Test
	public void testDispensewithNoFivesAvailable() {
		
		Map<GBP,Integer> notes = new TreeMap<GBP,Integer>();

		notes.put(GBP.FIFTY, 50);
		notes.put(GBP.TWENTY, 20);
		notes.put(GBP.TEN, 10);
				
		Dispenser.Result<GBP> result = dispenser.dispense(notes, 240);
		
		Map<GBP,Integer> dispensed = result.notes();
		
		assertTrue(result.success());
		assertEquals(dispensed.get(GBP.FIFTY).longValue(), 4);
		assertEquals(dispensed.get(GBP.TWENTY).longValue(), 2);
		assertEquals(dispensed.get(GBP.FIVE), null);
	}
	
	@Test
	public void testDispenseWithEnoughMoneyInsufficientDenomination() {
		
		Map<GBP,Integer> notes = new TreeMap<GBP,Integer>();

		notes.put(GBP.FIFTY, 1);
		notes.put(GBP.TEN, 1);
				
		Dispenser.Result<GBP> result = dispenser.dispense(notes, 30);
				
		assertFalse(result.success());
		assertTrue(result.notes().isEmpty());
		assertEquals(result.status().get(), Dispenser.Status.InsufficientDenomination);
	}
}
