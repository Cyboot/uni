-- .jar file in the same directory
REGISTER RDFStorage.jar;

-- load sibdataset200 
inputData1 = LOAD '/data/sib/sibdataset200.nt' USING RDFStorage() AS (user:chararray, creator:chararray, post:chararray);
inputData2 = LOAD '/data/sib/sibdataset200.nt' USING RDFStorage() AS (post:chararray, isa:chararray, typ:chararray);

-- consider only sioc:creator_of edges
creators = FILTER inputData1 by creator matches 'sioc:creator_of';

-- consider only a edges
types = FILTER inputData2 by isa matches 'a' and typ matches 'sib:Post';

-- join both prepared sets
joinedSets = join creators by post, types by post;

-- group and count ingoing foaf:knows edged for each user
groupedSubset   = group joinedSets by user;
totalIngoings   = foreach groupedSubset generate group, COUNT(joinedSets.typ);

dump totalIngoings;
