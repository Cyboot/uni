SELECT registrations.subject

FROM sib200 postings

JOIN sib200 posts
	ON posts.subject = postings.object AND posts.predicate = 'dc:created'
AND UNIX_TIMESTAMP(TRANSLATE(SUBSTRING(posts.object, 2, 19), "T", " "))
  BETWEEN UNIX_TIMESTAMP(CONCAT('$start', ' 00:00:00')) AND UNIX_TIMESTAMP(CONCAT(DATE_ADD('$end', 1), ' 00:00:00'))

  
RIGHT OUTER JOIN sib200 registrations
	ON registrations.subject = postings.subject
AND UNIX_TIMESTAMP(TRANSLATE(SUBSTRING(registrations.object, 2, 19), "T", " ")) < UNIX_TIMESTAMP(CONCAT('$start', ' 00:00:00'))

WHERE postings.predicate IS NULL  AND registrations.predicate = 'dc:date';
