package org.balanceline4j.source.impl;

import java.util.Collection;
import java.util.Iterator;

import org.balanceline4j.source.BalanceLineSource;



public abstract class CollectionBalanceLineSource<K extends Comparable<? super K>, R> implements BalanceLineSource<K, R> {

	private Collection<R> records;
	private Iterator<R> recordsIterator;
	protected R record;
	protected int size;
	protected int count;
	
	public CollectionBalanceLineSource(Collection<R> records) {
		if (records == null) {
			throw new IllegalArgumentException("Collection object cannot be null");
		}
		this.records = records;
		this.recordsIterator = records.iterator();
		this.size = records.size(); 
		this.count = 0;
	}

	public final boolean next() {
		count++;
		if (recordsIterator.hasNext()) {
			record = recordsIterator.next();
			return true;
		} else {
			return false;
		}
	}
	
	public final boolean isBeforeFirst() {
		if (count == 0 && recordsIterator.hasNext()) {
			return true;
		} else {
			return false;
		}
	}
	
	public final boolean isAfterLast() {
		if (size > 0 && count > size) {
			return true;
		} else {
			return false;
		}
	}

	public final boolean resetSupported() {
		return true;
	}
	
	public final void reset() {
		recordsIterator = records.iterator();
	}

}
