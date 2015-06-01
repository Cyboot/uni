
REGISTER 'enumerate.py' USING jython AS e;

file = LOAD '$input' USING PigStorage('\t') AS (s:chararray,p:chararray,o:chararray);

classes = FILTER file BY (p == 'a' OR p == 'rdf:type' OR p == ' <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>');
randomed_classes = FOREACH classes GENERATE o AS x, RANDOM() AS f, s, p, o;

randomed_predicates = FOREACH file GENERATE p AS x, RANDOM() AS f, s, p, o;

randomed = UNION randomed_classes, randomed_predicates;

grouped = GROUP randomed BY x;

topped = FOREACH grouped {
	ordered = ORDER randomed BY f;
	result = LIMIT ordered $amount;
	GENERATE group, result;
};

fed = FOREACH topped GENERATE group, e.rankToMapAndConcatExceptFirst('simple-', result);

DESCRIBE topped;
DESCRIBE fed;

DUMP fed;
--STORE fed INTO 'hbase://$tableName' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('cf:*');
