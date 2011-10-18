package org.balanceline4j.processor;

import org.balanceline4j.exception.BalanceLineProcessorException;
import org.balanceline4j.processor.record.event.RecordEvent;


public interface BalanceLineProcessor<K extends Comparable<? super K>, T, M> {

	public void transactionOnly(RecordEvent<K, T> transactionRecordEvent) throws BalanceLineProcessorException;
	
	public void masterOnly(RecordEvent<K, M> masterRecordEvent) throws BalanceLineProcessorException;
	
	public void bothMasterAndTransaction(RecordEvent<K, T> transactionRecordEvent, RecordEvent<K, M> masterRecordEvent) throws BalanceLineProcessorException;
	
}
