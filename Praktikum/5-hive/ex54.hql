
# Divides the number of likes through the number of users to get the global average.

SELECT COUNT(likes.predicate) / COUNT(DISTINCT users.subject) AS average_likes

FROM sibdata users LEFT OUTER JOIN sibdata likes
ON likes.subject = users.subject

WHERE

users.predicate = 'a' AND users.object = 'sib:User' AND
likes.predicate = 'sib:like';

