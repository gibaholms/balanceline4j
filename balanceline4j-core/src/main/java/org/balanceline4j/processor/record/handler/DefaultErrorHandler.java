package org.balanceline4j.processor.record.handler;

import org.balanceline4j.exception.BalanceLineProcessorException;


public class DefaultErrorHandler implements ErrorHandler {

	public void error(BalanceLineProcessorException exception) throws BalanceLineProcessorException {
		throw exception;
	}
	
}
