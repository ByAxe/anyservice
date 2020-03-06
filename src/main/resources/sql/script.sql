CREATE DATABASE anyservice_db;

\c anyservice_db

DROP SCHEMA IF EXISTS anyservice CASCADE;
create table if not exists anyservice;

drop table if exists users cascade;
create table if not exists users
(
    uuid                    uuid primary key,
    description             text,
    contacts                jsonb,
    legal_status            varchar(50),
    is_verified             boolean,
    is_legal_status_verfied boolean
--  profile_photo, documents_photos, portfolio
);


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
--     materials (photos, videos)
);

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

drop table if exists categories;
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
