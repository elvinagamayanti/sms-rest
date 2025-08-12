-- File: src/test/resources/cleanup.sql
-- Optional cleanup script

-- Clean up test data in reverse order of dependencies
DELETE FROM kegiatans WHERE id IN (1,2,3,4);
DELETE FROM outputs WHERE id IN (1,2,3,4);
DELETE FROM programs WHERE id IN (1,2,3);
DELETE FROM users WHERE id IN (1,2,3,4);
DELETE FROM satkers WHERE id IN (1,2,3,4,5);
DELETE FROM direktorats WHERE id IN (1,2,3,4);
DELETE FROM deputis WHERE id IN (1,2,3,4);
DELETE FROM provinces WHERE id IN (1,2,3,4,5);
DELETE FROM roles WHERE id IN (1,2,3,4,5,6,7);

-- Reset auto increment if needed
-- ALTER TABLE roles AUTO_INCREMENT = 1;
-- ALTER TABLE provinces AUTO_INCREMENT = 1;
-- ALTER TABLE deputis AUTO_INCREMENT = 1;
-- ALTER TABLE direktorats AUTO_INCREMENT = 1;
-- ALTER TABLE satkers AUTO_INCREMENT = 1;
-- ALTER TABLE users AUTO_INCREMENT = 1;
-- ALTER TABLE programs AUTO_INCREMENT = 1;
-- ALTER TABLE outputs AUTO_INCREMENT = 1;
-- ALTER TABLE kegiatans AUTO_INCREMENT = 1;