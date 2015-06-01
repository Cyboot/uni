file = LOAD '/user/teamprojekt2014/RDFProject/GraphData/Output/sib205/CollapsedGraph/' USING PigStorage('\t') AS (s,p,o,c);

ffile = FILTER file BY o != 'null';

indexed = FOREACH ffile GENERATE CONCAT(CONCAT(s, '\t'), o) AS k, s, p, o, c;
grouped = GROUP indexed BY k;
rels = FOREACH grouped GENERATE group, SUM(indexed.c);

STORE rels INTO '/user/teamprojekt2014/RDFProject/GraphData/Output/sib205/CollapsedMatrix' USING PigStorage('\t');

inedges = FOREACH file GENERATE CONCAT('collapsed_', s) AS k, TOMAP(CONCAT(CONCAT(s, '\t'), CONCAT(CONCAT('\t', p), CONCAT('\t', p))), c);
outedges = FOREACH file GENERATE CONCAT('collapsed_', o) AS k, TOMAP(CONCAT(CONCAT(s, '\t'), CONCAT(CONCAT('\t', p), CONCAT('\t', p))), c);

edges = UNION inedges, outedges;

STORE edges INTO 'hbase://rdfanalyzer_sib205' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('cf:*');

