package org.balanceline4j.example.benchmark;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.balanceline4j.exception.BalanceLineSourceException;
import org.balanceline4j.processor.record.event.DefaultRecordEvent;
import org.balanceline4j.processor.record.event.RecordEvent;
import org.balanceline4j.source.impl.ResultsetBalanceLineSource;

public class CustomerDatabaseBalanceLineSource extends ResultsetBalanceLineSource<Long, Customer> {
	
	public CustomerDatabaseBalanceLineSource(ResultSet resultset) {
		super(resultset);
	}

	public RecordEvent<Long, Customer> getRecordEvent() throws BalanceLineSourceException {
		if (isAfterLast()) {
			throw new NoSuchElementException("There are no more records to read");
		}
		try {
			Customer masterCustomer = new Customer();
			masterCustomer.setId(resultset.getLong("IDCUSTOMER"));
			masterCustomer.setName(resultset.getString("NAME"));
			masterCustomer.setAddress(resultset.getString("ADDRESS"));
			masterCustomer.setPhone(resultset.getString("PHONE"));
			DefaultRecordEvent<Long, Customer> masterRecordEvent = new DefaultRecordEvent<Long, Customer>(masterCustomer.getId(), masterCustomer);
			return masterRecordEvent;
		} catch (SQLException e) {
			throw new BalanceLineSourceException(e);
		}
	}

}
