create table address
(
    id           bigint auto_increment
        primary key,
    addition     varchar(255) null,
    city         varchar(255) null,
    house_number int          not null,
    postal_code  varchar(255) null,
    street       varchar(255) null
);

create table employee
(
    id         bigint auto_increment
        primary key,
    first_name varchar(255) null,
    last_name  varchar(255) null
);

create table hibernate_sequence
(
    next_val bigint null
);

create table location
(
    id         bigint auto_increment
        primary key,
    name       varchar(255) null,
    address_id bigint       null,
    constraint FKt8psi9b5mkkfc0r9fgptngwhg
        foreign key (address_id) references address (id)
);

create table room
(
    id          bigint auto_increment
        primary key,
    capacity    int    not null,
    floor       int    not null,
    location_id bigint null,
    constraint FKrqejnp96gs9ldf7o6fciylxkt
        foreign key (location_id) references location (id)
);

create table reservation
(
    id                 bigint auto_increment
        primary key,
    date               date         null,
    end_time           time         null,
    active             bit          not null,
    recurrence_pattern varchar(255) not null,
    start_time         time         null,
    workplace_amount   int          not null,
    employee_id        bigint       null,
    room_id            bigint       null,
    constraint FKm8xumi0g23038cw32oiva2ymw
        foreign key (room_id) references room (id),
    constraint FKoq2iacdgt8val8v26jn0iw83q
        foreign key (employee_id) references employee (id)
);

create table users
(
    id          bigint       not null
        primary key,
    password    varchar(255) null,
    role        int          null,
    username    varchar(255) not null,
    employee_id bigint       null,
    constraint UK_r43af9ap4edm43mmtq01oddj6
        unique (username),
    constraint FKfndbe67uw6silwqnlyudtwqmo
        foreign key (employee_id) references employee (id)
);
