create database anyservice_test_db;

\c anyservice_db

drop schema if exists anyservice_test cascade;
create schema if not exists anyservice_test;

comment on schema anyservice_test is 'Main schema for Testing AnyService';

-- FILE_DESCRIPTION
drop table if exists file_description cascade;
create table file_description
(
    uuid      uuid         not null primary key,
    name      varchar(255) not null,
    size      bigint,
    extension varchar(50),
    state     varchar(50),
    type      varchar(50)  not null,
    dt_create timestamptz  not null
);
comment on table file_description is 'Storage of files metadata';
comment on column file_description.uuid is 'Primary key of a file';
comment on column file_description.name is 'Original file name';
comment on column file_description.size is 'Size in bytes';
comment on column file_description.extension is 'File extension';
comment on column file_description.state is 'File state (LOADING or other)';
comment on column file_description.type is 'Domain, that files belongs to (profile photo etc.)';
comment on column file_description.dt_create is 'Date and time of file creation and also works as Version of a file';

drop table if exists countries cascade;
create table if not exists countries
(
    uuid    uuid primary key default (md5(((random())::text || (clock_timestamp())::text)))::uuid,
    country varchar(100) not null unique,
    alpha2  char(2)      not null unique,
    alpha3  char(3)      not null unique,
    number  smallint     not null unique
);

comment on table countries is 'Dictionary of allowed countries';
comment on column countries.uuid is 'Primary key of a country';
comment on column countries.country is 'Country name';
comment on column countries.alpha2 is 'Two letter code of a country';
comment on column countries.alpha2 is 'Three letter code of a country';
comment on column countries.number is 'Unique number of a country';

drop table if exists users cascade;
create table if not exists users
(
    uuid                     uuid primary key,
    dt_create                timestamptz not null default now(),
    dt_update                timestamptz not null default now(),
    password_update_date     timestamptz not null default now(),
    user_name                varchar(50) not null unique,
    initials                 jsonb       not null,
    password                 varchar     not null,
    description              text,
    country                  uuid references countries,
    addresses                jsonb,
    state                    varchar(50) not null,
    role                     varchar(50) not null,
    contacts                 jsonb,
    legal_status             varchar(50),
    is_verified              boolean,
    is_legal_status_verified boolean,
    photo                    uuid references file_description
);

comment on table users is 'All users of an application';
comment on column users.uuid is 'Primary key of a user';
comment on column users.dt_create is 'Date create of a user';
comment on column users.dt_update is 'Last date update of a user and also works as Version of a user';
comment on column users.password_update_date is 'Password update date';
comment on column users.user_name is 'User name of a user';
comment on column users.initials is 'First, second and last name of a user';
comment on column users.password is 'User password hash';
comment on column users.description is 'Description of a user';
comment on column users.country is 'Country where user registered in';
comment on column users.addresses is 'User addresses, listed as [title:address] pairs';
comment on column users.state is 'State of a user (ACTIVE, BLOCKED etc.)';
comment on column users.role is 'Role of user (USER, ADMIN etc.)';
comment on column users.contacts is 'All contacts of a user';
comment on column users.is_verified is 'Is user verified in application?';
comment on column users.legal_status is 'User legal status (LLC etc.)';
comment on column users.is_legal_status_verified is 'Is user status verified in application?';
comment on column users.photo is 'User profile photo';

-- users_countries
drop table if exists users_countries;
create table users_countries
(
    user_uuid    uuid references users (uuid)
        on update cascade
        on delete cascade,
    country_uuid uuid references countries (uuid)
        on update cascade
        on delete cascade,
    primary key (user_uuid, country_uuid)
);

comment on table users_countries is 'Contains data of countries where user can provide its services';

-- users_files
drop table if exists users_files;
create table users_files
(
    user_uuid uuid references users (uuid)
        on update cascade
        on delete cascade,
    file_uuid uuid references file_description (uuid)
        on update cascade
        on delete cascade,
    primary key (user_uuid, file_uuid)
);

comment on table users_files is 'Contains data of two "virtual columns": documents_photos and portfolio';

drop table if exists orders cascade;
create table if not exists orders
(
    uuid              uuid primary key,
    dt_create         timestamptz           not null default now(),
    dt_update         timestamptz           not null default now(),
    customer_uuid     uuid references users not null,
    executors         jsonb,
    headline          varchar               not null,
    description       text                  not null,
    location          jsonb                 not null,
    deadline          timestamptz,
    price             decimal,
    phone             varchar(50),
    selected_executor uuid references users
);

-- users_files
drop table if exists orders_files;
create table orders_files
(
    uuid       UUID primary key,
    order_uuid UUID references orders (uuid)
        on update cascade
        on delete cascade,
    file_uuid  UUID references file_description (uuid)
        on update cascade
        on delete cascade,
    dt_create  timestamptz,
    dt_update  timestamptz
);

comment on table orders_files is 'Contains any additional materials for an order (videos and photos)';

drop table if exists chats cascade;
create table if not exists chats
(
    uuid     uuid primary key,
    messages jsonb
);

drop table if exists reviews cascade;
create table if not exists reviews
(
    uuid    uuid primary key,
    author  uuid references users not null,
    rating  decimal               not null,
    header  varchar,
    body    text,
    payload jsonb
);

drop table if exists categories cascade;
create table if not exists categories
(
    uuid        uuid primary key,
    parent_uuid uuid references categories (uuid),
    title       varchar not null,
    description text
);

drop table if exists orders_categories;
create table if not exists orders_categories
(
    order_uuid    uuid references orders,
    category_uuid uuid references categories
);


------------------------INSERTS------------------------------------
INSERT INTO countries (country, alpha2, alpha3, number)
VALUES ('Poland', 'PL', 'POL', 616),
       ('Lithuania', 'LT', 'LTU', 440),
       ('Latvia', 'LV', 'LVA', 428),
       ('Germany', 'DE', 'DEU', 276),
       ('Switzerland', 'CH', 'CHE', 756),
       ('France', 'FR', 'FRA', 250),
       ('Italy', 'IT', 'ITA', 380),
       ('Spain', 'ES', 'ESP', 724),
       ('Sweden', 'SE', 'SWE', 752);
