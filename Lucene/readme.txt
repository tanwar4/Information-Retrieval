Goals:
Introduction to Lucene: Setup, indexing, search
Zipf’s law

Perform the following:
 
Index the raw (unpre-processed) CACM corpus http://www.search-engines-book.com/collections/ using Lucene. Make sure to use “SimpleAnalyzer” as your analyzer.

Build a list of (unique term, term_frequency) pairs over the entire collection. Sort by frequency.

Plot the Zipfian curve based on the list generated in (2) (you do not need Lucene to perform this. You may use any platform/software of your choosing to generate the plot)

Perform search for the queries below. You need to return the top 100 results for each query. Use the default document scoring/ranking function provided by Lucene.
 

portable operating systems
code optimization for space efficiency
parallel algorithms
parallel processor in information retrieval


*************************************************************************************************************************
The source code is present in src package

 How  to run: 
 
 If running on console:
 
 1.Save HW4.java on disk.
 2. Save the  jsoup-1.7.3.jar to the disk (Say C:\)
 3. Save  the jfreechart.jar-1.0.19.jar to the disk
 4. Save the jcommon-1.0.23.jar to disk 
 5. Add the lucene jar to classpath
 6.Compile the java file by giving below command: 

 javac  -cp  " C:\jsoup-1.7.3.jar  C:\jfreechart.jar-1.0.19.jar jcommon-1.0.23.jar "  HW4.java 
 
 7. Run by giving the command 
 java HW4

 
 If running on eclipse:
 
1. Import the src package
2. add the jsoup , jchart, jcommons and lucene jar to buildpath
3. Run the file 

-----------------------------------------------------------------------------------------
Algorithm:
-----------------------------------------------------------------------------------------
The program starts by prompting the user for index file location.
This is place where index file will be created.

The program next prompts for Location of docs which are to be indexed.
provide the name of the folder or file which has to be indexed

Since simple analyzer does not filter out html tags.I am using
jsoup to filter out html tags and then indexing the document.
I am also storing the text of document in "snippet" field of indexer
to generate snippet while querying.

Once the indexing has been completed, Querying process starts.

Each query outputs a "(querytext).txt" file which contains top 100 documents with Id, Score
and  text snippet of 200 charachters.
For this assignment the queries will generate following files in C:\\ drive
"code optimization for space efficiency.txt"
"parallel algorithms.txt"
"parallel processor in information retrieval.txt"
"portable operating systems.txt"

Once the user quits querying process A zipf plot is displayed
which is plotted on rank and probablity of each term.

Zipf plot requires rank frequency distribution which I am calculating
using Lucene Multifield API  and storing it in a hashmap.
This map is  sorted in descending order and contents
are written to a file "rank_freq.txt".

This map is passed to the constructor of another java class Chart
which draws the graph using jChart API.

-----------------------------------------------------------------------------------------

-----------------------------------------------------------------------------------------

Delieverables:

"Query_com.txt" : contains query comparision result
"rank_freq.txt" : rank and frequency in descending order
"query_result"  : This folder contains the top 100 query results for each query alongwith snippet

JAR:
jsoup
jcommons
jChart 
  


