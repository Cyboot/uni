data = LOAD '/data/sib/sibdataset200.nt' USING PigStorage(' ') AS (s,p,o);
typesA = FILTER data BY p == 'a' OR p == 'rdf:type';
typesB = FOREACH typesA GENERATE s,p,o;
subjectJoined = JOIN typesA BY s, data BY s;
objectsJoined = JOIN subjectJoined BY data::o LEFT OUTER, typesA BY s;
d = FOREACH objectsJoined GENERATE subjectJoined::typesA::o AS C, subjectJoined::data::p AS P, typesA::o AS C2;

gd = GROUP d BY (C, P, C2);
fgd = FOREACH gd GENERATE group.C, group.P, (group.C2 IS NULL ? '_' : group.C2), COUNT(d);

EXPLAIN fgd;
#STORE fgd INTO '/tmp/schema.tsv' USING PigStorage('\t');
