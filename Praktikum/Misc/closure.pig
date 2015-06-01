
-- Load the data
indata = LOAD '$input' USING PigStorage(' ') AS (s, p, o);

-- Filter out anything not being a friendship.
friendships = FILTER indata BY p == 'foaf:knows';

-- Copies the data, as Pig does not allow self-joins
copy = FOREACH friendships GENERATE *;

-- Join the data together, so we get A knows B knows C
joined = JOIN friendships BY o, copy BY s;

-- Eliminate A knows X knows A
--joined_filter = FILTER joined BY friendships::s != copy::o;

shrinked = FOREACH joined GENERATE friendships::s AS s, friendships::p AS p, copy::o AS o;
unioned = UNION friendships, shrinked;
distincted = DISTINCT unioned;

STORE distincted INTO '$output' USING PigStorage(' ');
