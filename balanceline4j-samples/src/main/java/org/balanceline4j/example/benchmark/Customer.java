package org.balanceline4j.example.benchmark;

import org.ffpojo.metadata.positional.annotation.PositionalField;
import org.ffpojo.metadata.positional.annotation.PositionalRecord;

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
