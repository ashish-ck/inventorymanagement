--GET ALL CHILDREN FUNCTION
USE `drivojoy_inventory_prod`#
DROP function IF EXISTS `getAllChildCategories`#
CREATE FUNCTION `getAllChildCategories`(in_id INTEGER) RETURNS varchar(1024) CHARSET utf8
    DETERMINISTIC
BEGIN 	
    DECLARE rv,q,queue,queue_children VARCHAR(1024);
    DECLARE queue_length,front_id,pos INT;
    SET rv = '';
    SET queue = in_id;
    SET queue_length = 1;
	SET @@sql_mode = '';
    WHILE queue_length > 0 DO
        SET front_id = FORMAT(queue,0);
        IF queue_length = 1 THEN
            SET queue = '';
        ELSE
            SET pos = LOCATE(',',queue) + 1;
            SET q = SUBSTR(queue,pos);
            SET queue = q;
        END IF;
        SET queue_length = queue_length - 1;

        SELECT IFNULL(qc,'') INTO queue_children
        FROM (SELECT GROUP_CONCAT(id) qc
        FROM `item_category` WHERE `parent_category` = front_id) A;

        IF LENGTH(queue_children) = 0 THEN
            IF LENGTH(queue) = 0 THEN
                SET queue_length = 0;
            END IF;
        ELSE
            IF LENGTH(rv) = 0 THEN
                SET rv = queue_children;
            ELSE
                SET rv = CONCAT(rv,',',queue_children);
            END IF;
            IF LENGTH(queue) = 0 THEN
                SET queue = queue_children;
            ELSE
               SET queue = CONCAT(queue,',',queue_children);
            END IF;
            SET queue_length = (LENGTH(queue) - LENGTH(REPLACE(queue,',',''))) + 1;
        END IF;
    END WHILE;
    RETURN rv;
END#

--GET ITEMS BY CATEGORY PROCEDURE
DROP procedure IF EXISTS `getItemsByCategory`#
CREATE PROCEDURE `getItemsByCategory`(IN in_id VARCHAR(1024))
BEGIN
SET @listOfCategories = `getAllChildCategories`(in_id);
SET @categories = CONCAT(@listOfCategories,',',in_id);
SELECT item.*, IFNULL(SUM(item_count.quantity_in_hand), 0) as quantity_in_hand  FROM `item` LEFT JOIN `item_count` ON item.id = item_count.item_id WHERE FIND_IN_SET (category, @categories) GROUP BY item.id;
END#


--UPDATE ITEM STATUS AND COUNT PROCEDURE
DROP procedure IF EXISTS `updateItemStatusAndCount`#
CREATE PROCEDURE `updateItemStatusAndCount`(IN barcodes VARCHAR(1000), IN status_to_set INTEGER, IN voucherRef VARCHAR(1000), IN quantity INTEGER)
BEGIN
	DECLARE current_item_id INTEGER DEFAULT 0;
    DECLARE current_warehouse INTEGER DEFAULT 0;
    DECLARE current_status INTEGER DEFAULT 0;
    DECLARE done INTEGER DEFAULT 0;
    DECLARE updateCountCursor CURSOR FOR SELECT item_id FROM `item_details` WHERE barcode IN (barcodes);
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
    SET @@sql_safe_updates = 0;
    OPEN updateCountCursor;
    update_status: LOOP
        FETCH updateCountCursor INTO current_item_id;
		IF done = 1 THEN
			LEAVE update_status;
		END IF;
		SELECT warehouse INTO current_warehouse FROM `item_details` WHERE item_id = current_item_id AND barcode IN (barcodes);
        SELECT status INTO current_status FROM `item_details` WHERE item_id = current_item_id AND barcode IN (barcodes);
		CASE status_to_set
			WHEN 1 THEN 
				IF (current_status = 2) THEN
					UPDATE `item_count` SET quantity_reserved = quantity_reserved - quantity, quantity_available = quantity_available + quantity 
						WHERE item_id = current_item_id AND warehouse = current_warehouse;
				ELSEIF (current_status = 3) THEN
					UPDATE `item_count` SET quantity_issued = quantity_issued - quantity, quantity_in_hand = quantity_in_hand + quantity, quantity_available = quantity_available + quantity 
						WHERE item_id = current_item_id AND warehouse = current_warehouse;
				ELSEIF (current_status = 4) THEN
					UPDATE `item_count` SET quantity_restock = quantity_restock - quantity, quantity_available = quantity_available + quantity, quantity_in_hand = quantity_in_hand + quantity
						WHERE item_id = current_item_id AND warehouse = current_warehouse;
				END IF;
			WHEN 2 THEN UPDATE `item_count` SET quantity_available = quantity_available - quantity, quantity_reserved = quantity_reserved + quantity 
					WHERE item_id = current_item_id AND warehouse = current_warehouse;
			WHEN 3 THEN 
				IF (current_status = 1) THEN
					UPDATE `item_count` SET quantity_available = quantity_available - quantity, quantity_in_hand = quantity_in_hand - quantity, quantity_issued = quantity_issued + quantity 
						WHERE item_id = current_item_id AND warehouse = current_warehouse;                    
				ELSEIF (current_status = 2) THEN
					UPDATE `item_count` SET quantity_reserved = quantity_reserved - quantity, quantity_in_hand = quantity_in_hand - quantity, quantity_issued = quantity_issued + quantity 
						WHERE item_id = current_item_id AND warehouse = current_warehouse;
				END IF;
			WHEN 4 THEN 
				IF (current_status = 2) THEN
					UPDATE `item_count` SET quantity_reserved = quantity_reserved - quantity, quantity_available = quantity_available + quantity 
						WHERE item_id = current_item_id AND warehouse = current_warehouse;
				ELSEIF (current_status = 3) THEN
					UPDATE `item_count` SET quantity_issued = quantity_issued - quantity, quantity_restock = quantity_restock + quantity 
						WHERE item_id = current_item_id AND warehouse = current_warehouse;
				END IF;
			WHEN 6 THEN 
				IF (current_status = 3) THEN
					UPDATE `item_count` SET quantity_issued = quantity_issued - quantity, quantity_invoiced = quantity_invoiced + quantity 
						WHERE item_id = current_item_id AND warehouse = current_warehouse;
				END IF;
		END CASE;
    END LOOP update_status;
    UPDATE `item_details` SET status = status_to_set, assigned_sales_order = voucherRef WHERE barcode IN (barcodes);
END#


