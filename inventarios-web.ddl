
drop database inventarios;
create database inventarios;

use inventarios;

CREATE TABLE Compania (compania varchar(20) not null, razonsocial varchar(120), 
	PRIMARY KEY(compania)
	) ENGINE=InnoDB;

CREATE TABLE Producto (
	compania varchar(20) not null, clave varchar(20) not null, descripcion varchar(260), precio double, numero varchar(20),
	PRIMARY KEY (compania, clave)
) ENGINE=InnoDB;

CREATE INDEX ProductoINDX1 on Producto(descripcion);

CREATE TABLE Codigo (
	compania varchar(20) not null, codigo varchar(20) not null, clave varchar(20) not null, piezas int not null,
	PRIMARY KEY (compania, codigo)
) ENGINE=InnoDB;

CREATE TABLE Aplicacion (
	compania varchar(20) not null, codigo varchar(20) not null, aplicacion varchar(200)
) ENGINE=InnoDB;
CREATE INDEX Aplicacion_INDX1 ON Aplicacion (compania, codigo, aplicacion);

CREATE TABLE Perfil (perfil varchar(5) not null, descripcion varchar(80) not null,
        PRIMARY KEY (perfil)
        ) ENGINE=InnoDB;

CREATE TABLE Permiso (permiso int not null, nombre varchar(80) not null,
        PRIMARY KEY (permiso)
        ) ENGINE=InnoDB;

CREATE TABLE PermisoPerfil (permiso int not null, perfil varchar(5) not null,
        PRIMARY KEY (permiso, perfil),
        FOREIGN KEY (perfil) REFERENCES Perfil (perfil)
        ) ENGINE=InnoDB;

CREATE TABLE Usuario (usuario varchar(30) not null,
        password varchar(20) not null, perfil varchar(5) not null, estado varchar(2), nombre varchar(80) not null, email varchar(60) not null, 
        PRIMARY KEY (usuario),
        FOREIGN KEY (perfil) REFERENCES Perfil (perfil)
        ) ENGINE=InnoDB;

CREATE TABLE Folio ( 
	compania varchar(20) not null, tipo varchar(20) not null, folio int,
	PRIMARY KEY (compania, tipo) 
) ENGINE=InnoDB;

CREATE TABLE Linea ( 
	compania varchar(20) not null, linea varchar(5) not null, descripcion varchar(20) not null, status varchar(1) not null,
	PRIMARY KEY (compania, linea) 
) ENGINE=InnoDB;

CREATE TABLE Ubicacion (
	compania varchar(20) not null, ubicacion varchar(20) not null, descripcion varchar(40) not null, 
	PRIMARY KEY (compania, ubicacion)
) ENGINE=InnoDB;

CREATE TABLE Inventario ( 
	flinventario int not null auto_increment, 
	compania varchar(20) not null, almacen int not null, descripcion varchar(40) not null,
	feinicio datetime, fetermino datetime, status varchar(2), fase varchar(3), fefase datetime,
	feprimerconteo datetime, fesegundoconteo datetime, fetercerconteo datetime, 
	PRIMARY KEY (flinventario) 
) ENGINE=InnoDB;

CREATE INDEX InventarioINDX1 on Inventario(compania);

CREATE TABLE InventarioDetalle ( 
	fldinventario int not null auto_increment, flinventario int not null, 
	clave varchar(20) not null, descripcion varchar(260) not null, precio double not null, existencia double not null,
	PRIMARY KEY (fldinventario)
) ENGINE=InnoDB;

CREATE INDEX InventarioDetalleINDX1 on InventarioDetalle(flinventario);
CREATE INDEX InventarioDetalleINDX2 on InventarioDetalle(flinventario, clave);

CREATE TABLE InventarioDetalleUbicacion ( 
	flduinventario int not null auto_increment, fldinventario int not null, flinventario int not null, 
	clave varchar(20) not null, descripcion varchar(260) not null, precio double not null, ubicacion varchar(20) not null, existencia double not null,
	PRIMARY KEY (flduinventario)
) ENGINE=InnoDB;

CREATE INDEX InventarioDetalleUbicacionINDX1 on InventarioDetalleUbicacion(flinventario, clave, ubicacion);

CREATE TABLE InventarioConteo (
	fldinventario int not null, flinventario int not null, 
	clave varchar(20) not null, descripcion varchar(260) not null, precio double not null, existencia double not null, existenciac double not null,
	existencia1 double not null, conteos1 int not null, 
	existencia2 double not null, conteos2 int not null, 
	existencia3 double not null, conteos3 int not null,
	status varchar(2) not null, terminal varchar(20), 
	PRIMARY KEY (fldinventario)
) ENGINE=InnoDB;

CREATE INDEX InventarioConteoINDX1 on InventarioConteo(flinventario);
CREATE INDEX InventarioConteoINDX2 on InventarioConteo(flinventario, clave);
CREATE INDEX InventarioConteoINDX3 on InventarioConteo(flinventario, terminal);

