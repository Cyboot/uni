REGISTER 'hdfs:///user/weberjo/lib/RDFStorage.jar';


indata = LOAD '/data/sib/sibdataset200.nt' USING RDFStorage() AS (s,p,o) ;

posts = FILTER indata BY p == 'a' and o == 'sib:Post';
creatings = FILTER indata BY p == 'sioc:creator_of';

post_creatings = JOIN posts BY s, creatings BY o;

user_creatings = GROUP post_creatings BY creatings::s;

user_creatings_count = FOREACH user_creatings GENERATE group, COUNT(post_creatings);

DUMP user_creatings_count;
