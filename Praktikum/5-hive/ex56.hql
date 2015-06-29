
SELECT creations.subject as user, COUNT(creations.object) 

FROM sibdata creations JOIN sibdata posts
ON creations.object = posts.subject

WHERE
creations.predicate = 'sioc:creator_of' AND
posts.object = 'sib:Post' AND posts.predicate = 'a'

GROUP BY creations.subject;
