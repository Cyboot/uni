REGISTER 'edge_format.py' USING jython AS f;

SET default_parallel 8;
SET hbase.zookeeper.quorum '$quorum';
SET mapreduce.fileoutputcommitter.marksuccessfuljobs false;

file = LOAD '$input' USING PigStorage('\t') AS (s:chararray,p:chararray,o:chararray);

subjects = FOREACH file GENERATE s AS s;
subjects = DISTINCT subjects;

incomings = FOREACH file GENERATE s, p, o, 'in' AS direction, o AS k;
outgoings = FOREACH file GENERATE s, p, o, 'out' AS direction, s AS k;

joined_incomings = JOIN subjects BY s, incomings BY o;
cleaned_incomings = FOREACH joined_incomings GENERATE incomings::s AS s, incomings::p AS p, incomings::o AS o, incomings::direction AS d, (subjects::s is null ? 'property' : 'edge') AS t, incomings::k AS k;
formatted_incomings = FOREACH cleaned_incomings GENERATE k, f.hashed(s,p,o,d,t) AS hash, f.formatTriple(s,p,o,d,t) AS data;
mapped_incomings = FOREACH formatted_incomings GENERATE k, TOMAP(hash, data);

joined_outgoings = JOIN subjects BY s RIGHT OUTER, outgoings BY o;
cleaned_outgoings = FOREACH joined_outgoings GENERATE outgoings::s AS s, outgoings::p AS p, outgoings::o AS o, outgoings::direction AS d, (subjects::s is null ? 'property' : 'edge') AS t, outgoings::k AS k;
formatted_outgoings = FOREACH cleaned_outgoings GENERATE k, f.hashed(s,p,o,d,t) AS hash, f.formatTriple(s,p,o,d,t) AS data;
mapped_outgoings = FOREACH formatted_outgoings GENERATE k, TOMAP(hash, data);

edges = UNION mapped_incomings, mapped_outgoings;

--EXPLAIN edges;
DUMP mapped_incomings;

--STORE edges INTO 'hbase://$tableName' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('cf:*');
