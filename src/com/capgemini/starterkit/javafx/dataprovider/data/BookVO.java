package com.capgemini.starterkit.javafx.dataprovider.data;

public class BookVO {

	private Long id;
	private String title;
	private String authors;

	public BookVO(Long id, String title, String authors) {
		super();
		this.id = id;
		this.title = title;
		this.authors = authors;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}


}
