package com.capgemini.starterkit.javafx.controller;

import java.util.ArrayList;
import java.util.Collection;

import com.capgemini.starterkit.javafx.dataprovider.DataProvider;
import com.capgemini.starterkit.javafx.dataprovider.data.BookVO;
import com.capgemini.starterkit.javafx.model.BookSearch;

import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class RestController {

	@FXML TableView<BookVO> bookTable;
	@FXML TableColumn<BookVO, Number> idColumn;
	@FXML TableColumn<BookVO, String> titleColumn;
	@FXML TableColumn<BookVO, String> authorsColumn;
	@FXML TextField prefixField;
	@FXML Button getBooksButton;

	private final DataProvider dataProvider = DataProvider.INSTANCE;
	private BookSearch model = new BookSearch();

	@FXML
	private void initialize(){
		initializeResultTable();
		bookTable.itemsProperty().bind(model.resultProperty());
	}

	private void initializeResultTable() {
		idColumn.setCellValueFactory(cellData -> new ReadOnlyLongWrapper(cellData.getValue().getId()));
		titleColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTitle()));
		authorsColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAuthors()));

		bookTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BookVO>() {

			@Override
			public void changed(ObservableValue<? extends BookVO> observable, BookVO oldValue,
					BookVO newValue) {
				Task<Void> backgroundTask = new Task<Void>(){

					@Override
					protected Void call() throws Exception {
						return null;
					}
				};
				new Thread(backgroundTask).start();
			}
		});
	}

	@FXML public void getBooksByPrefix() {
		Collection<BookVO> result = dataProvider.findBookByPrefix(prefixField.getText());
		model.setResult(new ArrayList<BookVO>(result));
		bookTable.getSortOrder().clear();
	}


}
