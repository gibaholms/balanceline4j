package org.balanceline4j.exception;

public class BalanceLineException extends Exception {
	private static final long serialVersionUID = 1L;

	public BalanceLineException(String message) {
		super(message);
	}
	
	public BalanceLineException(Throwable cause) {
		super(cause);
	}
	
	public BalanceLineException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
