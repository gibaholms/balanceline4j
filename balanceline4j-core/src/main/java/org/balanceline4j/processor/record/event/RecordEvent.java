package org.balanceline4j.processor.record.event;

import org.balanceline4j.TransactionType;

public interface RecordEvent<K extends Comparable<? super K>, R> {

	public K getKey();
	public R getRecord();
	public TransactionType getTransactionType();

}