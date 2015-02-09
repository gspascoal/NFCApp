package com.example.objetos;

public class ContentTag {
	
	 private long id;
	  private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	  @Override
	  public String toString() {
	    return id+" - "+name;
	  }
}
