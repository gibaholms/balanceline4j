package org.balanceline4j.source;

import org.balanceline4j.exception.BalanceLineSourceException;



public abstract class DefaultBalanceLineSource<K extends Comparable<? super K>, R> implements BalanceLineSource<K, R> {
	
	public void reset() throws BalanceLineSourceException {
		throw new UnsupportedOperationException("Reset method not supported");
	}

	public boolean resetSupported() throws BalanceLineSourceException {
		return false;
	}
	
}
