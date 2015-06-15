REGISTER 'RDFStorage.jar';

indata = LOAD '/input/10_6.nt' USING RDFStorage() AS (s:chararray, p:chararray, o:chararray);

-- Filter the data in the categories "photos", "friendships" and "tags".
photos = FILTER indata BY p == 'a' AND o == 'sib:Photo';
-- only relevant friendships, i.e. incoming to $user
friendships = FILTER indata BY o == '$user' AND p == 'foaf:knows';
tags = FILTER indata BY p == 'sib:usertag';

-- Filter out all tags that are not tagging our user
tags_of_user = FILTER tags BY o == '$user';

-- Get all the tags that have friends of the user as object
tags_of_friends = JOIN tags BY o, friendships BY s PARALLEL 8;

-- Join the tags of friends and tags of users, so we get a list of tags they both have in common.
common_tags = JOIN tags_of_user BY s, tags_of_friends BY tags::s PARALLEL 8;

-- Group this list by friend
grouped_tags = GROUP common_tags BY tags_of_friends::tags::o;

-- And count the tags by friend.
counted_tags = FOREACH grouped_tags GENERATE group, COUNT(common_tags);

-- Sort and limit this to only include the top 10 results.
ordered_tags = ORDER counted_tags BY $1 PARALLEL 8;
limited_tags = LIMIT ordered_tags 10;

DUMP limited_tags;
