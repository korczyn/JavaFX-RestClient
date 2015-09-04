package com.capgemini.starterkit.javafx.dataprovider.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.capgemini.starterkit.javafx.dataprovider.DataProvider;
import com.capgemini.starterkit.javafx.dataprovider.data.BookVO;

public class DataProviderImpl implements DataProvider {

	private Collection<BookVO> books = new ArrayList<>();
	JSONParser parser = new JSONParser();
	private String currentFilter = "";

	public DataProviderImpl() {
		books.add(new BookVO(1L, "pierwsza", "jan koza"));
		books.add(new BookVO(2L, "druga", "bozena zawadzka"));
		books.add(new BookVO(3L, "trzecia", "john doe"));
		books.add(new BookVO(4L, "czwarta", "jasiu mela"));
		books.add(new BookVO(5L, "piata", "konstanty staly"));
		books.add(new BookVO(6L, "szosta", "marcin koszela"));
		books.add(new BookVO(7L, "siodma", "marta klos"));
	}

	public String getCurrentFilter(){
		return currentFilter;
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

	private String resolveLinks(Activity a){
		Scanner sc = null;
		try{
			sc = new Scanner(new File("C:\\StarterKit-JavaFX\\workspace-main\\RestClient\\resources\\conf\\restConf.json"));
			String json = sc.nextLine();
			parser = new JSONParser();
			Object object = parser.parse(json);
			JSONObject o = (JSONObject) object;
			String link = "";
			if (a.equals(Activity.ADD)) {
				link = (String) o.get("add");
			}
			if (a.equals(Activity.DELETE)) {
				link = (String) o.get("delete");
			}
			if (a.equals(Activity.SEARCH)) {
				link = (String) o.get("search");
			}
			return link;

		} catch (Exception e){
			e.printStackTrace();
		} finally {
			sc.close();
		}
		return null;
	}

	@Override
	public Collection<BookVO> findBookByPrefixRest(String prefix) throws Exception {
		String url = resolveLinks(Activity.SEARCH) + prefix;
		BufferedReader in = null;
		HttpURLConnection con = null;
		try {
			URL obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			currentFilter = prefix;
			return parseJSONToCollection(response.toString());
		} finally {
			in.close();
			con.disconnect();
		}
	}

	@Override
	public BookVO addBook(String title, String authors) {
		String url = resolveLinks(Activity.ADD);
		HttpURLConnection con = null;
		DataOutputStream dos = null;
		BookVO res = null;
		try {
			URL obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			con.connect();

			JSONObject json = new JSONObject();
			json.put("title", title);
			json.put("authors", authors);
			String input = json.toString();

			dos = new DataOutputStream(con.getOutputStream());
			dos.writeBytes(input);
			dos.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			res = parseJSONToBookVO(response.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			con.disconnect();
		}
		return res;
	}

	@Override
	public void deleteBook(Long id) {
		try {
			String url = resolveLinks(Activity.DELETE) + id;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("DELETE");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			con.connect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Collection<BookVO> parseJSONToCollection(String json) throws ParseException {
		Collection<BookVO> result = new ArrayList<>();
		parser = new JSONParser();
		Object object = parser.parse(json);
		JSONArray array = (JSONArray) object;

		for (Object book : array) {
			result.add(parseJSONToBookVO(book.toString()));
		}
		return result;
	}

	private BookVO parseJSONToBookVO(String json) throws ParseException {
		parser = new JSONParser();
		Object object = parser.parse(json);
		JSONObject o = (JSONObject) object;

		Long id = (Long) o.get("id");
		String title = (String) o.get("title");
		String authors = (String) o.get("authors");

		return new BookVO(id, title, authors);
	}

}
