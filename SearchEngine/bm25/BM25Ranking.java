import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class BM25Ranking {
	String index_file;
	String rank_file;
	String queries_file;
	int max_result;
	BufferedReader  index_file_reader;
	HashMap<Integer, Integer> metadata = new HashMap<Integer,Integer>();
	
	public BM25Ranking(String index_file, String rank_file, String queries_file, int max_result) {
		super();
		this.index_file = index_file;
		this.rank_file = rank_file;
		this.queries_file = queries_file;
		this.max_result = max_result;
		retreivemetadata();			 //The first line in index file with identifier #0000
	}

	  /****************method to read metadata from index.txt******************************************
		 * Input : None
		 * Returns: void
		 * Description: This function reads the first line which is the meta data
		 * It identifies the line by the identifier #0000, it then parses the line and stores the
		 * metadata in  a hashmap
		
	  /**************************************************************************************************/	
	private void retreivemetadata() {
		try {
			index_file_reader  =  new BufferedReader(new FileReader(index_file));
			String index_entry;
			while( ( index_entry  = index_file_reader.readLine()) != null){
				String[] st = index_entry.split("\\s");						
				if(st[0].equals("#0000")){   //signifies the metadata
					for (int x=1; x<st.length; x++){      //ignore the first token as it is identifier for metadata						    
						String[] docid = st[x].split("=");  //Retrieving each docid and and there size 	
						int docnum = Integer.parseInt(docid[0].replaceAll("[{},]", ""));
						int doclength = Integer.parseInt(docid[1].replaceAll("[{},]", ""));
						metadata.put(docnum, doclength); // write the data to metadata hashma
					}						 
				}
				break;  //don't search further as the metadata has been found
			}
			index_file_reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**************************************************************************************************
		 * Input : None
		 * Returns: void
		 * Description: This function reads the queries file line by line and passes each query 
		 * line to a helper function that computes the accumulates BM25 score
	/**************************************************************************************************/	
	void computeScore(){
		String query;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(queries_file));
			while( (query = br.readLine()) != null){
				computeQueryScore(query);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/****************method to parse each query ******************************************
	 * Input : A single query
	 * Returns: void
	 * Description: This function receives a single query parses it and uses a helper
	 * function to calculate BM25 score.
/**************************************************************************************************/
	private void computeQueryScore(String query) {
		HashMap<String, Integer> query_term_freq = new HashMap<String,Integer>();
		StringTokenizer st = new StringTokenizer(query, " ");
		int query_id = Integer.parseInt(st.nextToken());  //first token in the query line is query id

		while(st.hasMoreTokens()){			  //
			String query_token = st.nextToken();             //remaining elements are the part of the query.			
			if(query_term_freq.containsKey(query_token)) {
				query_term_freq.put(query_token, query_term_freq.get(query_token)+1);  //incrementing the token frequency in query
			}
			else{
				query_term_freq.put(query_token, 1);     //add the token frequency in query 
			}

		}		
		bm25Score(query_term_freq,query_id);      //method call to bm25score with argument query term with frequency and the query id
	}

	
	/****************Computing the bm25 score of each term******************************************
	 * Input : A Hashmap which stores query terms  and there frequency and query Id 
	 * Returns: void
	 * Description: For each query term of a particular query this method reads the inverted index file
	 * search for the term , if the term is found it parses the line and calculates the BM25 score for
	 * all the documents present for that index word and stores the score in a Hashmap.
	 * and calculates the BM25 score .
	 * The BM25 score of each document for given query is accumulated and stored in doc_score Hashmap.
   /**************************************************************************************************/	
	//term at a time evaluation is done
	private void bm25Score(HashMap<String, Integer> query_tokens , int id) {
		HashMap<Integer, Double> doc_score = new HashMap<Integer,Double>(); 
		for (String token : query_tokens.keySet()){
			//fetch the  data of token from inverted index list
			try {
				index_file_reader  =  new BufferedReader(new FileReader(index_file));
				String index_entry;
				int qf = query_tokens.get(token);
				//index_file_reader.mark(0);  //Buffered reader seeking inefficient 
				while( ( index_entry  = index_file_reader.readLine()) != null){
					String[] st = index_entry.split("\\s");						
					if(st[0].equals(token)){
						int n1 = st.length-1; //no of docs in which term occurs
						for (int x=1; x<st.length; x++){      //ignore the first token as it is the index word we are calculating the score							    
							String[] docid = st[x].split("=");  //Retrieving each docid and frequency of words in it 	
							int docnum = Integer.parseInt(docid[0].replaceAll("[{},]", ""));
							int docfreq = Integer.parseInt(docid[1].replaceAll("[{},]", ""));
							calculateTermRank(docnum , docfreq,token,qf,n1,doc_score); //Calculate rank
						}
						break;	  //don't search further as the index has been found
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		writeRankFile(doc_score,id);            //Writing the document score to results fiel
	}

	/***********************************************************************************************
	 * Input : A Hashmap which stores BM25 score for the documents and query Id
	 * Returns: void
	 * Description: This function sorts the ranks of documents in Descending order.
	 * It filters the top 100 documents and writes that to an external file
   /**************************************************************************************************/	
	private void writeRankFile(HashMap<Integer, Double> doc_score, int id) {
		int max = 1;
		List<Map.Entry<Integer,Double>> ranklist = new  LinkedList<Map.Entry<Integer,Double>>(doc_score.entrySet());
		Collections.sort(ranklist, new Comparator<Map.Entry<Integer, Double>>(){
			public int compare(Map.Entry<Integer,Double> o1, Map.Entry<Integer,Double> o2){
				return o2.getValue().compareTo((double)o1.getValue() );
			}
		});

		Map<Integer, Double> sortedRank = new LinkedHashMap<Integer,Double>();
		for(Map.Entry<Integer, Double> rank : ranklist){
			sortedRank.put(rank.getKey(), rank.getValue());			
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(rank_file,true));
			Iterator itr = sortedRank.entrySet().iterator();
			while(itr.hasNext()&& (max<=max_result)){
				Map.Entry<Integer, Double> val = (Map.Entry)itr.next();
				//Uncomment the below line to see output on standard output
				//System.out.println("QueryID:"+id+ " Q0 "+" DocID "+val.getKey()+"  Rank "+max+" BM25Score  "+val.getValue()+"\n");
				bw.write("QueryID:"+id+ " Q0"+" DocID:"+val.getKey()+" Rank:"+max+" BM25Score "+val.getValue()+" SYS\n");
				max++;
			}
			bw.flush();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/********************************************************************************
	 * BM25 Algorithm
	*******************************************************************************/
	private void calculateTermRank(int docnum, int docfreq ,String token,int qf,int n1,HashMap<Integer, Double> doc_score) {
		//Given   k1=1.2, b=0.75, k2=100
		//Calculate K
		double k1 = 1.2;
		double b = 0.75;
		double k2 = 100;
		int sum = 0;
		int N = metadata.size();

		double dl =  metadata.get(docnum);

		for(Integer avg : metadata.keySet()){
			sum  = sum + metadata.get(avg); 
		}
		double avdl = (double)sum/(double)N;

		double K = k1*((1-b)+(b* (dl/avdl)));

		double v = Math.log((double)1/((n1+0.5)/(N-n1+0.5)));
		v = v*(((1.2+1)*docfreq)/(K+docfreq))*((k2+1)*qf/(k2+qf));

		if(doc_score.containsKey(docnum)){
			doc_score.put(docnum, doc_score.get(docnum)+v);
		}
		else{
			doc_score.put(docnum,v);
		}
	}


}
