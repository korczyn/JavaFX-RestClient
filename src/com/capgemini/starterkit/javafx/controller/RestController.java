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

/*
 * REV: lepsza nazwa to BookSearchController, ta klasa nie ma pojecia o RESTach
 */
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
		/*
		 * REV: tutaj zadziala tylko drugi bind, powinienes polaczyc bindy przez 'or'
		 */
		addBookButton.disableProperty().bind(titleField.textProperty().isEmpty());
		addBookButton.disableProperty().bind(authorsField.textProperty().isEmpty());
		deleteBookButton.disableProperty().bind(bookTable.getSelectionModel().selectedItemProperty().isNull());
	}

	private void initializeResultTable() {
		idColumn.setCellValueFactory(cellData -> new ReadOnlyLongWrapper(cellData.getValue().getId()));
		titleColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTitle()));
		authorsColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAuthors()));

		/*
		 * REV: ten listener nic nie robi???
		 */
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

	/*
	 * REV: nazwa powinna mowic co sie dzieje w kliencie, inne metody tez wysylaja request do serwera
	 */
	@FXML
	public void sendRequestToServer() throws Exception {
		Task<Collection<BookVO>> backgroundTask = new Task<Collection<BookVO>>() {

			@Override
			protected Collection<BookVO> call() throws Exception {
				/*
				 * REV: poprawniej byloby pobrac wartosc z modelu
				 */
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
				/*
				 * REV: poprawniej byloby ustawic wartosci w modelu
				 */
				titleField.setText("");
				authorsField.setText("");
				/*
				 * REV: co gdy filtr jest tak ustawiony, ze dodana ksiazka nie powinna sie pojawic?
				 */
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
		/*
		 * REV: lista powinna byc stala
		 */
		List<String> langs = new ArrayList<String>();
		langs.add("EN");
		langs.add("DE");
		langs.add("JP");
		langs.add("PL");
		langs.add("RU");
		langs.add("GR");
		langs.add("TA");

		/*
		 * REV: lepiej gdyby uzytkownik decydowal jaki jezyk wybiera :)
		 */
		Random r = new Random();
		int randLangIndex = r.nextInt(langs.size());
		Locale.setDefault(Locale.forLanguageTag(langs.get(randLangIndex)));
		/*
		 * REV: to jest troche niebezpieczne
		 * FXMLLoader tworzy nowe instancje obiektow zdefiniowanych w fxmlu (w tym tego kontrolera)
		 * Przy bardziej skomplikowanych konfiguracjach kontroler-model moze to prowadzic do memory leakow.
		 */
		anchor.getScene().setRoot(FXMLLoader.load(getClass().getResource("/view/Client.fxml"),
				ResourceBundle.getBundle("bundle/bundle")));
	}
}
