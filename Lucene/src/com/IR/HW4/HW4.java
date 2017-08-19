package com.IR.HW4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;

/**
 * To create Apache Lucene index in a folder and add files into this index based
 * on the input of the user.
 */
public class HW4 {
	private static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
	private static Analyzer sAnalyzer = new SimpleAnalyzer(Version.LUCENE_47);
	static HashMap<String, Integer> hm = new HashMap<>();
	static int total_doc =0;  

	private IndexWriter writer;
	private ArrayList<File> queue = new ArrayList<File>();

	public static void main(String[] args) throws IOException {
		System.out
		.println("Enter the FULL path where the index will be created: (e.g. /Usr/index or c:\\temp\\index)");

		String indexLocation = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();

		HW4 indexer = null;
		try {
			indexLocation = s;
			indexer = new HW4(s);
		} catch (Exception ex) {
			System.out.println("Cannot create index..." + ex.getMessage());
			System.exit(-1);
		}

		// ===================================================
		// read input from user until he enters q for quit
		// ===================================================
		while (!s.equalsIgnoreCase("q")) {
			try {
				System.out
				.println("Enter the FULL path to add into the index (q=quit): (e.g. /home/mydir/docs or c:\\Users\\mydir\\docs)");
				System.out
				.println("[Acceptable file types: .xml, .html, .html, .txt]");
				s = br.readLine();
				if (s.equalsIgnoreCase("q")) {
					break;
				}

				// try to add file into the index
				indexer.indexFileOrDirectory(s);
			} catch (Exception e) {
				System.out.println("Error indexing " + s + " : "
						+ e.getMessage());
			}
		}

		// ===================================================
		// after adding, we always have to call the
		// closeIndex, otherwise the index is not created
		// ===================================================
		indexer.closeIndex();

		// =========================================================
		// Now search
		// =========================================================
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
				indexLocation)));
		IndexSearcher searcher = new IndexSearcher(reader);
		//TopScoreDocCollector collector = TopScoreDocCollector.create(3, true);

		s = "";
		while (!s.equalsIgnoreCase("q")) {
			try {
				System.out.println("Enter the search query (q=quit):");
				s = br.readLine();
				if (s.equalsIgnoreCase("q")) {
					break;
				}
				TopScoreDocCollector collector = TopScoreDocCollector.create(100, true); //Use total_doc in place of 100 to retreive all results
				Query q = new QueryParser(Version.LUCENE_47, "contents",
						sAnalyzer).parse(s);
				searcher.search(q, collector);
				ScoreDoc[] hits = collector.topDocs().scoreDocs;
				// 4. display results
				System.out.println("Found " + hits.length + " hits.");
				for (int i = 0; i < hits.length; ++i) {
					int docId = hits[i].doc;

					Document d = searcher.doc(docId);
					String filename = d.get("filename");

					//snippet generation of 200 charachters
					String snippet =  d.get("snippet");  
					if(snippet != null && snippet.length() > 200) {
						snippet =  snippet.substring(0, 200) ;
					} 
					// Uncomment it to see the results on standard output screen
					/* System.out.println((i + 1) + ". " + d.get("path")
					+ " score=" + hits[i].score);
					System.out.println("snippet:"+ snippet);  */

					//write the score alongwith snippet of top 100 results of a query
					writeScoreToFile(filename,hits[i].score,snippet,s);  

				}
				// 5. term stats --> watch out for which "version" of the term
				// must be checked here instead!
				Term termInstance = new Term("contents", s);
				long termFreq = reader.totalTermFreq(termInstance);
				long docCount = reader.docFreq(termInstance);
				/*	System.out.println(s + " Term Frequency " + termFreq
						+ " - Document Frequency " + docCount); */

			} catch (Exception e) {
				System.out.println("Error searching " + s + " : "
						+ e.getMessage());
				e.printStackTrace();
				break;
			}

		}
		reader.close();
		drawGraph(indexLocation);   //Draw Zifs grah.
	}

	/**
	 * Write the score and snippet(200 charachters) of top 100 documents
	 * retrieved for each query
	 * to a file C:\\(...query text...).txt
	 * */

	private static void writeScoreToFile(String id, float score, String snippet,String q) {
		// TODO Auto-generated method stub
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\"+q+".txt",true));
			bw.write("ID: "+id +"\tscore: "+score +"\n Desc: "+snippet +"\n\n");			
			bw.flush();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Constructor
	 * 
	 * @param indexDir
	 *            the name of the folder in which the index should be created
	 * @throws java.io.IOException
	 *             when exception creating index.
	 */
	HW4(String indexDir) throws IOException {

		FSDirectory dir = FSDirectory.open(new File(indexDir));

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,
				sAnalyzer);

		writer = new IndexWriter(dir, config);
	}

	/**
	 * Indexes a file or directory
	 * 
	 * @param fileName
	 *            the name of a text file or a folder we wish to add to the
	 *            index
	 * @throws java.io.IOException
	 *             when exception
	 */
	public void indexFileOrDirectory(String fileName) throws IOException {
		// ===================================================
		// gets the list of files in a folder (if user has submitted
		// the name of a folder) or gets a single file name (is user
		// has submitted only the file name)
		// ===================================================
		addFiles(new File(fileName));

		int originalNumDocs = writer.numDocs();
		for (File f : queue) {
			try {
				Document doc = new Document();

				// ===================================================
				// add contents of file
				// ===================================================

				doc.add(new StringField("path", f.getPath(), Field.Store.YES));
				doc.add(new StringField("filename", f.getName(),
						Field.Store.YES));


				// Extracting the file contents in a string	
				String contents = new String(Files.readAllBytes(Paths.get(f.getPath())), StandardCharsets.UTF_8);

				// removing the html tags	
				String a = Jsoup.parse(contents).text();

				// Creating the token stream ; This will parse the text and tokenize it	
				TokenStream tokenStream1 = sAnalyzer.tokenStream("contents",a);

				//adding it to documets which will be written to indexer
				doc.add(new TextField("contents", tokenStream1));

				//adding a snippet field for document ,used for snippet generation
				doc.add(new StringField("snippet", a, Field.Store.YES));
				writer.addDocument(doc);

				//close the stream;
				tokenStream1.end();
				tokenStream1.close();

				//This code can be used for word frequency calculation while processing.
				//creating a hashmap which will store the term and their frequency for creating zipf plot
/*				TokenStream tokenStream = sAnalyzer.tokenStream("contents",a); 
				CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
				try {
					tokenStream.reset();		      
					// retreive all tokens until stream is exhausted  
					while (tokenStream.incrementToken()) {
						String term = (charTermAttribute.toString());
						//store the term and frequency to draw zipfian curve
						if(!hm.containsKey(term)){
							hm.put(term, 1);
						}
						else{
							hm.put(term, hm.get(term)+1);
						}
					}
					tokenStream.end();
				} finally {
					tokenStream.close();
				}*/

				System.out.println("Added: " + f);
			} catch (Exception e) {
				System.out.println("Could not add: " + f);
				e.printStackTrace();
			} 
		}

		int newNumDocs = writer.numDocs();
		System.out.println("");
		System.out.println("************************");
		System.out
		.println((newNumDocs - originalNumDocs) + " documents added.");
		total_doc = (newNumDocs - originalNumDocs);
		System.out.println("************************");

		queue.clear();
	}

	private void addFiles(File file) {

		if (!file.exists()) {
			System.out.println(file + " does not exist.");
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(f);
			}
		} else {
			String filename = file.getName().toLowerCase();
			// ===================================================
			// Only index text files
			// ===================================================
			if (filename.endsWith(".htm") || filename.endsWith(".html")
					|| filename.endsWith(".xml") || filename.endsWith(".txt")) {
				queue.add(file);
			} else {
				System.out.println("Skipped " + filename);
			}
		}
	}

	/**
	 * Close the index.
	 * 
	 * @throws java.io.IOException
	 *             when exception closing
	 */
	public void closeIndex() throws IOException {
		writer.close();
	}


	/**
	 * Drawing zipfs plot
	 * A hashmap with sorted frequency is created
	 * It is passed to the constructor of chart
	 * which is rendered using Jchart
	 * @throws IOException 
	 * 
	 * **/
	
	private static void drawGraph(String indexLocation) throws IOException {
		//Retreive all index words from the indexer 
		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File( indexLocation)));
		Fields fields = MultiFields.getFields(reader);
		Terms terms = fields.terms("contents");
		TermsEnum iterator = terms.iterator(null);
		BytesRef bytesRef = null;
		while((bytesRef = iterator.next())!=  null){
			String term = new String(bytesRef.bytes, bytesRef.offset,bytesRef.length);
			Term term_ins = new Term("contents",term);
			hm.put(term,(int)reader.totalTermFreq(term_ins));
		}
		
		reader.close();

		//Sort the hashmap		
		
		List<Map.Entry<String,Integer>> ranklist = new  LinkedList<Map.Entry<String,Integer>>(hm.entrySet());
		Collections.sort(ranklist, new Comparator<Map.Entry<String, Integer>>(){
			public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2){
				return o2.getValue().compareTo (o1.getValue());
			}
		});

		Map<String, Integer> sortedfreq = new LinkedHashMap<String,Integer>();
		for(Map.Entry<String, Integer> rank : ranklist){
			sortedfreq.put(rank.getKey(), rank.getValue());			
		}
    
		/* Writing the sorted frequency to a file */ 
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\rank_freq.txt"));
			Iterator itr = sortedfreq.entrySet().iterator();
			while(itr.hasNext()){
				Map.Entry<String, Integer> val = (Map.Entry)itr.next();
				bw.write("Word : "+val.getKey() + "\t\t\t"+ "Frequency : "+val.getValue() +"\n");
			}
			bw.flush();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		//Assign the rank 
		Map<Integer, Integer> ranked_sortedfreq = new LinkedHashMap<Integer,Integer>();
		int i = 1 ;
		for(Map.Entry<String, Integer> rank : ranklist){
			ranked_sortedfreq.put((Integer)i, rank.getValue());	
			i++;
		}

		Chart c = new Chart(ranked_sortedfreq);  //draw the graph
	}
}
