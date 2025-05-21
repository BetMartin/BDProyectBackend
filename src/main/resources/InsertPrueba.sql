INSERT INTO province (name) VALUES 
('Buenos Aires'), 
('Córdoba'), 
('Santa Fe'), 
('Mendoza'), 
('Salta');

INSERT INTO address (apartment, street, street_number, province_id) VALUES 
('A', 'Av. Siempre Viva', 742, 1), 
('B', 'Calle Falsa', 123, 2), 
('sinDpto', 'San Martín', 456, 3), 
('C', 'Belgrano', 789, 4), 
('sinDpto', 'Mitre', 101, 5);

INSERT INTO phone (number) VALUES 
('3512345678'), 
('3812345678'), 
('3412345678'), 
('2612345678'), 
('3871234567');

INSERT INTO user (username, password) VALUES 
('admin1', 'pass1'), 
('user2', 'pass2'), 
('visor3', 'pass3'), 
('admin4', 'pass4'), 
('user5', 'pass5');

INSERT INTO person (first_name, last_name, rol, address_id, phone_id, user_id) VALUES 
('Juan', 'Pérez', 'ADMIN', 1, 1, 1), 
('Ana', 'Gómez', 'VISOR', 2, 2, 2), 
('Luis', 'Martínez', 'ADMIN', 3, 3, 3), 
('María', 'Fernández', 'VISOR', 4, 4, 4), 
('Pedro', 'López', 'VISOR', 5, 5, 5);

INSERT INTO product_category (name) VALUES 
('running'), 
('urban'), 
('outdoor'), 
('trekking'), 
('skater');

INSERT INTO product (brand, description, image, model, product, product_category_id) VALUES 
('Nike', 'Zapatilla deportiva', 'nike.jpg', 'AirMax', 'Zapatilla', 1), 
('Adidas', 'Remera de algodón', 'adidas.jpg', 'FitDry', 'Remera', 2), 
('Puma', 'Gorra unisex', 'puma.jpg', 'CapOne', 'Gorra', 3), 
('Wilson', 'Pelota de tenis', 'wilson.jpg', 'Tour', 'Pelota', 4), 
('Sony', 'Auriculares inalámbricos', 'sony.jpg', 'WH1000XM4', 'Auriculares', 5);

INSERT INTO size (size_number) VALUES 
(38), (39), (40), (41), (42);

INSERT INTO product_sizes (product_id, size_id) VALUES 
(1, 1), 
(1, 2), 
(2, 3), 
(2, 4), 
(3, 5);

INSERT INTO product_stock (date, stock, product_size_id) VALUES 
('2024-01-01', 10, 1), 
('2024-01-02', 5, 2), 
('2024-01-03', 15, 3), 
('2024-01-04', 8, 4), 
('2024-01-05', 20, 5);

INSERT INTO invoice (date, persona_id) VALUES 
('2024-02-01', 1), 
('2024-02-02', 2), 
('2024-02-03', 3), 
('2024-02-04', 4), 
('2024-02-05', 5);

INSERT INTO invoice_detail (quantity, invoice_id, product_size_id) VALUES 
(1, 1, 1), 
(2, 1, 2), 
(1, 2, 3), 
(3, 3, 4), 
(2, 4, 5);

INSERT INTO historical_price (date, price, product_id) VALUES 
('2023-12-01', 15000.00, 1), 
('2023-12-05', 8000.00, 2), 
('2023-12-10', 3000.00, 3), 
('2023-12-15', 2000.00, 4), 
('2023-12-20', 45000.00, 5);



