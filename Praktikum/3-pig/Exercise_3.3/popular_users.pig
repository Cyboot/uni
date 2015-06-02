-- .jar file in the same directory
REGISTER RDFStorage.jar;

-- load sibdataset200 
inputData = LOAD '/data/sib/sibdataset200.nt' USING RDFStorage() AS (sub:chararray, pred:chararray, obj:chararray);

-- consider only foaf:knows edges
subset = FILTER inputData by pred matches 'foaf:knows';

-- group and count ingoing foaf:knows edged for each user
groupedSubset   = group subset by obj;
totalIngoings   = foreach groupedSubset generate group, COUNT(subset.sub) as friends;

-- calculate the avg based on prior result
sorted  = ORDER totalIngoings by friends DESC;
topK    = LIMIT sorted $k;


DUMP topK;
