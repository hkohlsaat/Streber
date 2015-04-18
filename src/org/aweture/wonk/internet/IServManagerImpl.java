package org.aweture.wonk.internet;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import org.aweture.wonk.log.LogUtil;

/**
 * {@link IServManager} implementation.
 * 
 * @author Hannes Kohlsaat
 *
 */
public class IServManagerImpl implements IServManager {

	private URL iServRoot;
	private URL iServSubstitutes;
	private URI iServURI;
	
	private final String username;
	private final String password;
	
	
	public IServManagerImpl(String username, String password) {
		this.username = username;
		this.password = password;

		try {
			iServRoot = new URL("https://gsp-ploen.de/idesk/");
			iServSubstitutes = new URL("https://gsp-ploen.de/idesk/infodisplay/mods/link.local.php/panelId=50/run/Schueler_Online/subst_001.htm");
			iServURI = new URI("https://gsp-ploen.de/");
		} catch (MalformedURLException e) {
			LogUtil.e(e);
		} catch (URISyntaxException e) {
			LogUtil.e(e);
		}
	}
	
	public LoginResult logIn() {
		if (CookieHandler.getDefault() == null) {
			CookieHandler.setDefault(new CookieManager());
		}
		
		HttpsURLConnection conn = null;
		BufferedOutputStream out = null;
		
		try {
			String loginString =  "login_act=" + username + "&login_pwd=" + password;
			byte[] loginBytes = loginString.getBytes("utf-8");
			
			// Open a connection to the IDesk and set it up.
			conn = (HttpsURLConnection) iServRoot.openConnection();
			// Announce that we want to write to an Outputstream.
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			// Announce that something (our user data) will be posted.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        conn.setFixedLengthStreamingMode(loginBytes.length);
	        
	        // Post the data needed for login.
			out = new BufferedOutputStream(conn.getOutputStream());
			out.write(loginBytes);
			out.flush();
			out.close();
			
			// Send the data and wait until the server answers by this blocking call.
			conn.getResponseCode();
			conn.disconnect();
		} catch (IOException e) {
			closeResources(conn, out);
			LogUtil.e(e);
			return LoginResult.NetworkFail;
		}
		
		if(isLoggedIn()) {
			return LoginResult.Success;
		} else {
			return LoginResult.WrongData;
		}
	}
	
	
	public String downloadSubstitutionPlan() throws IOException {
		
		// Open the connection and then connect to the plan location.
		HttpsURLConnection conn = (HttpsURLConnection) iServSubstitutes.openConnection();
		conn.connect();
		
		// Read the stream line by line (and set linebreaks after them, which is easier to debug);
		Scanner scanner = new Scanner(conn.getInputStream());
		StringBuilder plan = new StringBuilder();
		while (scanner.hasNextLine()) {
			plan.append(scanner.nextLine() + "\n");
		}
		// Close all things having to be closed.
		scanner.close();
		conn.disconnect();
		
		return plan.toString();
	}
	
	
	public boolean isLoggedIn() {
		List<HttpCookie> cookies = getCookies();
		return cookies.size() >= 2 && !cookies.get(0).hasExpired() && !cookies.get(1).hasExpired();
	}
	
	
	private void closeResources(HttpsURLConnection conn, BufferedOutputStream out) {
		if (out != null) {
			try {
				out.close();
			} catch (IOException ioe) {}
		}
		
		if (conn != null) {
			conn.disconnect();
		}
	}
	
	
	private List<HttpCookie> getCookies() {
		
		// Get a CookieManager or create one.
		CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
		if (cookieManager == null) {
			cookieManager = new CookieManager();
			CookieHandler.setDefault(cookieManager);
		}
		
		// Get cookie list from the CookieStore.
		CookieStore cookieStore = cookieManager.getCookieStore();
		return cookieStore.get(iServURI);
	}
}