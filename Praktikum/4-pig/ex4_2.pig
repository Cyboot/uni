REGISTER 'RDFStorage.jar';

indata = LOAD '/input/10_6.nt' USING RDFStorage() AS (s:chararray, p:chararray, o:chararray);

-- Firstly, we give names to all the vertices and edges in the graph.

-- vertices
users = FILTER indata BY p == 'a' AND o == 'sib:User';
photo_albums = FILTER indata BY p == 'rdf:type' AND o == 'sioct:ImageGallery';
photos = FILTER indata BY p == 'a' AND o == 'sib:Photo';

-- edges
friendships = FILTER indata BY p == 'foaf:knows' AND o == '$user';
incoming_tags = FILTER indata BY p == 'sib:usertag' AND o == '$user';
creations = FILTER indata BY p == 'sioc:creator_of';
containings = FILTER indata BY p == 'sioc:container_of';

-- In order to "walk around the triangle", we join the vertices and edges in a circular fashion:
friends							= JOIN users BY s, 								friendships		BY s PARALLEL 8;
creations_by_friends					= JOIN friends BY users::s, 							creations		BY s PARALLEL 8;
photo_albums_by_friends					= JOIN creations_by_friends BY creations::o, 					photo_albums 		BY s PARALLEL 8;
containings_in_photo_albums_by_friends 			= JOIN photo_albums_by_friends BY photo_albums::s, 				containings		BY s PARALLEL 8;
photos_by_friends 					= JOIN containings_in_photo_albums_by_friends BY containings::o, 		photos			BY s PARALLEL 8;
tags_on_photos_by_friends 				= JOIN photos_by_friends BY photos::s,						incoming_tags		BY s PARALLEL 8;

-- Shrink the outputted list to only one information, the identification of the photos.
tags = FOREACH tags_on_photos_by_friends GENERATE incoming_tags::s AS photo:chararray;

DUMP tags;
