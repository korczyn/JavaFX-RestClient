package com.capgemini.starterkit.javafx.dataprovider.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.capgemini.starterkit.javafx.dataprovider.DataProvider;
import com.capgemini.starterkit.javafx.dataprovider.data.BookVO;

public class DataProviderImpl implements DataProvider {

	private Collection<BookVO> books = new ArrayList<>();

	public DataProviderImpl(){
		books.add(new BookVO(1L, "pierwsza", "jan koza"));
		books.add(new BookVO(2L, "druga", "bozena zawadzka"));
		books.add(new BookVO(3L, "trzecia", "john doe"));
		books.add(new BookVO(4L, "czwarta", "jasiu mela"));
		books.add(new BookVO(5L, "piata", "konstanty staly"));
		books.add(new BookVO(6L, "szosta", "marcin koszela"));
		books.add(new BookVO(7L, "siodma", "marta klos"));
	}

	@Override
	public Collection<BookVO> findBookByPrefix(String prefix) {
		Collection<BookVO> result = new ArrayList<BookVO>();
		for (BookVO bookVO : books) {
			if(bookVO.getTitle().startsWith(prefix)){
				result.add(bookVO);
			}
		}
		return result;
	}

}
