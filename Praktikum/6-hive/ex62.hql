SELECT posts.subject


FROM sib200 posts
JOIN sib200 otherInfo 
	ON posts.subject 	= otherInfo.subject AND posts.predicate 	= 'dc:created'
JOIN sib200 postType 
	ON postType.subject = posts.subject 	AND postType.predicate 	= 'a' 			AND postType.object = 'sib:Post'

	
WHERE 
	(otherInfo.predicate = 'sioc:content' OR otherInfo.predicate = 'sib:hashtag')
AND
	UNIX_TIMESTAMP(TRANSLATE(SUBSTRING(posts.object, 2, 19), "T", " "))
	BETWEEN UNIX_TIMESTAMP(CONCAT('$start', ' 00:00:00')) 
AND 
	UNIX_TIMESTAMP(CONCAT(DATE_ADD('$end', 1), ' 00:00:00'))
AND 
	instr(otherInfo.object, '$event') != 0;
