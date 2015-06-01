
REGISTER 'edge_format.py' USING jython AS f;

file = LOAD 'friendships.nt' USING PigStorage(' ') AS (x:chararray, y:chararray);

d = FOREACH file GENERATE x, f.hashed(TOTUPLE(y, 'asd'));

DUMP d;

--STORE d INTO 'hbase://friendships' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('cf:*');
