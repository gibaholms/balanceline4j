## BalanceLine4j - BalanceLine Algorithm Implementation For Java ##

The **BalanceLine4j Project** is an implementation of the **Balance Line Algorithm** for Java applications. The Balance Line is an algorithm used to make **Coordinated Processing** and **Sequential Updates**, that improves **performance** and saves **machine resources**.

The BalanceLine4j library provides many features like:

* Provides abstraction of Sequential Data Sources that can be any sortable data set (Comparable<T>)
* Some of the built in Data Sources are Collection, Map, InputStream and ResultSet, but is very simple to implement your own
* Algorithm run by data streaming, little memory consumption
* Easy to use, easy API, no knowledge of the algorithm required
* Lightweight, no extra dependencies
* Provides a built in FileSorter class capable of safely sort very big quantity of text data without memory overflow
* Developer can focus on Business Rules and let the framework handle the complexity of the algorithm
* Make applications better to maintain and evolve because it promotes isolation of business rules out of the algorithm code

### Starting Points ###

* [Releases](https://github.com/gibaholms/balanceline4j/tree/master/balanceline4j-core-releases)
* [Getting Started](#getting-started)
* [Samples](#samples)

---

#### Getting Started Tutorial <a id="getting-started"/> ####
This is a samples based tutorial, that shows the most important features of BalanceLine4j in a hurry.
Download full samples in the project named **balanceline4j-samples**.

##### Building The Project #####

When comming to GitHub this project was migrated to Maven, then building the project is very simple, default maven style. This project also
have no dependencies, so thats simplier at most. The build process is shown below:

1. Install Apache Maven (version 2+)
1. Append the Maven bin path to your PATH environment variable
1. Open the command line and go to the project folder that have the "pom.xml" file
1. Enter the command below: `mvn clean install`
1. Now its just pick-up the generated jar file at the "target" folder.

---

##### What is "Balance Line" ? #####
Balance Line is an algorithm, a computational technique to coordinate the processing of sequential massive data.

---

##### What are "Sequential Data" #####
Sequential Data are big data sets, from one or more data sources, that have a common key and present themselves ordered by that key.

---

##### Why to use Balance Line Algorithm ? #####
Two main reasons: improves the processing performance and saves computational resources.

---

##### When to use Balance Line Algorithm ? #####
Three main use cases: data synchronization (like iPod), data loading (full or partial), data conciliation.

---

##### Concepts - Master File #####
Is the main data set, represents the final view of the data, the persistent, the reference, the orign. In the framework it can be any source, that can be implemented by hand or can be used the built in sources (CollectionBalanceLineSource, InputStreamBalanceLineSource, MapBalanceLineSource, ResultsetBalanceLineSource).

---

##### Concepts - Transaction File #####
Is the secoundary data set, represents the transactions made, contais the data that must be syncronized with the orign. In the framework it can be any source, that can be implemented by hand or can be used the built in sources (CollectionBalanceLineSource, InputStreamBalanceLineSource, MapBalanceLineSource, ResultsetBalanceLineSource).

---

##### Concepts - Key #####
Is an unique identificator that identifies one single record (can be a single field, a mix of fields, a SHA-1 hash and so on).

---

#### Index of Samples <a id="samples"/> ####

1. [Simple Collection Balance Line Example](#example-1)
1. MORE SAMPLES COMMING SOON

---

##### 1 - Simple Collection Balance Line Example <a id="example-1"/> #####

```java
// package and imports omitted

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
```
