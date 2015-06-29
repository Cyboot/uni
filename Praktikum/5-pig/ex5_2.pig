REGISTER 'RDFStorage.jar';
REGISTER '../5-pig-1.1-jar-with-dependencies.jar';

sh echo " "
sh echo "======================"
sh echo "==== Exercise 5.2 ===="

%default inputDir '/input/10_4.nt'
%default refdate '2015-06-06'
%default user 'sibp:p38'
indata 			= LOAD '$inputDir' USING RDFStorage() AS (s:chararray, p:chararray, o:chararray);

friendships 	= FILTER indata BY p == 'foaf:knows' AND s == '$user';
birthdays 		= FILTER indata BY p == 'foaf:birthday' AND s != '';

soon_birthdays = FILTER birthdays BY ex2.SoonBirthday(o, '$refdate');

DUMP soon_birthdays

--person_friends = JOIN account_ofs BY s, friendships BY o PARALLEL 8;
--friends_birthdays = JOIN person_friends BY account_ofs::o, soon_birthdays BY s PARALLEL 8;

--shrinked_list = FOREACH friends_birthdays GENERATE soon_birthdays::o AS birthday, person_friends::account_ofs::s;
--DUMP shrinked_list;
