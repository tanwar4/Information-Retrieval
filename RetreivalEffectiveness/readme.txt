Goal:
Evaluate retrieval effectiveness of the serach engine.

 How  to run: 
------------------------------------------------------------
Steps to follow before running 
------------------------------------------------------------
1. Copy the ranking files of query in C:\\query
(query folder contains the ranking of each query calculated
in previous assignment, the name of file is same as that of query).It is provided in the deliverable.

2. Copy the relevance judgment file in "C:\\rel.txt"(the program 
assumes the name of file as rel.txt).It is also provided in deliverable.
 
-------------------------------------------------------------

-------------------------------------------------------------
 If running on console:
 
 1.Save Evaluate.java on disk.
 2.Compile the java file by giving below command: 

 javac Evaluate.java 
 
 3. Run by giving the command 
 java Evaluate

 
 If running on eclipse:
 
1. Import the HW5 package
2. Run 


----------------------------------------------------------------
program:
----------------------------------------------------------------
The program starts by prompting the user for query name 
It than prompts for relevance id for the above mentioned query.
It calculates the required evaluation values and stores it in a file with same name as that of query in C:
The MAp and p@20 values are generated in console.

-----------------------------------------------------------
Mean average precision and p@20 values for queries
-----------------------------------------------------------

Enter the search query (q=quit):
portable operating systems
Enter the CACM  query ID
12
P @ 20 :0.20000000298023224

Enter the search query (q=quit):
code optimization for space efficiency
Enter the CACM  query ID
13
P @ 20 :0.20000000298023224

Enter the search query (q=quit):
parallel algorithms
Enter the CACM  query ID
19
P @ 20 :0.3499999940395355

Enter the search query (q=quit):
q
Mean Average precision0.4332617133404269


