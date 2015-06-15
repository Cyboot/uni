REGISTER 'RDFStorage.jar';

indata = LOAD '/input/10_6.nt' USING RDFStorage() AS (s:chararray, p:chararray, o:chararray);

-- Only friendships are interesting here.
friendships = FILTER indata BY p == 'foaf:knows';

-- We need the friendships of our user.
friendships_for_user = FILTER friendships BY s == '$user';

-- Generate friends-of-friends (excluding our user) as such:
-- USER -> FRIEND -> GUY
friendships_of_friends_of_user = JOIN friendships BY s, friendships_for_user BY o PARALLEL 8;
non_cyclic_friendships_of_friends_of_user = FILTER friendships_of_friends_of_user BY friendships::o != '$user';

-- Shrink the list and apply new names.
friendship_pairs = FOREACH non_cyclic_friendships_of_friends_of_user GENERATE friendships::o AS notfriend:chararray, friendships_for_user::o AS friend:chararray;

-- Group and then count the paths from GUY to USER
possible_friendships_grouped = GROUP friendship_pairs BY notfriend;
possible_friendships_counted = FOREACH possible_friendships_grouped GENERATE group, SIZE(friendship_pairs);

-- Order them by count descending
possible_friendships_sorted = ORDER possible_friendships_counted BY $1 DESC PARALLEL 8;

-- And dump only the top 10 results.
possible_friendships_limited = LIMIT possible_friendships_sorted 10;

DUMP possible_friendships_limited;
