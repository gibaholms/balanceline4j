package org.balanceline4j.processor.record.event;

import org.balanceline4j.TransactionType;


public class DefaultRecordEvent<K extends Comparable<? super K>, R> implements RecordEvent<K, R> {

	private K key;
	private R record;
	private TransactionType transactionType;
	
	public DefaultRecordEvent(K key, R record, TransactionType transactionType) {
		this.key = key;
		this.record = record;
		this.transactionType = transactionType;
	}
	
	public DefaultRecordEvent(K key, R record) {
		this.key = key;
		this.record = record;
	}

	public K getKey() {
		return key;
	}

	public R getRecord() {
		return record;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}
}
