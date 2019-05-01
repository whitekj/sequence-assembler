import java.util.ArrayList;
import java.io.PrintWriter;

/**
 * Program that takes a FASTA file as input, and converts the reads into a contig using a de Bruijn graph
 * @author whitekj
 */

public class DeBruijnBuilder {
	
	public static final String DEFAULT_OUT_FILE = "output.fasta";
	public static final int DEFAULT_KMER_LENGTH = 50;
	
	/**
	 * Runs the program
	 * @param args Arg1 = file name, Arg2 = k-mer length
	 */

	public static void main(String[] args) {
		String inFile = "";
		String outFile = "";
		int kLength;
		//Get input file
		if (args.length < 1) {
			System.out.println("Usage: DeBruijnBuilder [inputFile] [outputfile] [kmerSize]");
			System.exit(0);
		}
		else {
			inFile = args[0];
		}
		//Get output file
		if (args.length < 2) {
			outFile = DEFAULT_OUT_FILE;
		}
		else {
			outFile = args[1];
		}
		//Get k-mer length
		//If no k-mer length given, use default
		if (args.length < 3) {
			System.out.println("Using default k-mer length: "+DEFAULT_KMER_LENGTH);
			kLength = DEFAULT_KMER_LENGTH;
		}
		else {
			try {
				kLength = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				System.out.println("Using default k-mer length: "+DEFAULT_KMER_LENGTH);
				kLength = DEFAULT_KMER_LENGTH;
			}
		}
		//Read fasta file
		System.out.println("Reading input file...");
		FastaReader fastaReader = new FastaReader(inFile);
		if (!fastaReader.readFile()) {
			System.out.println("Error reading input file.");
			System.exit(0);
		}
		ArrayList<String> fasta = fastaReader.getFasta();
		//Break reads into k-mers
		KmerList kmerList = new KmerList(fasta, kLength);
		ArrayList<String> kmers = kmerList.makeKmers();
		//Make graph
		System.out.println("Building de Bruijn graph...");
		Graph graph = new Graph(kmers);
		//Solve graph
		System.out.println("Generating contigs...");
		ArrayList<String> path = graph.findPath();
		if(path.size()==0) {
			System.out.println("ERROR: Could not generate any contigs");
		}
		//Write contigs to file
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(outFile, "UTF-8");
			for(int i=0;i<path.size();i++) {
				writer.println(">Contig "+(i+1)+"\n"+path.get(i));
			}
		} catch (Exception e) {
			System.out.println("Error writing to file.");
		} finally {
			writer.close();
		}
		System.out.println("Contigs written to: "+outFile);
	}

}