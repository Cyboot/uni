REGISTER 'RDFStorage.jar';

sh echo " "
sh echo "======================"
sh echo "==== Exercise 4.2 ===="

%default inputDir '/data/sib/sibdataset200.nt'
indata = LOAD '$inputDir' USING RDFStorage() AS (s:chararray, p:chararray, o:chararray);

-- extract the three classes
users 			= FILTER indata BY p == 'a' 			AND o == 'sib:User';
img_gallery 	= FILTER indata BY p == 'rdf:type' 		AND o == 'sioct:ImageGallery';
photos 			= FILTER indata BY p == 'a' 			AND o == 'sib:Photo';

-- the four edges between
friendships 	= FILTER indata BY p == 'foaf:knows' 	AND o == '$user';
usertags 		= FILTER indata BY p == 'sib:usertag' 	AND o == '$user';
creator_of 		= FILTER indata BY p == 'sioc:creator_of';
container_of 	= FILTER indata BY p == 'sioc:container_of';

-- join the classes with the edges
friends								= JOIN users 							BY s, 				friendships		BY s;
creations_by_friends				= JOIN friends 							BY users::s, 		creator_of		BY s;
img_gallery_by_friends				= JOIN creations_by_friends 			BY creator_of::o, 	img_gallery 	BY s;
container_img_gallery_by_friends 	= JOIN img_gallery_by_friends 			BY img_gallery::s, 	container_of	BY s;
photos_by_friends 					= JOIN container_img_gallery_by_friends BY container_of::o, photos			BY s;
tags_on_photos_by_friends 			= JOIN photos_by_friends 				BY photos::s, 		usertags		BY s;

-- we are only interested in the photos themself
tags 			= FOREACH tags_on_photos_by_friends GENERATE usertags::s AS photo:chararray;


sh echo " "
sh echo "+++++++++++++++++++++"
sh echo "++++++ Results ++++++"
sh echo "Tags of $user: "
DUMP tags;
