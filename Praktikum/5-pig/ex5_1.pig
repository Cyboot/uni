REGISTER 'RDFStorage.jar';
REGISTER '../5-pig-1.1-jar-with-dependencies.jar';

sh echo " "
sh echo "======================"
sh echo "==== Exercise 5.1 ===="

%default inputDir '/input/all.nt'
indata = LOAD '$inputDir' USING RDFStorage() AS (sub:chararray, pred:chararray, obj:chararray);


-- Filter out any data that are not birthdays
birthdays = FILTER indata BY pred == 'foaf:birthday' AND sub != '';

-- calculate the age of the users
age = FOREACH birthdays GENERATE ex1.AgeOfUser($2) as years:float;

--
age_grouped   = group age all;
age_avg       = foreach age_grouped generate AVG(age.years);


sh echo "Average Age [in years] of Users: "
dump age_avg;