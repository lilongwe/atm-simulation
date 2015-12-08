/**
 * 
 */
package com.lawrence.banking;

import java.util.Comparator;

/**
 * @author lawrence
 * 
 *	represents an actual denomination
 *
 */
public interface Note {
	
	public static final Comparator<Note> COMPARATOR = (n1, n2) -> n1.getValue().compareTo(n2.getValue());

	public Integer getValue();
	
	public String toString();
	
	public String type();
}
