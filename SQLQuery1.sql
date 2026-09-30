Create database Hardware_web_shop

Create table Type(
	id int primary key,
	name varchar(50) not null 
)

create table Hardware(
	id int identity(1,1) primary key,
	name varchar(100) not null,
	code varchar(100) not null,
	price decimal(10,2) not null,
	typeId int not null,
	stock int not null,

	foreign key (typeId) references Type(id)
)

INSERT INTO Type (id, name)
VALUES
(1, 'CPU'),
(2, 'GPU'),
(3, 'MBO'),
(4, 'RAM'),
(5, 'STORAGE'),
(6, 'OTHER')

INSERT INTO Hardware (name, code, price, typeId, stock)
VALUES
    ('CPU-1','1111',100.0,1,10),
    ('CPU-2','2222',200.0,1,11)

SELECT * FROM Type

SELECT * FROM Hardware
/* DODANO OGRANICENJE */
ALTER TABLE Hardware
ADD CONSTRAINT UQ_Hardware_Code UNIQUE (code);