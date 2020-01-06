package org.internet.yggtorrent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class HttpUrlConnectionExample {

	private final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/66.0.3359.181 Chrome/66.0.3359.181 Safari/537.36";

	private List<String> cookies;
	private HttpsURLConnection conn;
	private HttpUrlConnectionExample mHTTP;


  // You need to connect (sConnectionURL) to website, before consulting it (sConsultationURL).
  public boolean connectToSite(String sConnectionURL, String sLogin, String sPassword, String sURLForFormIdentification) throws Exception {
	mHTTP = new HttpUrlConnectionExample();

	// Make sure cookies is turned on.
	CookieHandler.setDefault(new CookieManager());

	// Send a "GET" request, so that you can extract the form's data.
	String sPage = mHTTP.GetPageContent(sConnectionURL);
	if (sPage.length()==0) {
		System.out.print("KO: connexion page from \"" + sConnectionURL + "\" is empty. --> Aborting.");
		return false;
	}
	String postParams = mHTTP.getFormParams(sPage, sLogin, sPassword, sURLForFormIdentification);

		
	
	
	// Construct above post's content and then send a POST request for authentication.
	mHTTP.sendPost(sConnectionURL, postParams);

	return true;
  }

	public LinkedList<String> listOverviewPages(String sConsultationURL, String sStartOfBlurbs) throws Exception {
		LinkedList<String> lstReturnValue=null;
	
		if (mHTTP!=null) {
	
			// GET LIST OF HTML LINKS OF THE CURRENT PAGE
			String sHTMLPage = mHTTP.GetPageContent(sConsultationURL);
			Document docHTML = Jsoup.parse(sHTMLPage);
			Elements lstLink = docHTML.select("a[href]");
	
			// KEEP ONLY LINKS TO OVERVIEW PAGES
			lstReturnValue=new LinkedList<String>();
			for (Element link : lstLink) {
				if (link.attr("abs:href").startsWith(sStartOfBlurbs) && !link.attr("abs:href").endsWith("#comments")) {
					lstReturnValue.add(link.attr("abs:href"));
				}
	        }
			
			// FREE RESOURCES
			lstLink.clear();
		  }
		  
		  return lstReturnValue;
	  }

  public String retrieveFileDownloadLinkFromBlurb(String sOverviewPageURL, String sStartOfDownloadLinks) throws Exception {
	  String sReturnValue=null;
	  
	  
//	  System.out.println("DETAIL PAGE URL: >" + sOverviewPageURL + "<");
	  
//	  if (!sOverviewPageURL.equals("https://yggtorrent.com/torrent/ebook/livres/190799-la+provence+marseille+du+dimanche+18+février+2018")) {
//		  return "IGNORÉE";
//	  }
	  
	  if (mHTTP!=null) {
		String sHTMLPage = mHTTP.GetPageContent(sOverviewPageURL);
	  
		if (sHTMLPage==null) {
			return null;
		}
		Document docHTML = Jsoup.parse(sHTMLPage);

//		Document doc = Jsoup.connect(url).get();
        Elements lstLink = docHTML.select("a[href]");

//    	lstReturnValue=new LinkedList<String>();
    	
        for (Element link : lstLink) {
        	if (link.attr("abs:href").startsWith(sStartOfDownloadLinks)) {
        		sReturnValue=link.attr("abs:href");
        	}
        }
	  }
	  
	  return sReturnValue;
  }

  
  private void sendPost(String sURL, String postParams) throws Exception {

	URL obj = new URL(sURL);
	conn = (HttpsURLConnection) obj.openConnection();

	// Acts like a browser
	conn.setUseCaches(false);
	conn.setRequestMethod("POST");
	conn.setRequestProperty("Host", "ww1.yggtorrent.is");
	conn.setRequestProperty("User-Agent", USER_AGENT);
	conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	for (String cookie : this.cookies) {
		conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
	}
	conn.setRequestProperty("Connection", "keep-alive");
	conn.setRequestProperty("Referer", "https://ww1.yggtorrent.is/engine/search?name=&description=&file=&uploader=&category=2140&sub_category=2154&do=search");
	conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	conn.setRequestProperty("Content-Length", Integer.toString(postParams.length()));

	conn.setDoOutput(true);
	conn.setDoInput(true);

	// Send post request
	DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	wr.writeBytes(postParams);
	wr.flush();
	wr.close();

	int nResponseCode = conn.getResponseCode();
	System.out.println("\nSending 'POST' request to URL: " + sURL);
	System.out.println("Post parameters: " + postParams);
	System.out.println("Response Code: " + nResponseCode);

	
	if (nResponseCode==200) {
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String sInputLine;
		StringBuffer sbResponse = new StringBuffer();
	
		while ((sInputLine = in.readLine()) != null) {
			sbResponse.append(sInputLine);
		}
		in.close();
		// System.out.println(response.toString());
	}
  }

  private String removeFrenchAccents(String sSentence) {
  	// The two following arrays are ordered.
      final char[] acAccentedLetter = {'À','Á','Â','Ã','Ä','Å','Ç','È','É','Ê','Ë','Ì','Í','Î','Ï','Ñ','Ò','Ó','Ô','Õ','Ö','Ù','Ú','Û','Ü','Ý','à','á','â','ã','ä','ç','è','é','ê','ë','ì','í','î','ï','ñ','ò','ó','ô','õ','ö','ù','ú','û','ü','ý','ÿ'};  // Ordered. 
      final char[] acNotAccentedLetter = {'A','A','A','A','A','A','C','E','E','E','E','I','I','I','I','N','O','O','O','O','O','U','U','U','U','Y','a','a','a','a','a','c','e','e','e','e','i','i','i','i','n','o','o','o','o','o','u','u','u','u','y','y'};   // Ordered.
      char cCurrentChar;
      int nSearchResult;
      char[] acReturnValue = new char[sSentence.length()*2];	// Length doubled for ligatures (see below).
      
      int nJ=0;	// Cursor of the output string (acReturnValue[]).
      final int nSentenceLength=sSentence.length();
      for (int nI=-1; ++nI<nSentenceLength;) {
      	// Letter without accent.
          if (((cCurrentChar=sSentence.charAt(nI))>='a' && cCurrentChar<='z') || (cCurrentChar>='A' && cCurrentChar<='Z')) {
          	acReturnValue[nJ++]=cCurrentChar;
          }
          else {
          	// Accented letters.
          	// If binary search doesn't work anymore : order the accented characters by use (i.e. 'é', 'è' at the beginning; 'ÿ' at the end).
              if ((nSearchResult=Arrays.binarySearch(acAccentedLetter,cCurrentChar))>-1) {
                  acReturnValue[nJ++]=acNotAccentedLetter[nSearchResult];
              }
              else {
              	// Ligatures (aesc and ethel: Æ æ Œ œ).
                  if (cCurrentChar=='œ') {
                  	acReturnValue[nJ++]='o';
                  	acReturnValue[nJ++]='e';
                  } else if (cCurrentChar=='Œ') {
                  	acReturnValue[nJ++]='O';
                  	acReturnValue[nJ++]='E';
                  } else if (cCurrentChar=='æ') {
                  	acReturnValue[nJ++]='a';
                  	acReturnValue[nJ++]='e';
                  } else if (cCurrentChar=='Æ') {
                  	acReturnValue[nJ++]='A';
                  	acReturnValue[nJ++]='E';
                  } else {
                  	// Other character ('.', ',', ...).
                  	acReturnValue[nJ++]=cCurrentChar;
                  }
              }
          }
      } 
      
      return new String(acReturnValue,0, nJ);
  }
  
  private String GetPageContent(String url) throws Exception {

	  
	  
//	     String sEncodedURL = null;
//	     try {
//	       sEncodedURL = URLEncoder.encode(url, "UTF-8");
//	     }
//	     catch (UnsupportedEncodingException ex){
//	       throw new RuntimeException("UTF-8 not supported", ex);
//	     }
	  
	  
//	     byte[] ptext = url.getBytes(ISO_8859_1); 
//	     String sEncodedURL = new String(ptext, UTF_8); 
	     
	     
	  String sEncodedURL = removeFrenchAccents(url);
	  
	URL obj = new URL(sEncodedURL);
	conn = (HttpsURLConnection) obj.openConnection();

	// Default is GET
	conn.setRequestMethod("GET");

	conn.setUseCaches(false);

	// Act like a browser
	https://ww1.yggtorrent.is/user/login
	


	
	
	
//	conn.setRequestProperty("User-Agent", USER_AGENT);
//	conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//	conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

	conn.setRequestProperty("Host", "cdnjs.cloudflare.com");
	conn.setRequestProperty("User-Agent", USER_AGENT);
	conn.setRequestProperty("Accept", "*/*");
	conn.setRequestProperty("Accept-Language", "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
//	conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
//	conn.setRequestProperty("Referer", "https://yggtorrent.com/");
	conn.setRequestProperty("Referer", "https://ww1.yggtorrent.is/");
//	Cookie: __cfduid=d26c30797c76d290eb002dc10c2d612781503940745
	conn.setRequestProperty("Connection", "keep-alive");
	
	
	
	
	
	if (cookies != null) {
		for (String cookie : this.cookies) {
			conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
		}
	}
	int nResponseCode = conn.getResponseCode();
	System.out.print("\nSending 'GET' request to URL: \"" + url + "\" --> Response code: " + nResponseCode);
	if (nResponseCode==200) {
		System.out.print(" --> OK\n");
	}
	else {
		System.out.print(" --> NOK\n");		
	}
	
	if (nResponseCode==200) {
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
	
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		// Get the response cookies
		setCookies(conn.getHeaderFields().get("Set-Cookie"));

		return response.toString();
	}
	else {
		return null;
	}

  }

  public String getFormParams(String sHTMLPage, String username, String password, String sURL) throws UnsupportedEncodingException {

	System.out.print("\tExtracting form's data... ");

	Document doc = Jsoup.parse(sHTMLPage);

	// Form id
	//	Element loginform = doc.getElementById("gaia_loginform");	
	Elements loginform_ = doc.getElementsByAttributeValue("action", sURL);
	if (loginform_.size()==1) {
		System.out.print("--> only one form found --> OK.");
	}
	Element loginform = loginform_.first();
	Elements inputElements = loginform.getElementsByTag("input");
	List<String> paramList = new ArrayList<String>();
	for (Element inputElement : inputElements) {
		String key = inputElement.attr("name");
		String value = inputElement.attr("value");

		if (key.equals("id")) {
			value = username; }
		else if (key.equals("pass")) {
			value = password; }
		paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
	}

	// build parameters list
	StringBuilder sbReturnValue = new StringBuilder();
	for (String param : paramList) {
		if (sbReturnValue.length() == 0) {
			sbReturnValue.append(param);
		} else {
			sbReturnValue.append("&" + param);
		}
	}
	
	return sbReturnValue.toString();
  }

  public List<String> getCookies() {
	return cookies;
  }

  public void setCookies(List<String> cookies) {
	this.cookies = cookies;
  }
  
  private static final int BUFFER_SIZE = 4096;
  
  /**
   * This function has been downloaded from www.codejava.net 
   * and was part of class HttpDownloadUtility
   * 
   * Downloads a file from a URL
   * @author www.codejava.net
   * @param fileURL HTTP URL of the file to be downloaded
   * @param saveDir path of the directory to save the file
   * @throws IOException
   */
  public String downloadFile(String fileURL, String saveDir) throws IOException {
      URL url = new URL(fileURL);
      HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
      int responseCode = httpConn.getResponseCode();

      String fileName = null;

      // always check HTTP response code first
      if (responseCode == HttpURLConnection.HTTP_OK) {
          String disposition = httpConn.getHeaderField("Content-Disposition");
//          String contentType = httpConn.getContentType();
//          int contentLength = httpConn.getContentLength();

          if (disposition != null) {
              // extracts file name from header field
              int index = disposition.indexOf("filename=");
              if (index > 0) {
                  fileName = disposition.substring(index + 10, disposition.length() - 1);
              }
          } else {
              // extracts file name from URL
              fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
          }

//          System.out.println("Content-Type = " + contentType);
//          System.out.println("Content-Disposition = " + disposition);
//          System.out.println("Content-Length = " + contentLength);
//          System.out.println("fileName = " + fileName);

          // opens input stream from the HTTP connection
          InputStream inputStream = httpConn.getInputStream();
          String saveFilePath = saveDir + File.separator + fileName;
           
          // opens an output stream to save into file
          FileOutputStream outputStream = new FileOutputStream(saveFilePath);

          int bytesRead = -1;
          byte[] buffer = new byte[BUFFER_SIZE];
          while ((bytesRead = inputStream.read(buffer)) != -1) {
              outputStream.write(buffer, 0, bytesRead);
          }

          outputStream.close();
          inputStream.close();

//          System.out.println("File downloaded");
      } else {
//          System.out.println("No file to download. Server replied HTTP code: " + responseCode);
      }
      httpConn.disconnect();

      return fileName;
  }


}