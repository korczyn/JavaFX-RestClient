package com.capgemini.starterkit.javafx.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import com.capgemini.starterkit.javafx.dataprovider.DataProvider;
import com.capgemini.starterkit.javafx.dataprovider.data.BookVO;
import com.capgemini.starterkit.javafx.model.BookSearch;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.MenuButton;
import javafx.event.ActionEvent;

public class RestController {

	@FXML
	TableView<BookVO> bookTable;
	@FXML
	TableColumn<BookVO, Number> idColumn;
	@FXML
	TableColumn<BookVO, String> titleColumn;
	@FXML
	TableColumn<BookVO, String> authorsColumn;
	@FXML
	TextField prefixField;
	@FXML
	Button getBooksButton;
	@FXML
	Button requestButton;
	@FXML
	TextField titleField;
	@FXML
	TextField authorsField;
	@FXML
	Button addBookButton;
	@FXML
	Button changeLang;
	@FXML
	AnchorPane anchor;
	@FXML
	MenuButton changeLangMenu;
	@FXML
	Button deleteBookButton;

	private final DataProvider dataProvider = DataProvider.INSTANCE;
	private BookSearch model = new BookSearch();

	@FXML
	private void initialize() {
		initializeResultTable();
		bookTable.itemsProperty().bind(model.resultProperty());
		addBookButton.disableProperty().bind(titleField.textProperty().isEmpty());
		addBookButton.disableProperty().bind(authorsField.textProperty().isEmpty());
		deleteBookButton.disableProperty().bind(bookTable.getSelectionModel().selectedItemProperty().isNull());
	}

	private void initializeResultTable() {
		idColumn.setCellValueFactory(cellData -> new ReadOnlyLongWrapper(cellData.getValue().getId()));
		titleColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTitle()));
		authorsColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAuthors()));

		bookTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BookVO>() {

			@Override
			public void changed(ObservableValue<? extends BookVO> observable, BookVO oldValue, BookVO newValue) {

				Task<Void> backgroundTask = new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						return null;
					}
				};
				new Thread(backgroundTask).start();
			}
		});
	}

	@FXML
	public void sendRequestToServer() throws Exception {
		Task<Collection<BookVO>> backgroundTask = new Task<Collection<BookVO>>() {

			@Override
			protected Collection<BookVO> call() throws Exception {
				Collection<BookVO> result = dataProvider.findBookByPrefixRest(prefixField.getText());
				return result;
			}

			@Override
			protected void succeeded() {
				model.setResult(new ArrayList<BookVO>(getValue()));
				bookTable.getSortOrder().clear();
			}
		};
		new Thread(backgroundTask).start();
	}

	@FXML
	public void addBook() {
		Task<BookVO> backgroundTask = new Task<BookVO>() {

			@Override
			protected BookVO call() throws Exception {
					BookVO book = dataProvider.addBook(titleField.getText(), authorsField.getText());
					return book;
			}

			@Override
			protected void succeeded() {
				titleField.setText("");
				authorsField.setText("");
				bookTable.getItems().add(getValue());
			}
		};
		new Thread(backgroundTask).start();
	}

	@FXML
	public void deleteSelectedBook() {
		BookVO book = bookTable.getSelectionModel().getSelectedItem();
		Task<Void> backgroundTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				dataProvider.deleteBook(book.getId());
				return null;
			}

			@Override
			protected void succeeded() {
				bookTable.getItems().remove(book);
			}
		};
		new Thread(backgroundTask).start();
	}

	@FXML
	public void changeLanguage() throws IOException {
		List<String> langs = new ArrayList<String>();
		langs.add("EN");
		langs.add("DE");
		langs.add("JP");
		langs.add("PL");
		langs.add("RU");
		langs.add("GR");

		Random r = new Random();
		int randLangIndex = r.nextInt(langs.size());
		Locale.setDefault(Locale.forLanguageTag(langs.get(randLangIndex)));
		anchor.getScene().setRoot(FXMLLoader.load(getClass().getResource("/view/Client.fxml"),
				ResourceBundle.getBundle("bundle/bundle")));
	}
}
