REGISTER 'hdfs:///user/weberjo/lib/RDFStorage.jar';

/* firstly, load the data and filter out anything not being a like */
indata = LOAD '/data/sib/sibdataset200.nt' USING RDFStorage() AS (s,p,o) ;
likes = FILTER indata BY p == 'sib:like';


grouped_likes = GROUP likes BY s;
user_likes = FOREACH grouped_likes GENERATE COUNT(likes) as likes;


all_likes = GROUP user_likes all;
average_likes = FOREACH all_likes GENERATE AVG(all_likes.$1);

DUMP average_likes;

