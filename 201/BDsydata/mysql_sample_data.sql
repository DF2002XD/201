INSERT INTO productos (nombre_producto, precio, stock) VALUES
('Laptop Gamer X1', 1299.99, 50),
('Smartphone Y2', 699.99, 100),
('Tablet Z3', 399.99, 75),
('Smart Watch W4', 199.99, 150),
('Wireless Earbuds E5', 129.99, 200);

-- Insert sample data into usuarios table
INSERT INTO usuarios (nombre, email, ano_nacimiento) VALUES
('Juan Pérez', 'juan.perez@email.com', 1985),
('María García', 'maria.garcia@email.com', 1990),
('Carlos Rodríguez', 'carlos.rodriguez@email.com', 1988),
('Ana Martínez', 'ana.martinez@email.com', 1992),
('Luis Sánchez', 'luis.sanchez@email.com', 1987);

-- Insert sample data into pedidos table
INSERT INTO pedidos (id_usuario, fecha_pedido) VALUES
(1, '2023-01-15 10:30:00'),
(2, '2023-02-20 14:45:00'),
(3, '2023-03-25 09:15:00'),
(4, '2023-04-10 16:20:00'),
(5, '2023-05-05 11:00:00');

-- Insert sample data into pedidos_productos table
INSERT INTO pedidos_productos (id_pedido, id_producto, cantidad) VALUES
(1, 1, 1),
(1, 3, 1),
(2, 2, 2),
(3, 4, 1),
(3, 5, 2),
(4, 1, 1),
(4, 5, 1),
(5, 2, 1),
(5, 3, 1),
(5, 4, 1);