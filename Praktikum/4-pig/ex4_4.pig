REGISTER 'RDFStorage.jar';

sh echo " "
sh echo "======================"
sh echo "==== Exercise 4.4 ===="

%default inputDir '/data/sib/sibdataset200.nt'
indata = LOAD '$inputDir' USING RDFStorage() AS (s:chararray, p:chararray, o:chararray);

-- we are only interested in friendships
friendships 			= FILTER indata		 BY p == 'foaf:knows';
friendships_of_user 	= FILTER friendships BY s == '$user';


-- Generate a friend-of-friend chain: USER -> FRIEND -> FRIEND_OF_FRIEND (check/remove cyles)
friendship_chain_tmp 	= JOIN friendships 				BY s, friendships_of_user BY o;
friendship_chain 		= FILTER friendship_chain_tmp 	BY friendships::o != '$user';

-- clean up the results
friendship_pairs 		= FOREACH friendship_chain GENERATE friendships::o AS foaf:chararray, friendships_of_user::o AS friend:chararray;

-- Group and then count the paths from  FRIEND_OF_FRIEND to USER
suggest_friendships_grouped 	= GROUP friendship_pairs BY foaf;
suggest_friendships_counted 	= FOREACH suggest_friendships_grouped GENERATE group, SIZE(friendship_pairs);

-- order and limit to top-10
suggest_friendships_sorted 		= ORDER suggest_friendships_counted BY $1 DESC;
suggest_friendships_top10 		= LIMIT suggest_friendships_sorted 10;

sh echo " "
sh echo "+++++++++++++++++++++"
sh echo "++++++ Results ++++++"
sh echo "Top 10 suggested friends of $user: "
DUMP suggest_friendships_top10;