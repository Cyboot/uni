SELECT hashtags.object, COUNT(posts.subject) AS cnt

FROM sib200 hashtags
JOIN sib200 posts ON posts.subject = hashtags.subject

WHERE 
	posts.predicate = 'dc:created' AND
	hashtags.predicate = 'sib:hashtag' AND
	UNIX_TIMESTAMP(TRANSLATE(SUBSTRING(posts.object, 2, 19), "T", " ")) BETWEEN UNIX_TIMESTAMP(CONCAT('$start', ' 00:00:00')) AND UNIX_TIMESTAMP(CONCAT(DATE_ADD('$end', 1), ' 00:00:00'))

GROUP BY 
	hashtags.object
ORDER BY 
	cnt DESC;

SELECT hashtag.object, COUNT(*) as amount
FROM
    ${hiveconf:table} t1
JOIN
    ${hiveconf:table} t2
ON
    t1.subject = t2.subject
JOIN
    ${hiveconf:table} hashtag
ON
    t2.subject = hashtag.subject
WHERE
    t1.predicate == 'a' AND
    t1.object == 'sib:Post' AND
    t2.predicate == 'dc:created' AND
    regexp_extract(t2.object, '"([0-9]{4}-[0-9]{2}-[0-9]{2})T', 1) BETWEEN '${hiveconf:start_date}' AND '${hiveconf:stop_date}' AND
    hashtag.predicate == 'sib:hashtag'
GROUP BY
    hashtag.object
ORDER BY
    amount DESC
LIMIT 10;
