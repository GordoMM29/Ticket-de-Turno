DROP DATABASE IF EXISTS ticket_turno;
CREATE DATABASE ticket_turno CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ticket_turno;

-- CATALOGOS

CREATE TABLE municipio (
    id_municipio INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(60) NOT NULL UNIQUE,
    ultimo_turno INT NOT NULL DEFAULT 0   -- contador de turnos por municipio
) ENGINE=InnoDB;

CREATE TABLE nivel_educativo (
    id_nivel INT AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(40) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE asunto (
    id_asunto   INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE estatus (
    id_estatus INT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(20) NOT NULL UNIQUE
) ENGINE=InnoDB;

--  ADMINISTRADORES

CREATE TABLE administrador (
    id_admin   INT AUTO_INCREMENT PRIMARY KEY,
    usuario    VARCHAR(30) NOT NULL UNIQUE,
    contrasena CHAR(64) NOT NULL,
    nombre     VARCHAR(80) NOT NULL
) ENGINE=InnoDB;

--  SOLICITUD 

CREATE TABLE solicitud (
    curp             CHAR(18) PRIMARY KEY,
    nombre_tramitante VARCHAR(100) NOT NULL,   -- quien realiza el tramite
    nombre_alumno    VARCHAR(60) NOT NULL,
    apellido_paterno VARCHAR(40) NOT NULL,
    apellido_materno VARCHAR(40),
    telefono         VARCHAR(15) NOT NULL,
    correo           VARCHAR(80) NOT NULL,
    id_nivel         INT NOT NULL,
    id_municipio     INT NOT NULL,
    id_asunto        INT NOT NULL,
    turno            INT NOT NULL,
    id_estatus       INT NOT NULL DEFAULT 1,   -- 1 = Pendiente
    fecha_registro   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sol_nivel     FOREIGN KEY (id_nivel)     REFERENCES nivel_educativo(id_nivel),
    CONSTRAINT fk_sol_municipio FOREIGN KEY (id_municipio) REFERENCES municipio(id_municipio),
    CONSTRAINT fk_sol_asunto    FOREIGN KEY (id_asunto)    REFERENCES asunto(id_asunto),
    CONSTRAINT fk_sol_estatus   FOREIGN KEY (id_estatus)   REFERENCES estatus(id_estatus),
    -- el turno no se repite dentro de un mismo municipio
    CONSTRAINT uq_turno_municipio UNIQUE (id_municipio, turno)
) ENGINE=INNODB;



-- DATOS INICIALES

INSERT INTO estatus (nombre) VALUES ('Pendiente'), ('Resuelto');

INSERT INTO nivel_educativo (nombre) VALUES
 ('Preescolar'), ('Primaria'), ('Secundaria'), ('Bachillerato');

INSERT INTO asunto (descripcion) VALUES
 ('Inscripcion'), ('Reinscripcion'), ('Cambio de escuela'),
 ('Constancia de estudios'), ('Baja de alumno'), ('Correccion de datos'),
 ('Certificado'), ('Otro');

INSERT INTO municipio (nombre) VALUES
 ('Abasolo'), ('Acuña'), ('Allende'), ('Arteaga'), ('Candela'), ('Castaños'),
 ('Cuatro Ciénegas'), ('Escobedo'), ('Francisco I. Madero'), ('Frontera'),
 ('General Cepeda'), ('Guerrero'), ('Hidalgo'), ('Jiménez'), ('Juárez'),
 ('Lamadrid'), ('Matamoros'), ('Monclova'), ('Morelos'), ('Múzquiz'),
 ('Nadadores'), ('Nava'), ('Ocampo'), ('Parras'), ('Piedras Negras'),
 ('Progreso'), ('Ramos Arizpe'), ('Sabinas'), ('Sacramento'), ('Saltillo'),
 ('San Buenaventura'), ('San Juan de Sabinas'), ('San Pedro'),
 ('Sierra Mojada'), ('Torreón'), ('Viesca'), ('Villa Unión'), ('Zaragoza');

-- Usuario admin de prueba: admin / admin123
INSERT INTO administrador (usuario, contrasena, nombre)
VALUES ('admin', SHA2('admin123', 256), 'Administrador General');