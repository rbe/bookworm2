CREATE OR REPLACE VIEW v_webhoer AS
  SELECT
      bo.hoerernummer HOENR
    , bob.books_titelnummer TITNR
    , b.aghnummer TIAGNR
    , 'unbekannt01' DLSID -- TODO bo.dlsid
    , DATE_FORMAT(NOW(), '%Y%m%d') ABFRDT
    , DATE_FORMAT(NOW(), '%H%i%s') ABFRZT
    , 0 STATUS -- TODO
    , DATE_FORMAT(bo.ausleihdatum, '%Y%m%d') AUSLDT
    , DATE_FORMAT(bo.ausleihdatum, '%H%i%s') AUSLZT
    , DATE_FORMAT(bo.ausleihdatum + INTERVAL 1 MONTH, '%Y%m%d') RUEGDT -- TODO
    , DATE_FORMAT(bo.ausleihdatum + INTERVAL 1 MONTH, '%H%i%s') RUEGZT -- TODO
  FROM BlistaOrder bo
    INNER JOIN BlistaOrder_Book bob ON bo.id = bob.blistaorder_id
    INNER JOIN Book b ON bob.books_titelnummer = b.titelnummer
    INNER JOIN HoererBuchstamm hbs ON hbs.hoerernummer = bo.hoerernummer
;

DROP PROCEDURE IF EXISTS webhoer_csv_export;
DELIMITER $$
CREATE PROCEDURE webhoer_csv_export()
BEGIN
  SET @filepath = CONCAT('/home/wbh/apache/sites/wbh-online.de/www/app/download/webhoer-', DATE_FORMAT(NOW(), '%Y%m%d'), '.csv');
  SET @sql = CONCAT("SELECT CONCAT(
        LPAD(hoenr, 5, ' ')
        , LPAD(titnr, 6, ' ')
        , LPAD(tiagnr, 13, ' ')
        , LPAD(dlsid, 11, ' ')
        , LPAD(abfrdt, 8, ' ')
        , LPAD(abfrzt, 6, ' ')
        , LPAD(status, 1, ' ')
        , LPAD(ausldt, 8, ' ')
        , LPAD(auslzt, 6, ' ')
        , LPAD(ruegdt, 8, ' ')
        , LPAD(ruegzt, 6, ' ')
    )
  FROM v_webhoer
  INTO OUTFILE '", @filepath, "' LINES TERMINATED BY '\r\n'");
  PREPARE stmt FROM @sql;
  EXECUTE stmt;
  DEALLOCATE PREPARE stmt;
END $$
DELIMITER ;

DROP EVENT webhoer_csv;
DELIMITER $$
CREATE EVENT webhoer_csv
  ON SCHEDULE
    EVERY 1 DAY
    STARTS '2016-07-11 00:01:00' + INTERVAL 1 DAY
DO BEGIN
  CALL webhoer_csv_export();
END $$
DELIMITER ;
