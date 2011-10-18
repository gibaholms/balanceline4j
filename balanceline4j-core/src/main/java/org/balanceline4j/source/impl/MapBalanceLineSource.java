package org.balanceline4j.source.impl;

import java.util.Iterator;
import java.util.Map;

import org.balanceline4j.processor.record.event.DefaultRecordEvent;
import org.balanceline4j.processor.record.event.RecordEvent;
import org.balanceline4j.source.BalanceLineSource;



public class MapBalanceLineSource<K extends Comparable<? super K>, R> implements BalanceLineSource<K, R> {

	private Map<K, R> recordsByKey;
	private Iterator<K> recordKeysIterator;
	protected R record;
	protected K key;
	protected int size;
	protected int count;

	public MapBalanceLineSource(Map<K, R> recordsByKey) {
		this.recordsByKey = recordsByKey;
		this.recordKeysIterator = recordsByKey.keySet().iterator();
		this.size = recordsByKey.keySet().size();
		this.count = 0;
	}

	public RecordEvent<K, R> getRecordEvent() {
		return new DefaultRecordEvent<K, R>(key, record);
	}
	
	public final boolean next() {
		count++;
		if (recordKeysIterator.hasNext()) {
			key = recordKeysIterator.next();
			record = recordsByKey.get(key);
			return true;
		} else {
			return false;
		}
	}
	
	public final boolean isBeforeFirst() {
		if (count == 0 && recordKeysIterator.hasNext()) {
			return true;
		} else {
			return false;
		}
	}
	
	public final boolean isAfterLast() {
		if (size > 0 && count > size) {
			return true;
		} else {
			return false;
		}
	}

	public final boolean resetSupported() {
		return true;
	}
	
	public final void reset() {
		recordKeysIterator = recordsByKey.keySet().iterator();
	}
	
}
