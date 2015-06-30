-- Average Likes of user 

-- hive -f ex54.hql

SELECT COUNT(likes.predicate) / COUNT(DISTINCT users.subject) AS average_likes

FROM data_6 users LEFT OUTER JOIN data_6 likes
ON likes.subject = users.subject

WHERE

users.predicate = 'a' AND users.object = 'sib:User' AND
likes.predicate = 'sib:like';

