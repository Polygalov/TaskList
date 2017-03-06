package ua.com.adr.android.tasklist.objects;

import java.io.Serializable;
import java.util.Date;

public class TodoDocument implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7367289796391092618L;

	public TodoDocument() {
		// TODO Auto-generated constructor stub
	}


	
	public TodoDocument(String name, String content, Date createDate) {
		super();
		this.name = name;
		this.content = content;
		this.createDate = createDate;
	}


	private String name;
	private String content;
	private Date createDate;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	

	
	@Override
	public String toString() {
		return name;
	}





}