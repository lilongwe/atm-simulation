package com.lawrence.banking;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lawrence
 *
 * an implementation of note for GBP
 *
 */
public enum GBP implements Note {
	
	FIVE(5,"five"),TEN(10, "ten"),TWENTY(20, "twenty"),FIFTY(50, "fifty");
	
	final static Logger logger = LoggerFactory.getLogger(GBP.class);
	
	private static final String NAME = "GBP";
	
	private final int value;
	private final String description;
	
	private GBP(int value, String description) {
		this.value = value;
		this.description = description;
	}
	
	
	/* (non-Javadoc)
	 * @see com.lawrence.banking.Note#getValue()
	 */
	@Override
	public Integer getValue() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return description;
	}

	/* (non-Javadoc)
	 * @see com.lawrence.banking.Note#name()
	 */
	@Override
	public String type() {

		return NAME;
	}

	public static GBP noteOfValue(final int i) {

		logger.debug("Getting note for value '{}'", i);
		
		return Stream.of(GBP.values())
					.filter(v -> v.getValue() == i)
					.findFirst()
					.get();
	}
}
