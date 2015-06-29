
-- Load the data
indata = LOAD '$input' USING PigStorage(' ') AS (s, p, o);

-- Filter out anything not being a friendship.
friendships = FILTER indata BY p == 'foaf:knows';

-- Copies the data, as Pig does not allow self-joins
copy = FOREACH friendships GENERATE *;

-- Join the data together, so we get A knows B knows C
joined = JOIN friendships BY o, copy BY s;
-- Convert the joined data in a format suitable to union with the original data.
-- Emit A knows C.
shrinked = FOREACH joined GENERATE friendships::s, friendships::p, copy::o;

-- Keep the original data, but limit to friendships (we don't need the rest)
unioned = UNION friendships, shrinked;

-- Keep only distinct data
distincted = DISTINCT unioned;

