
The zip file contains following items:

indexer: program to create inverted index
bm25  : program to output bm25 ranking

The output file are present in each respective folder.

---------------------------------------------------------------
On Eclipse:
----------------------------------------------------------------
For Indexer:
1.Import the project indexer in workspace
2. In run configuration pass the required 2 argument as command 
  line arguments.
  first argument is corpus file
  second argument is indexer file 
  For e.g: "C:\\tccorpus.txt" "C:\\indexer.txt"
3.Run
----------------------------------------------------------------
For bm25:
1.Import the project bm25 in workspace
2. In run configuration pass the required 4 argument as command 
  line arguments.
  first argument is indexer file
  second argument is queries file 
  third argument is maximum doc required for each query
  fourth argument is result file
E.g:
   "C:\\index.txt"  "C:\\queries.txt"  100  "C:\\results.txt"
3.Run
----------------------------------------------------------------
On Command prompt:
----------------------------------------------------------------
1.Compile the project "indexer" and "bm25" by giving following command:

Assuming the files has been copied in C:\\project

To Compile:
 javac -cp "C:\\project\indexer\indexer" indexer.java
 javac -cp "C:\\project\indexer\bm25"  bm25.java

To Run:
 java -cp "C:\\project\indexer\indexer" indexer                    "C:\\tccorpus.txt" "C:\\indexer.txt"

 java -cp "C:\\project\indexer\bm25"  bm25 "C:\\index.txt"  "C:\\queries.txt"  100  "C:\\results.txt"

----------------------------------------------------------------

Code:
The project contains  two major classes:
1.BM25Ranking.java
2.Tokenizer.java

The source code is extensively documented.
----------------------------------------------------------------
Tokenizer.java :
----------------------------------------------------------------
This class reads the tccorpus.txt and build the inverted index file in.
To do this it uses a Hashmap data structure  whose keys are the index words and whose values are again a hashmap which contains the document no and the frequencies of the index words in thar particular documents.

This class also creates the metadata for the entire corpus i.e another Hashmap which store the each document ID as keys and values as the length of the documents.

The inverted index file first line contains a unique token with id "#0000"
this is used for storing the metadata .The remaining lines are used for storing the inverted index list.

-----------------------------------------------------------
BM25Ranking.java
-------------------------------------------------------------
This class calculates the BM25 scores for each queries.
the inverted index , results file , queries files and maximum no of results are passed as arguements in its constructor while invoking.

For each queries it performs term at a time rank calculation by 
reading indexer file and calculating BM25 score of the documents in which term for that particular query occured.

The output is written back to a file

--------------------------------------------------------------










