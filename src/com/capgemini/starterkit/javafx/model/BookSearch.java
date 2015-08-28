package com.capgemini.starterkit.javafx.model;

import java.util.ArrayList;
import java.util.List;

import com.capgemini.starterkit.javafx.dataprovider.data.BookVO;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class BookSearch {
	private final StringProperty titlePrefix = new SimpleStringProperty();
	private final ListProperty<BookVO> result = new SimpleListProperty<>(
			FXCollections.observableList(new ArrayList<>()));

	public String getTitlePrefix(){
		return titlePrefix.get();
	}

	public void setTitlePrefix(String titlePrefix){
		this.titlePrefix.set(titlePrefix);
	}

	public StringProperty titlePrefixProperty(){
		return titlePrefix;
	}

	public List<BookVO> getResult(){
		return result.get();
	}

	public void setResult(List<BookVO> value){
		this.result.setAll(value);
	}

	public ListProperty<BookVO> resultProperty(){
		return result;
	}
}
