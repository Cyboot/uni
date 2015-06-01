
file = LOAD '$input' USING PigStorage('\t') AS (s:chararray,p:chararray,o:chararray);

classes = FILTER file BY (p == 'a' OR p == 'rdf:type' OR p == ' <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>');

collapsed = GROUP classes BY s;
jcollapsed = FOREACH collapsed GENERATE group AS instance, BagToString(classes, ',') AS classes;

jcollapsed = SAMPLE jcollapsed 0.01;
DUMP jcollapsed;

jcs = JOIN file BY s, classes BY s;
fjcs = FOREACH jcs GENERATE classes::o AS s, file::p AS p, file::o AS o;

fjcso = JOIN fjcs BY o, classes BY s;
ffjcso = FOREACH fjcso GENERATE fjcs::s AS s, fjcs::p AS p, classes::o AS o;

ffjcso = DISTINCT ffjcso;

--DUMP ffjcso;
