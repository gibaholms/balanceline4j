package org.balanceline4j.example;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.balanceline4j.BalanceLine;
import org.balanceline4j.BalanceLineImpl;
import org.balanceline4j.exception.BalanceLineException;
import org.balanceline4j.exception.BalanceLineProcessorException;
import org.balanceline4j.processor.BalanceLineProcessor;
import org.balanceline4j.processor.record.event.DefaultRecordEvent;
import org.balanceline4j.processor.record.event.RecordEvent;
import org.balanceline4j.source.BalanceLineSource;
import org.balanceline4j.source.impl.CollectionBalanceLineSource;

public class SimpleCollectionBalanceLineExample {

	public static class Customer {
		private Integer id;
		private String name;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public class CustomerKey implements Comparable<CustomerKey> {
		private Integer id;
		
		public CustomerKey(Integer id) {
			this.id = id;
		}

		public int compareTo(CustomerKey other) {
			return this.id - other.id;
		}

		public Integer getId() {
			return id;
		}
	}
	
	public class CustomerBalanceLineSource extends CollectionBalanceLineSource<CustomerKey, Customer> {

		public CustomerBalanceLineSource(List<Customer> customers) {
			super(customers);
		}
		
		public RecordEvent<CustomerKey, Customer> getRecordEvent() {
			if (isAfterLast()) {
				throw new NoSuchElementException("There are no more records to read");
			}
			CustomerKey key = new CustomerKey(record.getId());
			RecordEvent<CustomerKey, Customer> recordEvent = new DefaultRecordEvent<CustomerKey, Customer>(key, record);
			return recordEvent;
		}
		
	}
	
	public class CustomerBalanceLineProcessor implements BalanceLineProcessor<CustomerKey, Customer, Customer> {

		public void transactionOnly(RecordEvent<CustomerKey, Customer> transactionRecordEvent) throws BalanceLineProcessorException {
			Customer transactionRecord = transactionRecordEvent.getRecord();
			System.out.print("In Transaction Side Only (consider inserting into master side): ");
			System.out.printf("[%d][%s]\n", transactionRecord.getId(), transactionRecord.getName());
		}
		
		public void masterOnly(RecordEvent<CustomerKey, Customer> masterRecordEvent) throws BalanceLineProcessorException {
			Customer masterRecord = masterRecordEvent.getRecord();
			System.out.print("In Master Side Only (consider mantaining or deleting from master side): ");
			System.out.printf("[%d][%s]\n", masterRecord.getId(), masterRecord.getName());
		}

		public void bothMasterAndTransaction(RecordEvent<CustomerKey, Customer> transactionRecordEvent, RecordEvent<CustomerKey, Customer> masterRecordEvent) throws BalanceLineProcessorException {
			Customer transactionRecord = transactionRecordEvent.getRecord();
			Customer masterRecord = masterRecordEvent.getRecord();
			System.out.print("In Both Sides (consider comparing and updating if needed): ");
			System.out.printf("[%d][%s] - [%d][%s]\n", transactionRecord.getId(), transactionRecord.getName(), masterRecord.getId(), masterRecord.getName());
		}
		
	}
	
	public static void main(String[] args) {
		SimpleCollectionBalanceLineExample example = new SimpleCollectionBalanceLineExample();
		try {
			System.out.println("Executing Balance Line...");
			example.executeBalanceLine();
			
			System.out.println("END !");
		} catch (BalanceLineException e) {
			e.printStackTrace();
		}
	}
	
	public void executeBalanceLine() throws BalanceLineException {
		BalanceLineSource<CustomerKey, Customer> customerMasterSource = new CustomerBalanceLineSource(createMasterCustomersMockList());
		BalanceLineSource<CustomerKey, Customer> customerTransactionSource = new CustomerBalanceLineSource(createTransactionCustomersMockList());
		BalanceLine<CustomerKey, Customer, Customer> balanceLine = new BalanceLineImpl<CustomerKey, Customer, Customer>(customerTransactionSource, customerMasterSource, new CustomerBalanceLineProcessor());
		balanceLine.execute();
	}
	
	private static List<Customer> createTransactionCustomersMockList() {
		List<Customer> customers = new ArrayList<Customer>();
		{
			Customer cust = new Customer();
			cust.setId(1); 
			cust.setName("Axel Rose"); 
			customers.add(cust);
		}
		{
			Customer cust = new Customer();
			cust.setId(2); 
			cust.setName("Bono Vox"); 
			customers.add(cust);
		}
		{
			Customer cust = new Customer();
			cust.setId(6); 
			cust.setName("Bob Marley"); 
			customers.add(cust);
		}
		return customers;
	}
	
	private static List<Customer> createMasterCustomersMockList() {
		List<Customer> customers = new ArrayList<Customer>();
		{
			Customer cust = new Customer();
			cust.setId(2); 
			cust.setName("Bono Vox"); 
			customers.add(cust);
		}
		{
			Customer cust = new Customer();
			cust.setId(5); 
			cust.setName("Sebastian Bach"); 
			customers.add(cust);
		}
		{
			Customer cust = new Customer();
			cust.setId(7); 
			cust.setName("Bryan Adams"); 
			customers.add(cust);
		}
		{
			Customer cust = new Customer();
			cust.setId(9); 
			cust.setName("Britney Spears"); 
			customers.add(cust);
		}
		return customers;
	}
	
}
