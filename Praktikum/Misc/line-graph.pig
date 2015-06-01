file = LOAD 'hdfs:///user/teamprojekt2014/RDFProject/GraphData/Output/sib206/CleanedInputGraph/part-m-00000' USING PigStorage('\t') AS (s:chararray,p:chararray,o:chararray);

fileP = FOREACH file GENERATE *;

joined = JOIN file BY CONCAT(o, CONCAT('\t', p)), file BY CONCAT(s, CONCAT('\t', p));

joinedF = FOREACH joined GENERATE CONCAT(file::s, CONCAT(file::p, file::o)), file::p, CONCAT(fileP::s, CONCAT(fileP::p, fileP::o));


DUMP joinedF;
