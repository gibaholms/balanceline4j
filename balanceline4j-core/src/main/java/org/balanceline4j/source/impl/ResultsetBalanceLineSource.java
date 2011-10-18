package org.balanceline4j.source.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.balanceline4j.exception.BalanceLineSourceException;
import org.balanceline4j.source.BalanceLineSource;



public abstract class ResultsetBalanceLineSource<K extends Comparable<? super K>, R> implements BalanceLineSource<K, R> {

	protected ResultSet resultset;
	
	public ResultsetBalanceLineSource(ResultSet resultset) {
		if (resultset == null) {
			throw new IllegalArgumentException("ResultSet object cannot be null");
		}
		this.resultset = resultset;
	}
	
	public final boolean next() throws BalanceLineSourceException {
		try {
			return resultset.next();
		} catch (SQLException e) {
			throw new BalanceLineSourceException(e);
		}
	}

	public final boolean isBeforeFirst() throws BalanceLineSourceException {
		try {
			return resultset.isBeforeFirst();
		} catch (SQLException e) {
			throw new BalanceLineSourceException(e);
		}
	}
	
	public final boolean isAfterLast() throws BalanceLineSourceException {
		try {
			return resultset.isAfterLast();
		} catch (SQLException e) {
			throw new BalanceLineSourceException(e);
		}
	}

	public final boolean resetSupported() throws BalanceLineSourceException {
		try {
			if (resultset.getType() == ResultSet.TYPE_FORWARD_ONLY) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			throw new BalanceLineSourceException(e);
		}
	}
	
	public final void reset() throws BalanceLineSourceException {
		if (resultset != null && !isBeforeFirst() && resetSupported()) {
			try {
				resultset.beforeFirst();
			} catch (SQLException e) {
				throw new BalanceLineSourceException(e);
			}
		}
	}

}
