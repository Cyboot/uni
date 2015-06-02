-- .jar file in the same directory
REGISTER RDFStorage.jar;

-- load sibdataset200 
inputData = LOAD '/data/sib/sibdataset200.nt' USING RDFStorage() AS (sub:chararray, pred:chararray, obj:chararray);

-- consider only sib:like edges
subset = FILTER inputData by pred matches 'sib:like';

-- group and count likes for each user
groupedSubset   = group subset by sub;
totalLikes    = foreach groupedSubset generate group, COUNT(subset.obj) as likes;

-- calculate the avg based on prior result
groupedLikes = group totalLikes all;
averageLikes = foreach groupedLikes generate AVG(totalLikes.likes);

-- print result on command line
DUMP averageLikes;
