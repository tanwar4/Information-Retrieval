package com.IR.HW5;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Evaluate {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashMap<String, Double> mean = new HashMap<>(); 
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = "";
		
		while (!s.equalsIgnoreCase("q")) {
			try {
				System.out.println("Enter the search query (q=quit):");
				s = br.readLine();
				if (s.equalsIgnoreCase("q")) {
					break;
				}
				System.out.println("Enter the CACM  query ID");
				String id = br.readLine();
				/*****************************************************
				 * Read relevanvce file
				 * ***************************************************/
				HashMap<Integer, Double> idcg = new HashMap<>();
				HashMap<String, Integer> cacm_data = new HashMap<>();
				evaluaterelevance(id,cacm_data,idcg);
				
				int totalRelvDoc = cacm_data.size();
				double map = 0.0;
				
				/********************************************************
				 * Read the outut rank file as (it is with same name as the query 
				 *********************************************************/
				 //calculate precision and recall
				//the rank file of previous assignment is stored in query folder
                 String name= "C:\\query\\"+(s.trim())+".txt";
				BufferedReader b  = new BufferedReader(new FileReader(name));
				String str;
				float precision = 0;
				float recall = 0;
				int tot_rel_ret = 0;
				int docCount=1;
				double P =0.0;
				double dcg = 0.0;
				while( ( str = b.readLine() )!= null){
					 String[] strArr = str.split(" "); 
					 String filename = strArr[1];

                     //parse the input ranking file
					 float score = Float.parseFloat(strArr[3]);
	                    String fname = filename.replaceAll(".html", "");
	                    fname = fname.replaceAll("CACM-", "");
	                    Integer n = Integer.parseInt(fname);
	                    fname = n.toString();

	                    int rel=0;
	                    //check whether the document is relevant or not
						if(cacm_data.containsKey(fname)){
							tot_rel_ret++;
							rel = cacm_data.get(fname);
						}
						
						//calculate recall and precision
						recall = (float)tot_rel_ret/(float)totalRelvDoc;
						precision =  (float)tot_rel_ret/(float)docCount;
						
						//calculate DCG
						if(docCount == 1){
							   dcg = dcg+ rel;
							}
							else{
							   dcg = (float) (dcg + (rel /(Math.log(docCount)/Math.log(2))));	
							}
						
						//Calculate NDCG
						double ndcg = dcg/idcg.get(docCount);
						
						
						//Writing the output to the file
						writeScoreToFile(filename,score,s,recall,precision,docCount,ndcg,rel);
						
						//P@ 20
						if(docCount == 20){
							P = precision;
						}
						
						//sum of all precision of individual query
						if(cacm_data.containsKey(fname)){
							map = map + precision;	
						}
						docCount++;
				}
	
				//mean average precision of single query
				mean.put(id, map/cacm_data.size());
				System.out.println("P @ 20 :"+P);
				
			 }
			catch(Exception e){
					e.printStackTrace();
			}
		}
		
		//calulate mean average precision of all the query
		double tot = 0.0;
		for(Double d:mean.values())
			tot =tot+d;
		System.out.println("Mean Average precision"+ tot/mean.size());

    }

	/***************************************************************************************
	 * Method to write the output to a file ; which is stored in C:\\ (name of query).txt
	 * 
	 * @param filename 
	 * @param score
	 * @param q
	 * @param recall
	 * @param precision
	 * @param docCount
	 * @param ndcg
	 * @param rel
	 ******************************************************************************************/
	private static void writeScoreToFile(String filename, float score, String q, float recall, float precision,
			int docCount, double ndcg, int rel) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\"+q+".txt",true));
			bw.write("Rank:"+docCount  +"  Doc ID:"+filename +" Doc Score:"+score +"  Relevance Level:"+rel
					+"  Recall:"+recall+ "  precision:"+precision+"  NDCG:"+ndcg +"\n\n");			
			bw.flush();
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**********************************************************************************
	 * This method reads the relevence judegement file stored in C:\\rel.txt
	 * It performs the following two task:
	 * 1. It stores the relevance judgement into a local hashmap which is then 
	 * used for further processing.
	 * 2. It calculates the ideal NDCG values for the top 100 rank.Since it is 
	 * binary relevant no need to sort the document in descending order, moving all the 
	 * relevant document to top of the ranking will serve the purpose.
	 * 
	 * *******************************************************************************/
	private static void evaluaterelevance(String id, HashMap<String, Integer> cacm_data, HashMap<Integer, Double> idcg) {
			try {
				BufferedReader br = new BufferedReader(new FileReader("C:\\rel.txt"));
				String s ;
				while( (s= br.readLine()) != null){
					String[] str =s.split(" ");
					if(str[0].equals(id)){       //doc id matches
						String docID = str[2];
						docID= docID.replaceAll("CACM-", "");
						cacm_data.put(docID,Integer.parseInt(str[3]));
					}
				}
				
				//calculating the idcg values and storing it in a map
				idcg.put(1, (double)1);
				for(int i=2; i<=100 ; i++){
					if(i>= 0 && i<= cacm_data.size()){
						double val = (double) (1 /(Math.log(i)/Math.log(2)));
						idcg.put(i, val+(idcg.get(i-1)));
					}
					else{
						idcg.put(i, 0.0+ (idcg.get(i-1)));
					}
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}

}