CREATE TABLE InventarioConteoUbicacion (
	flduinventario int not null auto_increment, fldinventario int not null, flinventario int not null, 
	clave varchar(20) not null, descripcion varchar(260) not null, precio double not null, ubicacion varchar(20) not null, 
	existenciainicial double not null, existencia double not null, terminal varchar(20) not null, usuario varchar(30) not null,
       	PRIMARY KEY (flduinventario)
) ENGINE=InnoDB;

CREATE INDEX InventarioConteoUbicacionINDX1 on InventarioConteoUbicacion(flinventario, fldinventario);
CREATE INDEX InventarioConteoUbicacionINDX2 on InventarioConteoUbicacion(flinventario, clave, ubicacion);


CREATE TABLE InventarioCaptura (
	flcapturainventario int not null auto_increment, fechatransmision datetime not null, flinventario int not null, fase varchar(3),
	compania varchar(20) not null, terminal varchar(20) not null, usuario varchar(30) not null, folio int not null, status varchar(2),
	PRIMARY KEY (flcapturainventario)
) ENGINE=InnoDB;

CREATE INDEX InventarioCapturaINDX1 ON InventarioCaptura(flinventario, compania, usuario);

CREATE TABLE InventarioCapturaDetalle (
	fldcapturainventario int not null auto_increment, flcapturainventario int not null, flinventario int not null, 
	ubicacion varchar(20) not null, clave varchar(20) not null, ean varchar(20) not null, descripcion varchar(260), cantidad double not null, 
	PRIMARY KEY (fldcapturainventario)
) ENGINE=InnoDB;

CREATE INDEX InventarioCapturaDetalleINDX1 ON InventarioCapturaDetalle(flcapturainventario);
CREATE INDEX InventarioCapturaDetalleINDX2 ON InventarioCapturaDetalle(flinventario, ubicacion, clave);

CREATE TABLE Parametro ( 
	compania varchar(20) not null, parametro varchar(30) not null, valor varchar(30), activo int, 
	PRIMARY KEY (compania, parametro) 
	) ENGINE=InnoDB;

CREATE TABLE ParametroUsuario (  
	compania varchar(20) not null, usuario varchar(30) not null, parametro varchar(30), valor varchar(30), activo int, 
	PRIMARY KEY (compania, usuario, parametro) 
	) ENGINE=InnoDB;

CREATE TABLE SentenciaSQL (
	compania varchar(20) not null, usuario varchar(30) not null, orden int, sentencia varchar(128), 
	PRIMARY KEY (compania, usuario, orden) 
) ENGINE=InnoDB;

CREATE TABLE VersionUsuario (  
	compania varchar(20) not null, usuario varchar(30) not null, version varchar(20), fecha varchar(10),
	PRIMARY KEY (compania, usuario) 
) ENGINE=InnoDB;

CREATE TABLE VersionSQL (
	version varchar(20) not null, orden int not null, sentencia varchar(128),
	PRIMARY KEY (version, orden) 
) ENGINE=InnoDB;

CREATE TABLE Token(compania varchar(20) not null, usuario varchar(30) not null, token varchar(255), fecha datetime,
	PRIMARY KEY(compania, usuario)
) ENGINE=InnoDB;

CREATE TABLE Dispositivo(compania varchar(20) not null, usuario varchar(30) not null, serie varchar(30) not null, numero varchar(20), sim varchar(30), 
	imei varchar(30), version varchar(10), fecha datetime,
	PRIMARY KEY(compania, usuario, serie)
) ENGINE=InnoDB;

CREATE TABLE InformacionUsuario(compania varchar(20) not null, usuario varchar(30) not null, password varchar(20), feultsincronizacion datetime,
	PRIMARY KEY(compania, usuario)
) ENGINE=InnoDB;

--
--
--

insert into VersionUsuario values ('01','*','0.0.1','2018-10-15');
insert into VersionSQL values ('0.0.1',0,'VER 0.0.1');

insert into Compania values ('01', 'COMPAÑIA UNO');

insert into Perfil values ('99', 'ADMINISTRADOR');
insert into Usuario values ('joelbecerram@gmail.com', 'mikasa110175', '99', 'A', 'Joel Becerra', 'joelbecerram@gmail.com');


truncate table Permiso;

set @row_number = 0;

insert into Permiso values ((@row_number:=@row_number + 1), 'catalogoPermisos');
insert into Permiso values ((@row_number:=@row_number + 1), 'catalogoPerfiles');
insert into Permiso values ((@row_number:=@row_number + 1), 'catalogoUsuarios');
insert into Permiso values ((@row_number:=@row_number + 1), 'catalogoCompanias');
insert into Permiso values ((@row_number:=@row_number + 1), 'inventarios');
insert into Permiso values ((@row_number:=@row_number + 1), 'analisisInventarios');

truncate table PermisoPerfil;

insert into PermisoPerfil 
select permiso, '99' from Permiso;


