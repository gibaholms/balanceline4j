package org.balanceline4j.exception;

public class BalanceLineSourceException extends BalanceLineException {
	private static final long serialVersionUID = 1L;

	public BalanceLineSourceException(String message) {
		super(message);
	}
	
	public BalanceLineSourceException(Throwable cause) {
		super(cause);
	}
	
	public BalanceLineSourceException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
