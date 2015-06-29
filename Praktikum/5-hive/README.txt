## Pig 
indata = LOAD '/input/10_4.nt' USING RDFStorage() AS (s,p,o) ;
STORE indata INTO '/input/hive/10_4' USING PigStorage('\t') ;




# Hive
CREATE EXTERNAL TABLE data_4 (
	subject STRING, 
	predicate STRING, 
	object STRING )
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
LOCATION '/input/10_4.tsv';