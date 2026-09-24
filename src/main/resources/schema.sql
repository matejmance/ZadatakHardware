CREATE TABLE Hardware
(
    id IDENTITY PRIMARY KEY,
    name varchar(100) NOT NULL,
    code varchar(100) NOT NULL ,
    price decimal(10 , 2) NOT NULL,
    type varchar(100) NOT NULL,
    stock INT NOT NULL
);