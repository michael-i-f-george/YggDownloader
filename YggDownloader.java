package org.internet.yggtorrent;


import java.util.LinkedList;
import java.util.HashSet;
import java.io.File;


public class YggDownloader {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		// CONSTANTS.
		final String TORRENT_FILE_STORAGE_PLACE = "/media/data/torrents_a_telecharger/";
		final String LOG_FILE = "0000_PREVIOUSLY_DOWNLOADED_TORRENTS.log";
//		final String CONNECTION_URL = "https://yggtorrent.com/user/login";
		final String CONNECTION_URL = "https://ww1.yggtorrent.is/";
		final String URL_FOR_FORM_IDENTIFICATION = "https://ww1.yggtorrent.is/user/login";
//		final String FIRST_CONSULTATION_PAGE_URL = "https://yggtorrent.com/torrents/ebook/2154-livres";
		final String FIRST_CONSULTATION_PAGE_URL = "https://ww1.yggtorrent.is/engine/search?name=&description=&file=&uploader=&category=2140&sub_category=2154&do=search";
		final String START_OF_BLURBS = "https://ww1.yggtorrent.is/torrent/ebook/livres/";	// A blurb is the description of a book.
		final String START_OF_DOWNLOAD_LINKS = "https://ww1.yggtorrent.is/engine/download_torrent?id=";
		final String NEXT_PAGE_SUFFIX = "?&page=";
		final int NEXT_PAGE_STEP = 25;
		final String LOGIN = "kiwikiwi63";
		final String PASSWORD = "ytreza_";

		long lStartTime = System.currentTimeMillis();
		
		try {

			System.out.print("Lauching program.\n");

			// CREATE A FOLDER TO STORE TORRENT FILES IF NECESSARY.
			if (!(new File(TORRENT_FILE_STORAGE_PLACE)).exists()) {
				(new File(TORRENT_FILE_STORAGE_PLACE)).mkdir();
			}

			// DELETE PREVIOUSLY DOWNLOADED TORRENT FILES.
			System.out.print("Deleting previously downloaded torrent files...");
			if (!(new IOUtil()).deletePreviouslyDownloadedTorrents(TORRENT_FILE_STORAGE_PLACE)) {
				System.out.println("NOK --> Aborting.");
				return;
			}
			System.out.print(" OK\n");
			
			// GET LIST OF PREVIOUSLY DOWNLOADED TORRENTS FROM LOG.
			System.out.print("Get list of previously downloaded torrents from log...");
			HashSet<String> hsetPreviouslyDownloadedTorrent = null;
			if ((new File(TORRENT_FILE_STORAGE_PLACE+LOG_FILE)).exists()) {
				hsetPreviouslyDownloadedTorrent = (new IOUtil()).readTextFile(TORRENT_FILE_STORAGE_PLACE+LOG_FILE);
			}
			System.out.print(" OK\n");
			
			// CONNECT TO YGGTORRENT
			System.out.print("Connect to download site...\t");
			HttpUrlConnectionExample WebConnection = new HttpUrlConnectionExample();
			if (!WebConnection.connectToSite(CONNECTION_URL, LOGIN, PASSWORD, URL_FOR_FORM_IDENTIFICATION)) {
				return;
			}
			System.out.print(" OK\n");
						
			// DOWNLOAD TORRENT FILES
			int nAlreadyDownloadedCount=0;
			int nDownloadedNowCount=0;
			String sDownloadedFileName=null;
			LinkedList<String> lstBlurb=null;
			LinkedList<String> lstDownloadLink=null;
			LinkedList<String> lstDownloadedTorrentFile=new LinkedList<String>();
//			for (int nJ=0; nJ<7 && nAlreadyDownloadedCount<5; nJ++) {
			for (int nJ=0; nJ<1 && nAlreadyDownloadedCount<5; nJ++) {

				// Get list of overview pages
				System.out.print("Get list of blurbs...");
				lstBlurb = WebConnection.listOverviewPages(FIRST_CONSULTATION_PAGE_URL + NEXT_PAGE_SUFFIX + nJ*NEXT_PAGE_STEP, START_OF_BLURBS);
				System.out.print(" --> " + lstBlurb.size() + " found ");
				if (lstBlurb.size() > 0) {
					System.out.print(" --> OK\n");					
				}
				else {
					System.out.print(" --> NOK\n");										
				}
				
				// Retrieve download link from each blurb.
				lstDownloadLink=new LinkedList<String>();
				for(int nI=0; nI<lstBlurb.size(); nI++) {
					lstDownloadLink.add(WebConnection.retrieveFileDownloadLinkFromBlurb(lstBlurb.get(nI), START_OF_DOWNLOAD_LINKS));
					System.out.println(nJ + "/" + nI + " DOWNLOAD LINK: " + lstDownloadLink.getLast() + "\n");
				}
				
				// Download Torrent files
				for(int nI=0; nI<lstDownloadLink.size() && nAlreadyDownloadedCount<5; nI++) {
					sDownloadedFileName=null;
		        	sDownloadedFileName=WebConnection.downloadFile(lstDownloadLink.get(nI), TORRENT_FILE_STORAGE_PLACE);
	        		if (sDownloadedFileName!=null) {
	        			if (hsetPreviouslyDownloadedTorrent!=null && hsetPreviouslyDownloadedTorrent.contains(sDownloadedFileName)) {
	        				nAlreadyDownloadedCount++;
	        			}
	        			else {
	        				nDownloadedNowCount++;
		        			lstDownloadedTorrentFile.add(sDownloadedFileName);
		        			System.out.println(nJ + "/" + nI + " - TORRENT FILE DOWNLOADED: " + sDownloadedFileName + " to " + TORRENT_FILE_STORAGE_PLACE);
	        			}
        			}
				}
			
			}
			sDownloadedFileName=null;
			
			// DISCONNECT (not really: simply freeing resources)
			WebConnection = null;

			// LOG LIST OF DOWNLOADED TORRENTS
			(new IOUtil()).writeToTextFile(TORRENT_FILE_STORAGE_PLACE + LOG_FILE, lstDownloadedTorrentFile);
			if (hsetPreviouslyDownloadedTorrent!=null) {
				hsetPreviouslyDownloadedTorrent.clear();
			}
			lstDownloadedTorrentFile.clear();

			
			System.out.println("Downloaded " + nDownloadedNowCount + " file(s) in " + (System.currentTimeMillis()-lStartTime)/1000 + " seconds.");
		}
        catch (java.net.UnknownHostException ex) {
			System.out.println("Cannot connect to download site.");
        }		
        catch (Exception ex) {
            ex.printStackTrace();
        }		
		
	}
	
	
	
	

}
