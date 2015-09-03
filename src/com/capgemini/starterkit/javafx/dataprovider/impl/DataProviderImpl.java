package com.capgemini.starterkit.javafx.dataprovider.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.capgemini.starterkit.javafx.dataprovider.DataProvider;
import com.capgemini.starterkit.javafx.dataprovider.data.BookVO;

public class DataProviderImpl implements DataProvider {

	private final String USER_AGENT = "Mozilla/5.0";
	private Collection<BookVO> books = new ArrayList<>();

	public DataProviderImpl() {
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
			if (bookVO.getTitle().startsWith(prefix)) {
				result.add(bookVO);
			}
		}
		return result;
	}

	@Override
	public Collection<BookVO> findBookByPrefixRest(String prefix) throws Exception {
		/*
		 * REV: lepiej pobrac z pliku konfiguracyjnego
		 */
		String url = "http://localhost:9721/workshop/rest/books/books-by-title?titlePrefix=" + prefix;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("GET");

		/*
		 * REV: czy to jest rzeczywiscie konieczne?
		 */
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		/*
		 * REV: uzywaj loggera
		 */
		System.out.println("Sending GET to " + url);
		System.out.println("Response code " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		/*
		 * REV: zamykanie/rozlaczanie powinno byc robione w bloku finally
		 */
		in.close();
		con.disconnect();

		return parseJSONToCollection(response.toString());
	}

	private Collection<BookVO> parseJSONToCollection(String json) throws ParseException {
		Collection<BookVO> result = new ArrayList<>();
		/*
		 * REV: parser moze byc zdefiniowany jako pole w tej klasie
		 */
		JSONParser parser = new JSONParser();
		Object object = parser.parse(json);
		JSONArray array = (JSONArray) object;

		for (Object book : array) {
			JSONObject o = (JSONObject) book;
			/*
			 * REV: mozna by wykorzystac parseJSONToBookVO() do parsowania elementow tablicy
			 */
			Long id = (Long) o.get("id");
			String title = (String) o.get("title");
			String authors = (String) o.get("authors");

			result.add(new BookVO(id, title, authors));
		}
		return result;
	}

	private BookVO parseJSONToBookVO(String json) throws ParseException{
		JSONParser parser = new JSONParser();
		Object object = parser.parse(json);
		JSONObject o = (JSONObject) object;

		Long id = (Long) o.get("id");
		String title = (String) o.get("title");
		String authors = (String) o.get("authors");

		return new BookVO(id, title, authors);
	}

	@Override
	public BookVO addBook(String title, String authors) {
		try {
			/*
			 * REV: j.w.
			 */
			String url = "http://localhost:9721/workshop/rest/books/addBook";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			con.connect();

			JSONObject json = new JSONObject();
			json.put("title", title);
			json.put("authors", authors);
			String input = json.toString();

			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(input);
			wr.flush();
			/*
			 * REV: zamykanie w bloku finally
			 */
			wr.close();

			int responseCode = con.getResponseCode();
			/*
			 * REV: j.w.
			 */
			System.out.println("Sending POST to " + url);
			System.out.println("Response code " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			/*
			 * REV: j.w.
			 */
			System.out.println(response);

			return parseJSONToBookVO(response.toString());

		} catch (Exception e) {
			/*
			 * REV: uzywaj loggera
			 * lepiej rzucic wyjatek i wyswietlic blad na GUI
			 */
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public void deleteBook(Long id) {
		try{
			/*
			 * REV: j.w.
			 */
			String url = "http://localhost:9721/workshop/rest/books/book/" + id;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("DELETE");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			con.connect();

			int responseCode = con.getResponseCode();
			/*
			 * REV: j.w.
			 */
			System.out.println("Sending DELETE to " + url);
			System.out.println("Response code " + responseCode);

		} catch(Exception e){
			/*
			 * REV: j.w.
			 */
			e.printStackTrace();
		}
	}
}
