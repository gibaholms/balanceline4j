package org.balanceline4j;

import org.balanceline4j.exception.BalanceLineException;
import org.balanceline4j.processor.record.handler.ErrorHandler;

public interface BalanceLine<K extends Comparable<? super K>, T, M> {

	public void setErrorHandler(ErrorHandler errorHandler);

	public void execute() throws BalanceLineException;

}