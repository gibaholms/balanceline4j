package org.balanceline4j.exception;

public class BalanceLineProcessorException extends BalanceLineException {
	private static final long serialVersionUID = 1L;

	public BalanceLineProcessorException(String message) {
		super(message);
	}
	
	public BalanceLineProcessorException(Throwable cause) {
		super(cause);
	}
	
	public BalanceLineProcessorException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
