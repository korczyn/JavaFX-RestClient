package com.capgemini.starterkit.javafx.dataprovider;

import java.util.Collection;

import com.capgemini.starterkit.javafx.dataprovider.data.BookVO;
import com.capgemini.starterkit.javafx.dataprovider.impl.DataProviderImpl;

public interface DataProvider {
	DataProvider INSTANCE = new DataProviderImpl();
	Collection<BookVO> findBookByPrefix(String prefix);
	Collection<BookVO> findBookByPrefixRest(String prefix) throws Exception;
}
