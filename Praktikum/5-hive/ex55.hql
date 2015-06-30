-- Determine the top-K popular persons

-- hive -f ex55.hql -hiveconf k=3


SELECT object AS user, COUNT(predicate) AS amount FROM data_6

WHERE predicate = 'foaf:knows'

GROUP BY object
SORT BY amount DESC
LIMIT ${hiveconf:k};
