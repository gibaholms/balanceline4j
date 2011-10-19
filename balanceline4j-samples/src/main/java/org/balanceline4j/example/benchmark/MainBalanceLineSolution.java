package org.balanceline4j.example.benchmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.balanceline4j.BalanceLine;
import org.balanceline4j.BalanceLineImpl;
import org.balanceline4j.exception.BalanceLineException;
import org.balanceline4j.source.BalanceLineSource;
import org.ffpojo.exception.FFPojoException;


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
