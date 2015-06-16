REGISTER 'RDFStorage.jar';

sh echo " "
sh echo "======================"
sh echo "==== Exercise 4.3 ===="

%default inputDir '/data/sib/sibdataset200.nt'
indata = LOAD '$inputDir' USING RDFStorage() AS (s:chararray, p:chararray, o:chararray);

-- the three classes
photos 		= FILTER indata BY p == 'a' 			AND o == 'sib:Photo';
friendships = FILTER indata BY p == 'foaf:knows'	AND o == '$user';
tags 		= FILTER indata BY p == 'sib:usertag';


-- We are just intersted in tags of our user
tags_of_user 	= FILTER tags 	BY o == '$user';
-- but also get tags of the users friends
tags_of_friends = JOIN tags BY o, friendships BY s;

-- generate a list of common tags with the user and his friends
common_tags 	= JOIN tags_of_user BY s, tags_of_friends BY tags::s;

-- Group this list by friend
grouped_tags 	= GROUP common_tags BY tags_of_friends::tags::o;

-- count the tags by friend
counted_tags 	= FOREACH grouped_tags GENERATE group, COUNT(common_tags);

-- sort and limit to the top-10
ordered_tags 	= ORDER counted_tags BY $1;
top10_tags 		= LIMIT ordered_tags 10;

sh echo " "
sh echo "+++++++++++++++++++++"
sh echo "++++++ Results ++++++"
sh echo "Top 10 close friends of $user: "
DUMP top10_tags;
