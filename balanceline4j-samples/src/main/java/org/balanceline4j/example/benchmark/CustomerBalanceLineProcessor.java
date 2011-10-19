package org.balanceline4j.example.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.balanceline4j.exception.BalanceLineProcessorException;
import org.balanceline4j.processor.BalanceLineProcessor;
import org.balanceline4j.processor.record.event.RecordEvent;

public class CustomerBalanceLineProcessor implements BalanceLineProcessor<Long, Customer, Customer> {

	private Connection conn;
	
	public CustomerBalanceLineProcessor(Connection conn) {
		this.conn = conn;
	}

	public void transactionOnly(RecordEvent<Long, Customer> transactionRecordEvent) throws BalanceLineProcessorException {
		Customer transactionRecord = transactionRecordEvent.getRecord();
		PreparedStatement stmt = null;
		try {
			System.out.println("Inserting new customer...");
			stmt = conn.prepareStatement("INSERT INTO CUSTOMER (IDCUSTOMER,NAME,ADDRESS,PHONE) VALUES (?,?,?,?)");
			stmt.setLong	(1, transactionRecord.getId());
			stmt.setString	(2, transactionRecord.getName());
			stmt.setString	(3, transactionRecord.getAddress());
			stmt.setString	(4, transactionRecord.getPhone());
			stmt.executeUpdate();
			stmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try { if (stmt != null) stmt.close(); } catch(Exception e) {}
		}
	}
	
	public void masterOnly(RecordEvent<Long, Customer> masterRecordEvent) throws BalanceLineProcessorException {
		Long key = masterRecordEvent.getKey();
		PreparedStatement stmt = null;
		try {
			System.out.println("Deleting existing customer...");
			stmt = conn.prepareStatement("DELETE FROM CUSTOMER WHERE IDCUSTOMER=?");
			stmt.setLong	(1, key);
			stmt.executeUpdate();
			stmt.close();
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try { if (stmt != null) stmt.close(); } catch(Exception e) {}
		}
	}
	
	public void bothMasterAndTransaction(RecordEvent<Long, Customer> transactionRecordEvent, RecordEvent<Long, Customer> masterRecordEvent) throws BalanceLineProcessorException {
		Customer transactionRecord = transactionRecordEvent.getRecord();
		Customer masterRecord = masterRecordEvent.getRecord();
		PreparedStatement stmt = null;
		try {
			if (!transactionRecord.equals(masterRecord)) {
				System.out.println("Updating existing customer...");
				stmt = conn.prepareStatement("UPDATE CUSTOMER SET NAME=?,ADDRESS=?,PHONE=? WHERE IDCUSTOMER=?");
				stmt.setString	(1, transactionRecord.getName());
				stmt.setString	(2, transactionRecord.getAddress());
				stmt.setString	(3, transactionRecord.getPhone());
				stmt.setLong	(4, transactionRecord.getId());
				stmt.executeUpdate();
				stmt.close();
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try { if (stmt != null) stmt.close(); } catch(Exception e) {}
		}
	}
}
