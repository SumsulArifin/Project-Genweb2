/* Queries for Quintor Amersfoort */
INSERT INTO address (street, house_number, postal_code, city, addition)
VALUES ('Maanlander', '14', '3824 MP', 'Amersfoort', 'm');

INSERT INTO location (name, address_id)
VALUES ('Quintor Amersfoort', 1);

INSERT INTO room(floor, location_id, capacity)
VALUES (0, 1, 6),
       (0, 1, 8);

/* Queries for Quintor Den Bosch */
INSERT INTO address (street, house_number, postal_code, city, addition)
VALUES ('Havensingel', '1', '5211 TX', 'Den Bosch', '');

INSERT INTO location (name, address_id)
VALUES ('Quintor Den Bosch', 2);

INSERT INTO room(floor, location_id, capacity)
VALUES (0, 2, 4),
       (0, 2, 4),
       (1, 2, 8);

/* Queries for Quintor Deventer */
INSERT INTO address (street, house_number, postal_code, city, addition)
VALUES ('Zutphenseweg', '6', '7418 AJ', 'Deventer', '');

INSERT INTO location (name, address_id)
VALUES ('Quintor Deventer', 3);

INSERT INTO room(floor, location_id, capacity)
VALUES (0, 3, 2),
       (1, 3, 4);

/* Queries for Quintor Den Haag */
INSERT INTO address (street, house_number, postal_code, city, addition)
VALUES ('Lange Vijverberg', '4', '2513 AC', 'Den Haag', '-5');

INSERT INTO location (name, address_id)
VALUES ('Quintor Den Haag', 4);

INSERT INTO room(floor, location_id, capacity)
VALUES (1, 4, 10),
       (2, 4, 12);

/* Queries for Quintor Groningen */
INSERT INTO address (street, house_number, postal_code, city, addition)
VALUES ('Ubbo Emmiussingel', '112', '9711 BK', 'Groningen', '');

INSERT INTO location (name, address_id)
VALUES ('Quintor Groningen', 5);

INSERT INTO room(floor, location_id, capacity)
VALUES (3, 5, 24),
       (-1, 5, 6);