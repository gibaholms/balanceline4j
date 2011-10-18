package org.balanceline4j.processor.record.handler;

import org.balanceline4j.exception.BalanceLineProcessorException;

public interface ErrorHandler {

	public void error(BalanceLineProcessorException exception) throws BalanceLineProcessorException;
	
}
