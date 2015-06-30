## Pig 
REGISTER 'RDFStorage.jar';
indata = LOAD '/data/sib/sibdataset200.nt' USING RDFStorage() AS (s,p,o) ;
STORE indata INTO '/user/schmiedt/sibdataset200_tab' USING PigStorage('\t') ;




# Hive
CREATE EXTERNAL TABLE data_6 (
	subject STRING, 
	predicate STRING, 
	object STRING )
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
LOCATION '/user/schmiedt/sibdataset200_tab';