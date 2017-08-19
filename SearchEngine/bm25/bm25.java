
public class bm25 {

	public static void main(String[] args) {
		String index = args[0];   //index file
		String queries  = args[1];  //queries file
		int max_doc = Integer.parseInt(args[2]);   //max doc for each query
		String results  = args[3];         //output file
		BM25Ranking rank = new BM25Ranking(index,results, queries, max_doc);
		rank.computeScore();
	}
}
