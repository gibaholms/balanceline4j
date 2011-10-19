package org.balanceline4j.example.benchmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import org.ffpojo.FFPojoHelper;
import org.ffpojo.exception.FFPojoException;


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
