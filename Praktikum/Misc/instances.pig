REGISTER 'edge_format.py' USING jython AS f;

SET default_parallel 8;
SET hbase.zookeeper.quorum '$quorum';

file = LOAD '$input' USING PigStorage('\t') AS (s:chararray,p:chararray,o:chararray);

subjects = FOREACH file GENERATE CONCAT('sib200_', s) AS s;
subjects = DISTINCT subjects;

incomings = FOREACH file GENERATE s, p, o, 'in' AS direction, CONCAT('sib200_', o) AS k, CONCAT('sib200_', s) AS k2;
outgoings = FOREACH file GENERATE s, p, o, 'out' AS direction, CONCAT('sib200_', s) AS k, CONCAT('sib200_', o) AS k2;

joined_incomings = JOIN subjects BY s, incomings BY k2;
cleaned_incomings = FOREACH joined_incomings GENERATE incomings::s AS s, incomings::p AS p, incomings::o AS o, incomings::direction AS direction, incomings::k AS k, (subjects::s is null ? 'property' : 'edge') AS t;
formatted_incomings = FOREACH cleaned_incomings GENERATE k, f.formatTriple(s,p,o,t,direction);


joined_outgoings = JOIN subjects BY s RIGHT OUTER, outgoings BY k2;
cleaned_outgoings = FOREACH joined_outgoings GENERATE outgoings::s AS s, outgoings::p AS p, outgoings::o AS o, outgoings::direction AS direction, outgoings::k AS k, (subjects::s is null ? 'property' : 'edge') AS t;
formatted_outgoings = FOREACH cleaned_outgoings GENERATE k, f.formatTriple(s,p,o,t,direction);

edges = UNION formatted_incomings, formatted_outgoings;

ranked_edges = RANK edges;

formatted_edges = FOREACH ranked_edges GENERATE CONCAT(CONCAT(k, '_'), (chararray) rank_edges), formatted;

--STORE formatted_edges INTO 'hbase://$tableName' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('cf:link');
explain formatted_edges;
