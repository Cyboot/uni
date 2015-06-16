REGISTER 'RDFStorage.jar';

sh echo " "
sh echo "======================"
sh echo "==== Exercise 4.1 ===="

indata = LOAD '/input/all.nt' USING RDFStorage() AS (s:chararray, p:chararray, o:chararray);

-- we are only interested in birthdays
birthdays = FILTER indata BY p == 'foaf:birthday';


-- split the date, use month (Fomat: "YYYY-mm-dd" ex:"1983-06-08")
sh echo "Split date..."
months 				= FOREACH birthdays GENERATE s,p,o, SUBSTRING(o, 6, 8);

-- group by month and count birthdays in the month
sh echo "group and count months..."
grouped_months 		= GROUP months BY $3;
months_count 		= FOREACH grouped_months GENERATE group, COUNT(months) as birthdays;
months_count_all 	= GROUP months_count ALL;


-- Find the maximum of birthdays per month.
sh echo "find the maximum of birthdays per month..."
max_birthday_count 	= FOREACH months_count_all GENERATE MAX(months_count.$1);


-- And now get all months that have this value as their count.
sh echo "Selecting the month with the most birthdays..."
max_birthday_months = JOIN months_count BY $1, max_birthday_count BY $0 USING 'replicated';


-- we just want to know the month
birthday_month = FOREACH max_birthday_months GENERATE $0;

sh echo " "
sh echo "+++++++++++++++++++++"
sh echo "++++++ Results ++++++"
sh echo "Month with the most birthdays is: "
DUMP birthday_month;

