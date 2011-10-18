package org.balanceline4j.source;

import org.balanceline4j.exception.BalanceLineSourceException;
import org.balanceline4j.processor.record.event.RecordEvent;


public interface BalanceLineSource<K extends Comparable<? super K>, R> {

	public boolean next() throws BalanceLineSourceException;
	public boolean isBeforeFirst() throws BalanceLineSourceException;
	public boolean isAfterLast() throws BalanceLineSourceException;
	
	public boolean resetSupported() throws BalanceLineSourceException;
	public void reset() throws BalanceLineSourceException;

	public RecordEvent<K, R> getRecordEvent() throws BalanceLineSourceException;
	
}
