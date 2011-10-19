package org.balanceline4j.example.benchmark;

import java.io.InputStream;

import org.balanceline4j.exception.BalanceLineSourceException;
import org.balanceline4j.processor.record.event.DefaultRecordEvent;
import org.balanceline4j.processor.record.event.RecordEvent;
import org.balanceline4j.source.impl.InputStreamBalanceLineSource;
import org.ffpojo.FFPojoHelper;
import org.ffpojo.exception.FFPojoException;

public class CustomerTextBalanceLineSource extends InputStreamBalanceLineSource<Long, Customer> {

	public CustomerTextBalanceLineSource(InputStream inputStream) {
		super(inputStream);
	}
	
	public RecordEvent<Long, Customer> getRecordEvent() throws BalanceLineSourceException {
		Customer transactionCustomer;
		try {
			transactionCustomer = FFPojoHelper.getInstance().createFromText(Customer.class, recordText);
		} catch (FFPojoException e) {
			throw new BalanceLineSourceException("Error while parsing customer record", e);
		}
		DefaultRecordEvent<Long, Customer> transactionRecordEvent = new DefaultRecordEvent<Long, Customer>(transactionCustomer.getId(), transactionCustomer);
		return transactionRecordEvent;
	}

}
