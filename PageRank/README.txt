
The programe runs in two mode:
1.For Graph
2.Dataset

The delievered code has been defaulted to run on dataset.
Keep the inlinks files in C:\

If running the graph model, please uncomment the below lines:

Line 51: iteration = Integer.parseInt(args[0]);
Line 163 : while(iteration >0){ 
Line 196:	iteration --; 

Also, Comment the following lines:
Line 164: while(cond){ 

Instructions are given in source code as well.

Compilation and Running:

1. If Runniing the small graph model
  Download the "PageRank.java " file.
  Uncomment the lines  in main function and Calculatepr()  functon
(instructions are given in source file) 
2. compile the file -> javac PageRank.java 
3.  Run the file ->  java PageRank (1/10/100 i.e any one of these no)


1.Running the Dataset
 The code has been defaulted to use dataset.
It can be run by giving  java PageRank  command.

The output is generated in C:\output.txt file