package org.balanceline4j.source.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.balanceline4j.exception.BalanceLineSourceException;
import org.balanceline4j.source.BalanceLineSource;



public abstract class InputStreamBalanceLineSource<K extends Comparable<? super K>, R> implements BalanceLineSource<K, R> {

	private BufferedReader reader;
	private boolean beforeFirst;
	private boolean afterLast;	
	protected String recordText;
	
	public InputStreamBalanceLineSource(InputStream inputStream) {
		if (inputStream == null) {
			throw new IllegalArgumentException("InputStream object cannot be null");
		}
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
		this.beforeFirst = true;
		this.afterLast = false;
	}
	
	public final boolean next() throws BalanceLineSourceException {
		beforeFirst = false;
		try {
			recordText = reader.readLine();
			if (recordText == null) {
				afterLast = true;
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			throw new BalanceLineSourceException(e);
		}
	}

	public final boolean isBeforeFirst() {
		return beforeFirst;
	}
	
	public final boolean isAfterLast() {
		return afterLast;
	}

	public final boolean resetSupported() {
		return false;
	}
	
	public final void reset() {
		throw new UnsupportedOperationException("Reset method not supported");
	}

}
