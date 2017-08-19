import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class Tokenizer {

	/***********Data Structure for inverted Index******************************
	 * HashMap<token , HashMap<documentId,Termfreq>>
	 * this data structure is used for storing the inverted index.
	 * token : signifies the index word
	 * document Id : the Document Id in which term occurs
	 * Termfreq    : the term frequency of the token word in document Id
	 * *************************************************************************/
	HashMap<String , HashMap<Integer,Integer>> Inverted_Index = new HashMap<String , HashMap<Integer,Integer>>();
	HashMap<Integer, Integer> Sum_Tokens = new HashMap<>();  //Map to store document id and corresponding document length

	String corpus;      //tccorus file
	String indexfile;   //file in which inverted index will be stored
	public Tokenizer(String corpus , String indexfile) {
		super();
		this.corpus = corpus;
		this.indexfile =  indexfile;
	}

	/*******Reading the tokens from the file and adding in the inverted index data structure********
	 * Input : None
	 * Returns: void
	 * Description: This function reads tccorpus line by line and identifies whether a line is
	 * a new Document Id or the contents of the Document. If the line signifies the content
	 * this function calls a helper function to add the tokens in the inverted list

   **************************************************************************************************/
	void createInvertedIndex(){
		String line;
		String current_doc = null;
		try {
			BufferedReader freader = new BufferedReader(new FileReader(corpus)); //tccorpus.txt
			while((line = freader.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line," ");								
				//check whether the line is document or contents for the document.
				if( isdocument(st.nextToken())){ // true if it signifies a document
					current_doc = st.nextToken();
					Sum_Tokens.put(Integer.parseInt(current_doc), 0);
				}
				else{  //if it signifies the contents for the document
					// fill the already created inverted list for this particular document.
					add_tokens_to_inv_list(Integer.parseInt(current_doc),line);
				}
			}
			freader.close();
			writefile(indexfile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//Method to check whether the given string signifies a document number or content of a file.
	boolean isdocument (String line){
		if(line.startsWith("#")){
			return true;
		}
		return false;
	}

	/******** Method:  void add_tokens_to_inv_list(int current_doc , String line)************************
	 * Input : A document Id retrieved from tccorpus  and a line containing the the contents to be parsed
	 * Returns: void
	 * Description: This function populates the inverted index data structure by reading each line 
	 * which was passed as input to it. It divides the entire line into tokens , ignore the numeric tokens
	 * and stores the unique tokens as key in Inverted_Index Hashmap. 
	 * If token is not present in the map it stores the token as key and a map of
	 *  Document Id and token frequency as values.
	 * If the key is already present it retrieves the corresponding values and increments the 
	 * frequency count if DocumentId is present else add the Document Id in map  
	 * ******************************************************************************************************/

	void add_tokens_to_inv_list(int current_doc , String line){
		StringTokenizer st = new StringTokenizer(line," ");
		String	token = null;
		while(st.hasMoreTokens()){
			token = st.nextToken();
			if(token.matches("[0-9]+")) continue;  //ignoring the numeric values

			Sum_Tokens.put(current_doc, Sum_Tokens.get(current_doc)+1); // populating the Sum token map 

			//If token already resent
			if(Inverted_Index.containsKey(token)){
				//Retreive the ma already resent.
				HashMap<Integer, Integer>	hm = Inverted_Index.get(token);
				if((hm.get(current_doc)) != null){   
					//incrementing the token count if in the same document
					hm.put(current_doc, hm.get(current_doc) +1);
					Inverted_Index.put(token, hm);
				}
				else{
					//if the token is already in the list but it is found in new document
					hm.put(current_doc, 1);
					Inverted_Index.put(token, hm);
				}
			}
			else{
				HashMap<Integer, Integer> hmi = new HashMap<>();
				hmi.put(current_doc, 1);
				Inverted_Index.put(token, hmi);
			}
		}
	}

  /****************method to write the inverted index in index.txt***********************************
	 * Input : A file where inverted index will be stored
	 * Returns: void
	 * Description: This function writes the inverted index file.
	 * The first line of the inverted index is metadata which is assigned a unique token  #0000
	 * to identify it.
	 * The meta data is contains information about each documentId and document length
	 * The remaining lines of the file contains the inverted index 	

  /**************************************************************************************************/	
	void writefile(String file){
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(file,true));
			output.write("#0000 "+Sum_Tokens+"\n");  //Storing the metadata in the inverted index. #0000 special token to identify it
			Iterator itr = Inverted_Index.entrySet().iterator();
			while(itr.hasNext()){
				Map.Entry item = (Map.Entry)itr.next();
				output.write((String)item.getKey() +" "+(HashMap)item.getValue() +"\n");
			}
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
