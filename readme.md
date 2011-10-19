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
1. [Convencional Solution vs. BalanceLine4j Solution Example](#example-2)

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

---

##### 2 - Convencional Solution vs. BalanceLine4j Solution Example <a id="example-2"/> #####

This sample is about a benchmark scenario that compares the convencional (dummy) solution against the BalanceLine4j solution. Some considerations about the sample:
* Master File: a database table named "CUSTOMER"
* Transaction File: a positional text file named "sample_customers_file.txt" containing the very changes
* Key: is used the customer ID, that is the primary key of the table and the first field of the flat file
* This sample uses the ffpojo-core-0.1 library to facilitate the text file parsing - see the [project site](https://github.com/gibaholms/ffpojo)
* This sample also depends on the JDBC driver of the chosen database (this code is Oracle based)
* The sample code is "one layer" only to facilitate demonstration, but dont forget the patterns in the real implementation

Scenario setup:
1. Install OracleXE database (or other one of your choice, but this sample is Oracle based)
1. Create one user-schema named "TESTS" with password "TESTS"
1. Run the create table scripts on file "benchmark-resources/ddl_create_table.sql"
1. To generate some sample data, run the insert scripts on file "benchmark-resources/sql_insert_into.sql"
1. Copy the file "benchmark-resources/sample_customers_file.txt" to some location at your disk (dont forget to change the file path references in the java code)
1. Now you are ready to run the two main benchmark starter files through the main method (MainConventionalSolution.java and MainBalanceLineSolution.java)
1. Dont forget to erase the table data and insert the sample data again before start the benchmarks, case else the table will be up-to-date

Customer.java
```java
// package and imports omitted

@PositionalRecord
public class Customer {

	private long id;
	private String name;
	private String address;
	private String phone;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		return true;
	}
	
	@PositionalField(initialPosition = 1, finalPosition = 10)
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setId(String id) {
		this.id = Long.valueOf(id);
	}
	
	@PositionalField(initialPosition = 11, finalPosition = 25)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@PositionalField(initialPosition = 26, finalPosition = 40)
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@PositionalField(initialPosition = 41, finalPosition = 55)
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
```

CustomerDatabaseBalanceLineSource.java
```java
// package and imports omitted

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
```

CustomerTextBalanceLineSource.java
```java
// package and imports omitted

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
```

CustomerBalanceLineProcessor.java
```java
// package and imports omitted

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
```

MainBalanceLineSolution.java
```java
// package and imports omitted

public class MainBalanceLineSolution {

	public static void main(String[] args) throws FFPojoException, BalanceLineException, SQLException, FileNotFoundException {
		MainBalanceLineSolution solution = new MainBalanceLineSolution();
		long start = System.currentTimeMillis();
		solution.process();
		float elapsedTimeSec = (System.currentTimeMillis() - start)/1000F;
		System.out.println("Elapsed Solution Time (sec): " + elapsedTimeSec);
	}
	
	public void process() {
		// change the path to file in your machine
		File file = new File("c:/sample_customers_file.txt");
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		InputStream inputStream = null;
		try {			
			conn = ConnectionFactory.createConnection();
			
			stmt = conn.prepareStatement("SELECT * FROM CUSTOMER ORDER BY IDCUSTOMER");
			rset = stmt.executeQuery();
			
			inputStream = new FileInputStream(file);
			
			CustomerBalanceLineProcessor customerBalanceLineProcessor = new CustomerBalanceLineProcessor(conn);
			
			BalanceLineSource<Long, Customer> customerMasterSource = new CustomerDatabaseBalanceLineSource(rset);
			BalanceLineSource<Long, Customer> customerTransactionSource = new CustomerTextBalanceLineSource(inputStream);
			BalanceLine<Long, Customer, Customer> balanceLine = new BalanceLineImpl<Long, Customer, Customer>(customerTransactionSource, customerMasterSource, customerBalanceLineProcessor);
			balanceLine.execute();
			
			// RETRIEVE STATISTICS FROM ORACLE SESSION
			System.out.println("----> ORACLE STATISTICS:");
			stmt = conn.prepareStatement("select SS.sid, SS.program, SN.name, SE.value from V$SESSION SS, V$SESSTAT SE, V$STATNAME SN where SE.SID = SS.SID and se.statistic# = sn.statistic# and SE.statistic# = 12 and SS.username = ?");
			stmt.setString(1, "TESTS");
			rset = stmt.executeQuery();
			while (rset.next()) {
				String sessionId = rset.getString("SID");
				String application = rset.getString("PROGRAM");
				String statisticName = rset.getString("NAME");
				String statisticValue = rset.getString("VALUE");
				System.out.printf("Sid: %s | App: %s | Stat: %s | Val: %s", sessionId, application, statisticName, statisticValue);
				System.out.println();
			}
			rset.close();
			stmt.close();
			
		} catch(SQLException e) {
			e.printStackTrace();
		} catch (BalanceLineException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try { if (rset != null) rset.close(); } catch(Exception e) {}
			try { if (stmt != null) stmt.close(); } catch(Exception e) {}
			try { if (conn != null) conn.close(); } catch(Exception e) {}
			try { if (inputStream != null) inputStream.close(); } catch(Exception e) {}
		}
	}
	
}
```

MainConventionalSolution.java
```java
// package and imports omitted

public class MainConventionalSolution {

	public static void main(String[] args) throws FFPojoException {
		MainConventionalSolution solution = new MainConventionalSolution();
		long start = System.currentTimeMillis();
		solution.process();
		float elapsedTimeSec = (System.currentTimeMillis() - start)/1000F;
		System.out.println("Elapsed Solution Time (sec): " + elapsedTimeSec);
	}
	
	public void process() {
		// change the path to file in your machine
		File file = new File("C:/sample_customers_file.txt");
		BufferedReader reader = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		Set<Long> transactionCustomersIds = new TreeSet<Long>();
		try {			
			conn = ConnectionFactory.createConnection();
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while((line=reader.readLine()) != null) {
				Customer transactionCustomer = FFPojoHelper.getInstance().createFromText(Customer.class, line);
				transactionCustomersIds.add(transactionCustomer.getId());
				
				// TRY TO FIND THE SAME CUSTOMER IN DATABASE
				stmt = conn.prepareStatement("SELECT * FROM CUSTOMER WHERE IDCUSTOMER = ?");
				stmt.setLong	(1, transactionCustomer.getId());
				rset = stmt.executeQuery();
				Customer masterCustomer = null; 
				if (rset.next()) {
					masterCustomer = new Customer();
					masterCustomer.setId(rset.getLong("IDCUSTOMER"));
					masterCustomer.setName(rset.getString("NAME"));
					masterCustomer.setAddress(rset.getString("ADDRESS"));
					masterCustomer.setPhone(rset.getString("PHONE"));
				}
				rset.close();
				stmt.close();
				
				// IF THE CUSTOMER NOT EXISTS, INSERT NEW CUSTOMER
				if (masterCustomer == null) {
					System.out.println("Inserting new customer...");
					stmt = conn.prepareStatement("INSERT INTO CUSTOMER (IDCUSTOMER,NAME,ADDRESS,PHONE) VALUES (?,?,?,?)");
					stmt.setLong	(1, transactionCustomer.getId());
					stmt.setString	(2, transactionCustomer.getName());
					stmt.setString	(3, transactionCustomer.getAddress());
					stmt.setString	(4, transactionCustomer.getPhone());
					stmt.executeUpdate();
					stmt.close();
				} else {
					// IF THE CUSTOMER ALREADY EXISTS, VERIFY IF WAS CHANGED
					if (!transactionCustomer.equals(masterCustomer)) {
						// IF THE CUSTOMER RECORD WAS CHANGED, UPDATE THE RECORD
						System.out.println("Updating existing customer...");
						stmt = conn.prepareStatement("UPDATE CUSTOMER SET NAME=?,ADDRESS=?,PHONE=? WHERE IDCUSTOMER=?");
						stmt.setString	(1, transactionCustomer.getName());
						stmt.setString	(2, transactionCustomer.getAddress());
						stmt.setString	(3, transactionCustomer.getPhone());
						stmt.setLong	(4, transactionCustomer.getId());
						stmt.executeUpdate();
						stmt.close();
					}
				}
			}
			
			// DELETE FROM DATABASE THE CUSTOMERS THAT NOT EXISTS ANYMORE IN TRANSACTION FILE
			stmt = conn.prepareStatement("SELECT IDCUSTOMER FROM CUSTOMER ORDER BY IDCUSTOMER");
			rset = stmt.executeQuery();
			while (rset.next()) {
				long masterCustomerId = rset.getLong("IDCUSTOMER");
				if (!transactionCustomersIds.contains(masterCustomerId)) {
					System.out.println("Deleting existing customer...");
					stmt = conn.prepareStatement("DELETE FROM CUSTOMER WHERE IDCUSTOMER=?");
					stmt.setLong	(1, masterCustomerId);
					stmt.executeUpdate();
					stmt.close();
				}
			}
			rset.close();
			stmt.close();
			
			// RETRIEVE STATISTICS FROM ORACLE SESSION
			System.out.println("----> ORACLE STATISTICS:");
			stmt = conn.prepareStatement("select SS.sid, SS.program, SN.name, SE.value from V$SESSION SS, V$SESSTAT SE, V$STATNAME SN where SE.SID = SS.SID and se.statistic# = sn.statistic# and SE.statistic# = 12 and SS.username = ?");
			stmt.setString(1, "TESTES");
			rset = stmt.executeQuery();
			while (rset.next()) {
				String sessionId = rset.getString("SID");
				String application = rset.getString("PROGRAM");
				String statisticName = rset.getString("NAME");
				String statisticValue = rset.getString("VALUE");
				System.out.printf("Sid: %s | App: %s | Stat: %s | Val: %s", sessionId, application, statisticName, statisticValue);
				System.out.println();
			}
			rset.close();
			stmt.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch(SQLException e) {
			e.printStackTrace();
		} catch (FFPojoException e) {
			e.printStackTrace();
		} finally {
			try { if (reader != null) reader.close(); } catch(Exception e) {}
			try { if (rset != null) rset.close(); } catch(Exception e) {}
			try { if (stmt != null) stmt.close(); } catch(Exception e) {}
			try { if (conn != null) conn.close(); } catch(Exception e) {}
		}
	}
	
}
```
