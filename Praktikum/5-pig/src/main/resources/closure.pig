-- Load the data: currentInput + originalInput
indataOrigin 		= LOAD '$inputOrigin' 	USING PigStorage(' ') AS (s, p, o);
indataIterate 		= LOAD '$inputIterate'	USING PigStorage(' ') AS (s, p, o);

-- Filter out anything not being a friendship.
friendshipsOrigin 	= FILTER indataOrigin  BY p == 'foaf:knows';
friendshipsIterate 	= FILTER indataIterate BY p == 'foaf:knows';

-- Join the data together, so we get A knows B knows C
joined 				= JOIN friendshipsOrigin 	BY o, friendshipsIterate BY s;
shrinked 			= FOREACH joined 			GENERATE friendshipsOrigin::s, friendshipsOrigin::p, friendshipsIterate::o;

-- create the union of the new results + original data
unioned 			= UNION friendshipsOrigin, shrinked;

-- Keep only distinct data
distincted 			= DISTINCT unioned;