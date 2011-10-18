package org.balanceline4j;

import org.balanceline4j.exception.BalanceLineException;
import org.balanceline4j.exception.BalanceLineProcessorException;
import org.balanceline4j.processor.BalanceLineProcessor;
import org.balanceline4j.processor.record.event.RecordEvent;
import org.balanceline4j.processor.record.handler.DefaultErrorHandler;
import org.balanceline4j.processor.record.handler.ErrorHandler;
import org.balanceline4j.source.BalanceLineSource;

public class BalanceLineImpl<K extends Comparable<? super K>, T, M> implements BalanceLine<K, T, M> {

	private BalanceLineSource<K, M> masterSource;
	private BalanceLineSource<K, T> transactionSource;
	private BalanceLineProcessor<K, T, M> processor;
	private ErrorHandler errorHandler;
	
	public BalanceLineImpl(BalanceLineSource<K, T> transactionSource, BalanceLineSource<K, M> masterSource, BalanceLineProcessor<K, T, M> processor) {
		this.transactionSource = transactionSource;
		this.masterSource = masterSource;
		this.processor = processor;
		this.errorHandler = new DefaultErrorHandler();
	}
	
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
	
	public void execute() throws BalanceLineException {
		boolean isTransactionSourceActive;
		boolean isMasterSourceActive;
		
		RecordEvent<K, T> transactionRecordEvent;
		RecordEvent<K, M> masterRecordEvent;
		
		isTransactionSourceActive = transactionSource.next();
		isMasterSourceActive = masterSource.next();
		while(isTransactionSourceActive || isMasterSourceActive) {
			if (!isTransactionSourceActive) {
				masterRecordEvent = masterSource.getRecordEvent();
				try {
					processor.masterOnly(masterRecordEvent);
				} catch (BalanceLineProcessorException e) {
					try { errorHandler.error(e); } catch (BalanceLineProcessorException exThrownByErrorHandler) { throw exThrownByErrorHandler; }
				}					
				isMasterSourceActive = masterSource.next();
			} else if (!isMasterSourceActive) {
				transactionRecordEvent = transactionSource.getRecordEvent();					
				try {
					processor.transactionOnly(transactionRecordEvent);
				} catch (BalanceLineProcessorException e) {
					try { errorHandler.error(e); } catch (BalanceLineProcessorException exThrownByErrorHandler) { throw exThrownByErrorHandler; }
				}
				isTransactionSourceActive = transactionSource.next();
			} else { 
				masterRecordEvent = masterSource.getRecordEvent();
				transactionRecordEvent = transactionSource.getRecordEvent();
				int comparisonResult = transactionRecordEvent.getKey().compareTo(masterRecordEvent.getKey());
				if (comparisonResult < 0) {
					try {
						processor.transactionOnly(transactionRecordEvent);
					} catch (BalanceLineProcessorException e) {
						try { errorHandler.error(e); } catch (BalanceLineProcessorException exThrownByErrorHandler) { throw exThrownByErrorHandler; }
					}
					isTransactionSourceActive = transactionSource.next();
				} else if (comparisonResult > 0) {
					try {
						processor.masterOnly(masterRecordEvent);
					} catch (BalanceLineProcessorException e) {
						try { errorHandler.error(e); } catch (BalanceLineProcessorException exThrownByErrorHandler) { throw exThrownByErrorHandler; }
					}
					isMasterSourceActive = masterSource.next();
				} else {
					try {
						processor.bothMasterAndTransaction(transactionRecordEvent, masterRecordEvent);
					} catch (BalanceLineProcessorException e) {
						try { errorHandler.error(e); } catch (BalanceLineProcessorException exThrownByErrorHandler) { throw exThrownByErrorHandler; }
					}
					isTransactionSourceActive = transactionSource.next();
										
					// Begin: transaction-key duplication support
					if (isTransactionSourceActive) {
						K previousTransactionKey = transactionRecordEvent.getKey();
						transactionRecordEvent = transactionSource.getRecordEvent();
						while (previousTransactionKey.compareTo(transactionRecordEvent.getKey()) == 0) {
							try {
								processor.bothMasterAndTransaction(transactionRecordEvent, masterRecordEvent);
							} catch (BalanceLineProcessorException e) {
								try { errorHandler.error(e); } catch (BalanceLineProcessorException exThrownByErrorHandler) { throw exThrownByErrorHandler; }
							}
							isTransactionSourceActive = transactionSource.next();
							previousTransactionKey = transactionRecordEvent.getKey();
							transactionRecordEvent = transactionSource.getRecordEvent();
						}
					}
					// End: transaction-key duplication support
					
					isMasterSourceActive = masterSource.next();
				}
			}
		}
	}
	
}
