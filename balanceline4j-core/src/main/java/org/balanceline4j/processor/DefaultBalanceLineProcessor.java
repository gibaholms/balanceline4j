package org.balanceline4j.processor;

import org.balanceline4j.exception.BalanceLineProcessorException;
import org.balanceline4j.processor.record.event.RecordEvent;


public abstract class DefaultBalanceLineProcessor<K extends Comparable<? super K>, T, M> implements BalanceLineProcessor<K, T, M> {

	public void bothMasterAndTransaction(RecordEvent<K, T> transactionRecordEvent, RecordEvent<K, M> masterRecordEvent) throws BalanceLineProcessorException {
		// blank
	}

	public void masterOnly(RecordEvent<K, M> masterRecordEvent) throws BalanceLineProcessorException {
		// blank
	}

	public void transactionOnly(RecordEvent<K, T> transactionRecordEvent) throws BalanceLineProcessorException {
		// blank
	}
	
}
