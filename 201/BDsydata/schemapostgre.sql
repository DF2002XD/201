-- Create custom type for contact information
CREATE TYPE contacto_info AS (
    nombre_contacto VARCHAR(255),
    nif VARCHAR(20),
    telefono VARCHAR(20),
    email VARCHAR(255)
);

-- Create categorias table
CREATE TABLE categorias (
    id_categoria SERIAL PRIMARY KEY,
    nombre_categoria VARCHAR(255) NOT NULL
);

-- Create proveedores table with composite contact type
CREATE TABLE proveedores (
    id_proveedor SERIAL PRIMARY KEY,
    nombre_proveedor VARCHAR(255) NOT NULL,
    contacto contacto_info NOT NULL
);

-- Create productos table
CREATE TABLE productos (
    id_producto SERIAL PRIMARY KEY,
    id_proveedor INTEGER NOT NULL REFERENCES proveedores(id_proveedor),
    id_categoria INTEGER NOT NULL REFERENCES categorias(id_categoria)
);

-- Create almacenes table
CREATE TABLE almacenes (
    id_almacen SERIAL PRIMARY KEY,
    nombre_almacen VARCHAR(255) NOT NULL,
    ubicacion VARCHAR(255) NOT NULL
);

-- Create junction table for almacenes_productos
CREATE TABLE almacenes_productos (
    id_almacen INTEGER NOT NULL REFERENCES almacenes(id_almacen),
    id_producto INTEGER NOT NULL REFERENCES productos(id_producto),
    cantidad INTEGER NOT NULL CHECK (cantidad >= 0),
    PRIMARY KEY (id_almacen, id_producto)
);

-- Create indexes for better performance
CREATE INDEX idx_productos_proveedor ON productos(id_proveedor);
CREATE INDEX idx_productos_categoria ON productos(id_categoria);
CREATE INDEX idx_almacenes_productos_producto ON almacenes_productos(id_producto);
CREATE INDEX idx_almacenes_productos_almacen ON almacenes_productos(id_almacen);

-- Add some helpful constraints
ALTER TABLE almacenes_productos
    ADD CONSTRAINT cantidad_positiva CHECK (cantidad >= 0);

-- Create a function to validate email in contacto_info
CREATE OR REPLACE FUNCTION validate_contacto_email() 
RETURNS trigger AS $$
BEGIN
    IF (NEW.contacto).email !~ '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$' THEN
        RAISE EXCEPTION 'Invalid email format in contacto';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger to validate email before insert/update
CREATE TRIGGER check_contacto_email
    BEFORE INSERT OR UPDATE ON proveedores
    FOR EACH ROW
    EXECUTE FUNCTION validate_contacto_email();