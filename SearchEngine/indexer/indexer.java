
public class indexer {

	public static void main(String[] args) {
		String corpus = args[0];  //Command line arguement for corpus
		String index  = args[1];   //Command Line argument where inverted file will be stored
		Tokenizer tok = new Tokenizer(corpus,index); 
		tok.createInvertedIndex();  
	}
}
