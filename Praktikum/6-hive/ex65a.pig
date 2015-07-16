REGISTER 'RDFStorage.jar';

indata = LOAD '$input' USING RDFStorage() AS (s:chararray, p:chararray, o:chararray);

-- Filter for relevant data
posts 		= FILTER indata BY p == 'a' 			AND o == 'sib:Post';
creations 	= FILTER indata BY p == 'dc:created';
likes 		= FILTER indata BY p == 'sib:like';
commentings = FILTER indata BY p == 'sioc:reply_of';

-- Parse the dates of creations
parsed_creations = FOREACH creations GENERATE s, p, ToUnixTime(ToDate(SUBSTRING(o, 1, 20), 'yyyy-MM-dd\'T\'HH:mm:ss', 'UTC')) as uo;

-- get all likes that are made for posts
likes_for_posts = JOIN posts BY s, likes BY o;
-- group them by their post
grouped_likes 	= GROUP likes_for_posts BY posts::s;
-- and count them.
counted_likes = FOREACH grouped_likes GENERATE group AS pid, COUNT(likes_for_posts) AS likecount;


-- get all comments that are done for posts, and also join the creation date of the post
commentings_for_posts = JOIN posts BY s, commentings BY o, parsed_creations BY s;
commentings_for_posts = FOREACH commentings_for_posts GENERATE posts::s AS pid, commentings::s AS cid, parsed_creations::uo AS postdate;

-- add the date of creation to the comments, and rename all the columns again
dated_comments 	= JOIN commentings_for_posts BY cid, parsed_creations BY s;
dated_comments 	= FOREACH dated_comments GENERATE commentings_for_posts::pid AS pid, commentings_for_posts::cid AS cid, commentings_for_posts::postdate AS postdate, parsed_creations::uo AS commentdate;

-- group them to the post
grouped_comments = GROUP dated_comments BY pid;

-- and count all comments, get the maximum date a comment was written and the minimum date of posts (technically, it's a bag, and MIN is used to extract the value).
counted_comments = FOREACH grouped_comments GENERATE group AS pid, COUNT(dated_comments) AS commentcount,
	MAX(dated_comments.commentdate) AS maxdate, MIN(dated_comments.postdate) AS mindate;

-- combine the two bits of information
joined_counts = JOIN counted_likes BY pid, counted_comments BY pid;

-- and generate the ratio by division and the lifetime by subtraction.
-- Technically, the lifetime is __not__ in days, but in seconds, contrary to the wording in the task.
-- This could be trivially converted to be in days by dividing with 86400.
divided_counts = FOREACH joined_counts GENERATE counted_likes::pid AS pid, ((double) counted_likes::likecount) / ((double) counted_comments::commentcount) AS ratio,
  counted_comments::maxdate - counted_comments::mindate AS lifetime;

-- contrary to the wording in the task, let's sort the posts descendingly, as we can only take the _top_ 50 posts, not the bottom 50 posts later on.
sorted_counts = ORDER divided_counts BY ratio DESC, lifetime DESC;
-- take 50 posts
limited_counts = LIMIT sorted_counts 50;

DUMP limited_counts;