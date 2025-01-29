create table artist
(
    id integer not null,
    original_name varchar(255) not null,
    overwritten_name varchar(255) not null,
    primary key(id)
);

create table song
(
    id integer not null,
    original_name varchar(255) not null,
    overwritten_name varchar(255) not null,
    primary key(id)
);