import java.util.ArrayList;

/**
 * Divides an ArrayList of strings into k-mers of given length
 */

public class KmerList {
	
	/**
	 * Constructs a new KmerList
	 * @param readArray Array of reads
	 * @param length K-mer length
	 */
	
	public KmerList(ArrayList<String> readArray, int length) {
		this.kmerArray = new ArrayList<String>();
		this.readArray = readArray;
		this.length = length;
	}
	
	/**
	 * Creates and returns ArrayList of k-mers 
	 * @return ArrayList of kmers
	 */
	
	public ArrayList<String> makeKmers() {
		for (String read : readArray) {
			int beginIndex = 0;
			while (!read.isEmpty()) {
				int endIndex = beginIndex + length;
				//Check if end of read
				if (read.substring(beginIndex).length() < length) {
					break;
				}
				kmerArray.add(read.substring(beginIndex, endIndex));
				beginIndex++;
			}
		}
		return kmerArray;
	}
	
	private ArrayList<String> kmerArray;
	private ArrayList<String> readArray;
	private int length;
	
}
