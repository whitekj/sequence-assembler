import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Reads and stores lines from a given FASTA file
 *
 */

public class FastaReader {
	
	/**
	 * Constructs a new FastaReader
	 * @param fileName Name of FASTA file
	 */
	
	public FastaReader(String fileName) {
		this.fileName = fileName;
		fasta = new ArrayList<String>();
	}
	
	/**
	 * @return ArrayList of reads from FASTA file
	 */
	
	public ArrayList<String> getFasta() {
		return fasta;
	}
	
	/**
	 * Reads data from file and stores in fasta array
	 */
	
	public boolean readFile() {
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName));){
			String line;
		    while ((line = reader.readLine()) != null) {
		    	if (line.charAt(0)!='>') {		//Skip description lines
		    		fasta.add(line);
		    	}
		    }
		} catch (StringIndexOutOfBoundsException e) {
			//do nothing
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private String fileName;
	private ArrayList<String> fasta;

}
