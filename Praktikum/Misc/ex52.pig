
REGISTER 'birthday-1.jar';
REGISTER 'lib/RDFStorage.jar';

indata = LOAD '$input' USING RDFStorage() AS (s:chararray, p:chararray, o:chararray);

friendships = FILTER indata BY p == 'foaf:knows' AND s == '$user';
account_ofs = FILTER indata BY p == 'sioc:account_of';
birthdays = FILTER indata BY p == 'foaf:birthday';

parsed_birthdays = FOREACH birthdays GENERATE s, p, o, ToDate(o, '"yyyy-MM-dd"^^\'xsd:date\'') AS pd;
soon_birthdays = FILTER parsed_birthdays BY HasAnniversaryUntil(pd, ToDate('$limit', 'yyyy-MM-dd'));

person_friends = JOIN account_ofs BY s, friendships BY o;
friends_birthdays = JOIN person_friends BY account_ofs::o, soon_birthdays BY s;

shrinked_list = FOREACH friends_birthdays GENERATE soon_birthdays::o AS birthday, person_friends::account_ofs::s;
DUMP shrinked_list;
