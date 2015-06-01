file = LOAD '$input' USING PigStorage('\t') AS (s:chararray,p:chararray,o:chararray);

subjects = FOREACH file GENERATE s;
distinct_subjects = DISTINCT subjects;
randomed_subjects = FOREACH distinct_subjects GENERATE RANDOM() AS r, s;
ordered_subjects = ORDER randomed_subjects BY r;
sorted_subjects = FOREACH ordered_subjects GENERATE s;

limited_subjects = LIMIT sorted_subjects 5;

STORE limited_subjects INTO '$output' USING PigStorage();
