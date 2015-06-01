
REGISTER 'birthday-1.jar';
REGISTER 'lib/RDFStorage.jar';

indata = LOAD '/data/sib/sibdataset200.nt' USING RDFStorage() AS (s:chararray, p:chararray, o:chararray);

-- Filter out any data that are not birthdays
birthdays = FILTER indata BY p == 'foaf:birthday';

-- Parse the dates of birth to a long
parsed_birthdays = FOREACH birthdays GENERATE s,p,o, ToDate(o, '"yyyy-MM-dd"^^\'xsd:date\'') AS parsed;

-- Combine all of those dates together in one group
all_birthdays = GROUP parsed_birthdays ALL;

-- And calculate the average age of the birthdates
avg = FOREACH all_birthdays GENERATE AverageAge(parsed_birthdays.parsed);

DUMP avg;
