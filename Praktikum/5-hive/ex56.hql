-- How many posts a user have created

-- hive -f ex56.hql

SELECT creations.subject as user, COUNT(creations.object) 

FROM data_6 creations JOIN data_6 posts
ON creations.object = posts.subject

WHERE
creations.predicate = 'sioc:creator_of' AND
posts.object = 'sib:Post' AND posts.predicate = 'a'

GROUP BY creations.subject;
