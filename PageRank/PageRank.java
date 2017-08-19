import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class PageRank {
	
	
	Map<String, Set<String>> outlinks = new HashMap<String, Set<String>>();
	Map<String, Integer> outlinks_count = new HashMap<String, Integer>();
	Map<String, Set<String>> inlinks =  new HashMap<String, Set<String>>();
	Map<String, Integer> inlinks_count = new HashMap<String, Integer>();
	ArrayList<Double> ppx_list = new ArrayList<>();
	Set<String> sink ;
	
	Map<String, Double> PageRank = new HashMap<String, Double>();
	Map<String, Double> init_PageRank = new HashMap<String, Double>();
	Map<String, Double> new_PageRank = new HashMap<String, Double>();
	int outlink_count;
	int inlink_count;
	double d = 0.85;
	int total_nodes=0;
	int source =0;
	

	public static void main(String[] args) throws IOException{
	
		
		int iteration =1;
		
		/* If Calculating Graph Ranks uncomment the below line an pass 
		 * the no of iterations through command line
		 * Also, Uncomment the while loop in calculate calculatePR method*/
		
	//	iteration = Integer.parseInt(args[0]);
		
		PageRank r = new PageRank();
		r.parseline();
		r.getSinkSet();
		r.calculatePR(iteration);
		r.readWriteFile();
		
		/*Below commented code is used for finding the total no of nodes whose
		 * values are less than there initial page rank values*/
		
		/*	int count=0;
		for(String s:PageRank.keySet()){
		    if(PageRank.get(s) < init_PageRank.get(s))
		    	count++;
		}
		
		System.out.println("Value less than initial rank"+count); */
	}
	
/*******************************************************************************************/	
	/*parses each line and create inlinks and outlinks map from the given dataset*/
	
	void parseline(){
		try {
		String currentLine;
		BufferedReader freader = new BufferedReader(new FileReader("C:\\wt2g_inlinks.txt"));  //default to dataset
	//	BufferedReader freader = new BufferedReader(new FileReader("C:\\inlink.txt"));        // Use this for Graph 
		Set<String> ar ;
			while((currentLine= freader.readLine()) != null){       
				Set<String> in_data = new HashSet<>();
				StringTokenizer st = new StringTokenizer(currentLine," ");
				String node = st.nextToken();
				if(!inlinks.containsKey(node)){
					inlinks.put(node, null);                               // All the nodes are stored in inlinks Map
				}
					while(st.hasMoreTokens()){
						String edge = st.nextToken();
					    if(!inlinks.containsKey(edge)){
					    	inlinks.put(edge, null);
					       } 
					    if(inlinks.get(node)!=null){
					    	Set B = inlinks.get(node);
					    	B.add(edge);
					    	inlinks.put(node, B);
					       }
					   else{
					       in_data.add(edge);
					       inlinks.put(node, in_data);
					       }					     
					   if(!(outlinks.containsKey(edge))){                              //Creating the outlinks Map  
						   Set<String > nd = new HashSet<>();
						   nd.add(node);
					       outlinks.put(edge, nd);
					       }	
					   else{
						   ar = outlinks.get(edge);
						   ar.add(node);
						   outlinks.put(edge, ar);
					       }
					}
 				   
			}
		} catch (IOException e) {
			e.printStackTrace();
		    }
		
		System.out.println("Total Nodes : " +inlinks.size());
		
        for(String str : inlinks.keySet()){
        	Set s1 = inlinks.get(str);
        	if(s1 != null)
        	inlinks_count.put(str, s1.size());
        	else
        		source++;
        }
        System.out.println("Total Source " +source);
        
        for(String str : outlinks.keySet()){
        	Set s1 = outlinks.get(str);
        	outlinks_count.put(str, s1.size());
        }
	}
	
/********************************************************************************************************/
	/*This method calculates the sink nodes*/
	void getSinkSet(){
		sink = new HashSet<>(inlinks.keySet());
		for(String node : outlinks.keySet()){
			if(!(sink.add(node))){
				sink.remove(node);
			}
		}
		
		System.out.println("Sink Size : "+sink.size());
	  }
	
	
/*****************************************************************************************************/	
  /*Algorithm to calculate the page rank */
	
	void calculatePR( int iteration){
		
		total_nodes = inlinks.size();
		for(String page : inlinks.keySet()){
			PageRank.put(page, (1/(double)(total_nodes)));
			init_PageRank.put(page, (1/(double)(total_nodes)));
		}
           
      double ppx = 0.0;
      boolean cond = true;
      int itr =1;
      // while(iteration >0){                //Used for Graph page rank calculation; c
       while(cond){                           
    	System.out.println("Iteration"+itr);  
        double sinkPR = 0;
        for(String s: sink){
        	sinkPR = sinkPR + (PageRank.get(s));  //calculate total sink PR
        }
        
        double entropy = 0.0;
        
        for(String s: PageRank.keySet()){
        	double newPR;
        	newPR = (1.0 -d)/total_nodes;
        	newPR = newPR + (d*sinkPR)/total_nodes;
        	Set<String> in = inlinks.get(s);    //pages pointing to P
        	if(in != null){
        		for(String s1 : in){
        			newPR = newPR + d* (PageRank.get(s1)/outlinks_count.get(s1));     		     
          		}
        	}  
       		new_PageRank.put(s, newPR);
       		entropy = entropy + (newPR*(Math.log(newPR)/Math.log(2)));   //entroPy calculation
        	}
        
        PageRank.putAll(new_PageRank);
        ppx = Math.pow(2,-1*entropy);
        System.out.println("perplexity  :  "+ ppx);
        ppx_list.add(ppx);
        if(ppx_list.size()>5) 
        	cond= !check(4);
       
        itr++;
        	
        //	 iteration --;                  //Used for page Rank Calculation of Graph
        } 
	}

	//This method check Covergence by analyzing the 4 consecutive iteration perplexities .
	boolean check(int x){
		int size = ppx_list.size();
		if (x == 0){
			return true;
		}
		else{
			return (ppx_list.get(size-x)-ppx_list.get(size-(x+1)) < 1) && check(x-1);
		}		
	}

/*********************************************************************************************************/	
// Helper function to sort map according to values ; Used for pagerank sorting 
	
	Map<String, Double> sortByValues(Map<String, Double> map_to_sort){
	List<Map.Entry<String,Double>> ranklist = new  LinkedList<Map.Entry<String,Double>>(map_to_sort.entrySet());
	Collections.sort(ranklist, new Comparator<Map.Entry<String, Double>>(){
		public int compare(Map.Entry<String,Double> o1, Map.Entry<String,Double> o2){
			return o2.getValue().compareTo((double)o1.getValue() );
		}
	});
	
	Map<String, Double> sortedRank = new LinkedHashMap<String,Double>();
	for(Map.Entry<String, Double> rank : ranklist){
		sortedRank.put(rank.getKey(), rank.getValue());			
	}
	
	return sortedRank;
	}
	

	/*Used for Sorting  Map<String, Integer> ; Inlinks count use this format*/
	Map<String, Integer> sortByinlinks(Map<String, Integer> inlinks_sort){
	List<Map.Entry<String,Integer>> ranklist = new  LinkedList<Map.Entry<String,Integer>>(inlinks_sort.entrySet());
	Collections.sort(ranklist, new Comparator<Map.Entry<String, Integer>>(){
		public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2){
			return o2.getValue().compareTo(o1.getValue() );
		}
	});
	
	Map<String, Integer> sortedRank = new LinkedHashMap<String,Integer>();
	for(Map.Entry<String, Integer> rank : ranklist){
		sortedRank.put(rank.getKey(), rank.getValue());			
	}
	
	return sortedRank;
	}
	
	
	
	
/********************Below Function write the  output to an external file defined in C:/output.txt ********************/	
	void readWriteFile() throws IOException{
				
		// Sorting before writing it to the file
	    Map<String, Double> sortedRank = sortByValues(PageRank);    
		
        // below code is used for finding the inlinks count, Comment the probablity sum in  below iterator if uncommenting this line
		//Map<String, Integer> sortedRank = sortByinlinks(inlinks_count);
		

		//Writing it to the file
		File file = new File("C:/output.txt");
		BufferedWriter output = new BufferedWriter(new FileWriter(file,true));
		Iterator  iterator = sortedRank.entrySet().iterator();
		double s = 0.0;
			while(iterator.hasNext()){
				Map.Entry val = (Map.Entry)iterator.next();
				output.write(val.getKey() +"\t"+ val.getValue()+ "\n");				
				s= s+ (double)val.getValue();        //Adding the Probablity ; Comment this line if using Inlink calculation
			}
			System.out.println("Probablity Sum : "+s);   //After convergence should be 1; ; Comment this line if using Inlink calculation
			output.flush();
			output.close();
	}
}
