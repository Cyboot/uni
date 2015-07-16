SELECT friendship.subject
FROM sib400 friendship

LEFT OUTER JOIN sib400 ownmemberships 
ON 	ownmemberships.object 		= friendship.subject
AND ownmemberships.subject 		= '$group'
AND ownmemberships.predicate	= 'sioc:has_member'

JOIN sib400 memberships ON friendship.object = memberships.object

WHERE friendship.predicate 		= 'foaf:knows'
AND memberships.predicate 		= 'sioc:has_member'
AND memberships.subject 		= '$group'
AND ownmemberships.subject 		IS NULL

GROUP BY friendship.subject 
HAVING COUNT(memberships.object) >= 3;

