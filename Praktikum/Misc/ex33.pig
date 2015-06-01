REGISTER 'hdfs:///user/weberjo/lib/RDFStorage.jar';


indata = LOAD '/data/sib/sibdataset200.nt' USING RDFStorage() AS (s,p,o) ;
friendships = FILTER indata BY p == 'foaf:knows';

incoming_friendships = FOREACH (GROUP friendships BY s) GENERATE group, COUNT(friendships.o) as friendships;

ordered_incoming_friendships = ORDER incoming_friendships BY friendships DESC;

top_k_users = LIMIT ordered_incoming_friendships $k;

DUMP top_k_users;
