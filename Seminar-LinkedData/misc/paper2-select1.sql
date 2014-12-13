SELECT ?type (COUNT(? ind ) AS ?count ) WHERE {
	?ind a <$class >.
	?ind a ?type .
} GROUP BY ?type



SELECT ?type (COUNT(? ind ) AS ?count ) WHERE {
	?ind a <$class >.
	?ind a ?type .
} GROUP BY ?type



SELECT ?p (COUNT(? s ) AS ?count ) WHERE {
	?s ?p ?o .
	?s <$property> ?o .
} GROUP BY ?p



SELECT ?type COUNT(DISTINCT ?ind ) WHERE {
	?ind <$property> ?o .
	?ind a ?type .
} GROUP BY ?type



SELECT ?type (COUNT(DISTINCT ?ind ) AS ?cnt ) WHERE {
	?s <$property> ? ind .
	?ind a ?type .
} GROUP BY ?type



SELECT ? datatype COUNT(DISTINCT ?ind ) WHERE {
	?ind <$property> ? val .
} GROUP BY (DATATYPE(? val ) AS ? datatype)


SELECT ?p (COUNT(∗) AS ? cnt ) WHERE {
	?s <$property> ?o .
	?o ?p ?s .
} GROUP BY ?p