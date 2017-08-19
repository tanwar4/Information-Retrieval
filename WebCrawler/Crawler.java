import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

	List<String> retrieved_URL_list = new ArrayList<String>();
	static int total_pages_crawled;	
	Pattern  seed_pattern = Pattern.compile("https://en.wikipedia.org/wiki/");  //links pattern should start with it.
	Pattern pattern = Pattern.compile(".*(edit|Main_Page|#|:).*");				//Do not search for links containing these terms

	public static void main(String[] args) throws Exception{
		long startTime = System.currentTimeMillis();
		Map<String, Integer> link_url = new ConcurrentHashMap<String,Integer>(); //Frontier which holds the url to be crawled
		String seed_link = args[0];			
		String keyphrase= null;
		try{
			keyphrase = args[1];
		}
		catch(ArrayIndexOutOfBoundsException e){
			keyphrase = null;
		}
		link_url.put(seed_link, 0);						//adding the seed to Frontier
		Crawler crawl = new Crawler();
		if(keyphrase != null){
			crawl.docparser(link_url,seed_link,keyphrase);  //crawling the seed page,which gives initial set of links
		}
		else{
			crawl.docparser(link_url,seed_link);
		}
		link_url.remove(seed_link);						 
		crawl.crawlLinks(link_url,keyphrase);			//Once initial list retrieved crawl through it and add other links to Frontier 
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total Time taken:"+totalTime);
		System.out.println("Total pages Crawled:"+total_pages_crawled);
		
	}

	/* Function : int docparser(Map<String,Integer> list, String resource, String keyphrase)
	 * Description:This function parse pages and stores the unique links retrieved from those pages in the Map.  
	 * INPUT: 
	 * list  : A Map which stores the url links to be crawled as keys and depths at which pages were discovered. 
	 * resource   : The URL to be parsed 
	 * keyphrase  : keyphrase to be searched
	 * OUTPUT: int value for break condition.
	 * USAGE: Used when a keyphrase is used.
	 * **********/
	int docparser(Map<String,Integer> list, String resource, String keyphrase) throws InterruptedException{
		Document doc;
		int depth;
		if( list.get(resource) == null){
			depth = 0;
		}
		else depth = list.get(resource);

		try {

			String url = resource;
			//TimeUnit.SECONDS.sleep(1);
			doc = Jsoup.connect(url).get();		//Connection made
			total_pages_crawled++;

			String html_body = doc.body().text();	
			Matcher key_phrase_matcher = Pattern.compile(keyphrase,Pattern.CASE_INSENSITIVE).matcher(html_body);//Matches Keyphrase

			if(key_phrase_matcher.find()){			
				retrieved_URL_list.add(url);
				//System.out.println(url+"  "+":"+depth+"\n");  //can be used to check progress in console.
				if(retrieved_URL_list.size()== 1000) return 1;
				// get all links in the page being parsed
				Elements links = doc.select("a[href]");
				for (Element link : links) {
					// get the value from href attribute
					Matcher matcher = pattern.matcher(link.attr("href"));					
					if(!matcher.find()){				
						Matcher seed_matcher = seed_pattern.matcher(link.absUrl("href"));
						if(seed_matcher.find()){
							if(!list.containsKey(link.absUrl("href").toString())&&((depth+1)<5)){
								list.put(link.absUrl("href").toString(),depth+1);  //Map contains links to be crawled.
							}
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Connectionn ERROR!!!!!!!!!Could not connect to URL:"+resource);
			//e.printStackTrace();
		}
		return 0;
	}

	/* Function : int docparser(Map<String,Integer> list, String resource)
	 * Description:This function parse pages and stores the unique links retrieved from those pages in the Map.  
	 * INPUT: list , resource
	 * list  : A Map which stores the url links to be crawled as keys and depths at which pages were discovered. 
	 * resource   : The URL to be parsed 
	 * OUTPUT: int value for break condition.
	 * USAGE: Used when no keyphrase is used.
	 * **********/
	int docparser(Map<String,Integer> list, String resource) throws InterruptedException{
		Document doc;
		int depth = list.get(resource);

		try {
			doc = Jsoup.connect(resource).get();
			total_pages_crawled++;
			
			retrieved_URL_list.add(resource);   //
			if(retrieved_URL_list.size()== 1000) return 1;
			// get all links
			Elements links = doc.select("a[href]");
			for (Element link : links) {
				// get the value from href attribute
				Matcher matcher = pattern.matcher(link.attr("href"));

				if(!matcher.find()){
					Matcher seed_matcher = seed_pattern.matcher(link.absUrl("href"));
					if(seed_matcher.find()){				
						if(!list.containsKey(link.absUrl("href").toString()) && ((depth+1)<5)){
							list.put(link.absUrl("href").toString(),depth+1);
						}			 
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();

		}
		return 0;
	}

	/* Function : void crawlLinks(Map<String,Integer> list, String key)
	 * Description:This function crawls the links which are stored as keys in Map.It uses docparser() to parse the page.
	 * INPUT: list , key
	 * list  : A Map which stores the url links to be crawled as keys and depths at which pages were discovered. 
	 * Key   : The keyphrase   
	 * OUTPUT: None
	 * */
	void crawlLinks(Map<String,Integer> list, String key) throws InterruptedException,IOException{
		
		Iterator<String> iterator = list.keySet().iterator();	
		
		while(iterator.hasNext()){
			String resource = iterator.next();
			int val;
			TimeUnit.SECONDS.sleep(1);	//politeness 
			if(key == null){
				val = docparser(list,resource);
			}
			else{
				val = docparser(list,resource,key);	
			}
			if(val == 1) break;
		}
		readWriteFile();
	}
	
/********************Below Function write the  output to an external file defined in C:/Output.txt ********************/	
	void readWriteFile() throws IOException{
		File file = new File("C:/Output.txt");
		BufferedWriter output = new BufferedWriter(new FileWriter(file,true));
		Iterator<String> iterator = retrieved_URL_list.iterator();
			while(iterator.hasNext()){
				output.write(iterator.next()+"\n");
			}
			output.flush();
			output.close();
			//System.out.println("Total Relevent Links:"+retrieved_URL_list.size());
	}

}





