Functionality
SELECT COUNT(DISTINCT ? s ) AS ? functional WHERE {
	? s <$property> ?o1 .
	FILTER NOT EXISTS {? s <$property> ?o2 . FILTER(? o1 != ?o2 )}
}


Inverse-
Functionality
SELECT COUNT(DISTINCT ?o) AS ? inversefunctional WHERE {
	? s1 <$property> ?o .
	FILTER NOT EXISTS {? s2 <$property> ?o . FILTER(? s1 != ? s2 )}
}


Symmetry
SELECT (COUNT(∗) AS ?symmetric) WHERE {
	? s <$property> ?o .
	?o <$property> ?s .
}


Asymmetry
SELECT (COUNT(∗) AS ?asymmetric) WHERE {
	? s <$property> ?o .
	FILTER NOT EXISTS {?o <$property> ?s .}
}


Reflexivity
SELECT (COUNT(DISTINCT ?s ) AS ? reflexive ) WHERE {
	? s <$property> ?s .
}


Irreflexivity
SELECT (COUNT(DISTINCT ?s ) AS ? irreflexive ) WHERE {
	? s <$property> ?o .
	FILTER NOT EXISTS {? s <$property> ?s .}
}


Transitivity
SELECT (COUNT(∗) AS ? transitive ) WHERE {
	? s <$property> ?o .
	?o <$property> ?o1 .
	? s <$property> ?o1 .
}