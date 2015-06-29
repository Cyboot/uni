
# Calculate the count of incoming edges per user and report the top-k results
SELECT object AS user, COUNT(predicate) AS amount FROM sibdata

WHERE predicate = 'foaf:knows'

GROUP BY object
SORT BY amount DESC
LIMIT $k;
