INSERT INTO categorias (nombre_categoria) VALUES
('Electrónica'),
('Informática'),
('Accesorios'),
('Audio'),
('Wearables');

-- Insert sample data into proveedores table
INSERT INTO proveedores (nombre_proveedor, contacto) VALUES
('TechCorp', ROW('Juan Proveedor', '12345678A', '123456789', 'juan@techcorp.com')),
('GadgetWorld', ROW('María Suministros', '87654321B', '987654321', 'maria@gadgetworld.com')),
('InnovateTech', ROW('Carlos Innovación', '11223344C', '456789123', 'carlos@innovatetech.com')),
('SmartSolutions', ROW('Ana Soluciones', '44332211D', '321654987', 'ana@smartsolutions.com')),
('FutureDevices', ROW('Luis Futuro', '55667788E', '159753456', 'luis@futuredevices.com'));

-- Insert sample data into productos table
-- Note: We're using the same IDs as in MySQL for consistency
INSERT INTO productos (id_producto, id_proveedor, id_categoria) VALUES
(1, 1, 2), -- Laptop Gamer X1
(2, 2, 1), -- Smartphone Y2
(3, 3, 1), -- Tablet Z3
(4, 4, 5), -- Smart Watch W4
(5, 5, 4); -- Wireless Earbuds E5

-- Insert sample data into almacenes table
INSERT INTO almacenes (nombre_almacen, ubicacion) VALUES
('Almacén Central', 'Madrid'),
('Almacén Norte', 'Barcelona'),
('Almacén Sur', 'Sevilla');

-- Insert sample data into almacenes_productos table
INSERT INTO almacenes_productos (id_almacen, id_producto, cantidad) VALUES
(1, 1, 30),
(1, 2, 50),
(1, 3, 40),
(2, 1, 20),
(2, 4, 100),
(2, 5, 150),
(3, 2, 50),
(3, 3, 35),
(3, 4, 50),
(3, 5, 50);