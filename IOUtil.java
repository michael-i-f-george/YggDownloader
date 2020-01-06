package org.internet.yggtorrent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;

public class IOUtil {
	
	public HashSet<String> readTextFile(String sInputFile) {	// HashSet has been chosen for its .contains() method.
		HashSet<String> hsetReturnValue = new HashSet<String>();
		
		try {
			// OPEN FILE. 
	        BufferedReader brInputFile=new BufferedReader(new FileReader(sInputFile));
 
	        // READ FILE.             
			String sLine;
		    while ((sLine=brInputFile.readLine()) != null) {
		    	hsetReturnValue.add(sLine);
		    }
		
		    // CLOSE FILE.
		    brInputFile.close();                
		    brInputFile = null;
		}
		catch (java.io.FileNotFoundException e) {
			javax.swing.JOptionPane.showMessageDialog(null, "Input file (\"" + sInputFile + "\") does not exist.");
		}
		catch (java.io.IOException e) {
		   e.printStackTrace();
		}
		
		// RETURN VALUES.
		return hsetReturnValue;
	}
	
	
	
	
	public void writeToTextFile(String sFilePath, LinkedList<String> lstLine) {
    	final String EOL = System.getProperty("line.separator");
		
		try {
			int nLineCount = lstLine.size();

			// DISCARD.
			if (nLineCount==0) {
				return;
			}
			 
			// OPEN TEXT FILE.
			BufferedWriter bwOutputFile = new BufferedWriter(new FileWriter(sFilePath));
			
			// BUILD TEXT TO BE WRITTEN.
	        StringBuilder sbTextToExport = new StringBuilder();
			for (int nI=0; nI<nLineCount; nI++) {
		        sbTextToExport.append(lstLine.get(nI));
		        sbTextToExport.append(EOL);
			}			 

			// WRITE TEXT TO FILE.
			bwOutputFile.write(sbTextToExport.toString());

			// CLOSE TEXT FILE
			sbTextToExport.setLength(0);	// TODO: really necessary?
			sbTextToExport.trimToSize();	// TODO: really necessary?
			sbTextToExport=null;
			sbTextToExport = null;
			bwOutputFile.close();
			bwOutputFile=null;
			
		} catch (Exception e) {
			e.printStackTrace();
		}     		
	}

	public boolean deletePreviouslyDownloadedTorrents(String sDirectory) {
		return deleteFilesWithGivenExtension(new File(sDirectory), ".torrent", false, false);
	}
	
	private boolean deleteFilesWithGivenExtension(File ioDirectory, String sFileExtension, boolean bRecursive, boolean bDeleteDirectoryItself) {
		boolean bReturnValue = false;
		
	    if (ioDirectory.exists() && ioDirectory.isDirectory()) { 
	        bReturnValue = true; 
	        File[] iolstFile = ioDirectory.listFiles(); 
	        for (File ioFile : iolstFile) { 
	            if (ioFile.isDirectory() && bRecursive) { 
	                bReturnValue &= deleteFilesWithGivenExtension(ioDirectory, sFileExtension, bRecursive, bDeleteDirectoryItself);	// Recursive function. 
	            } else {
	            	if (sFileExtension==null || ioFile.getName().endsWith(sFileExtension)) {
	            		bReturnValue &= ioFile.delete();
	            	}
	            } 
	        }
	        if (bDeleteDirectoryItself) {
	        	bReturnValue &= ioDirectory.delete();	// Delete directory itself
	        }
	    } 

	    return bReturnValue; 
	}	
	
	
}