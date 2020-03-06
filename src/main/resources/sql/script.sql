CREATE DATABASE anyservice_db;

\c anyservice_db

DROP SCHEMA IF EXISTS anyservice CASCADE;
create table if not exists anyservice;

-- FILE_DESCRIPTION
DROP TABLE IF EXISTS file_description;
CREATE TABLE file_description
(
    uuid      uuid                        NOT NULL PRIMARY KEY,
    name      character(255)              NOT NULL,
    md5       character(64),
    size      bigint,
    extension character(50),
    dt_create timestamp without time zone NOT NULL,
    state     character(50),
    summary   character(255),
    storage   jsonb
);
COMMENT ON TABLE file_description IS 'Таблица хранения метаданных файлов';
COMMENT ON COLUMN file_description.uuid IS 'Первичный ключ и суррогатный идентификатор файла';
COMMENT ON COLUMN file_description.name IS 'Описание файла';
COMMENT ON COLUMN file_description.md5 IS 'Хеш файла по алгоритму MD5';
COMMENT ON COLUMN file_description.size IS 'Размер файла в байтах';
COMMENT ON COLUMN file_description.extension IS 'Расширение файла';
COMMENT ON COLUMN file_description.dt_create IS 'Дата/время создания файла';
COMMENT ON COLUMN file_description.state IS 'Состояние файла (TEMP или иное)';
COMMENT ON COLUMN file_description.summary IS 'Сводка об файле';
COMMENT ON COLUMN file_description.storage IS 'Описание местоположения файла в файловом хранилище';

drop table if exists users cascade;
create table if not exists users
(
    uuid                    uuid primary key,
    description             text,
    contacts                jsonb,
    legal_status            varchar(50),
    is_verified             boolean,
    is_legal_status_verfied boolean,
    profile_photo           uuid references file_description
);

-- users_files
DROP TABLE IF EXISTS users_files;
CREATE TABLE users_files
(
    uuid      UUID PRIMARY KEY,
    user      UUID REFERENCES users (uuid)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    file      UUID REFERENCES file_description (uuid)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    dt_create TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    dt_update TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

COMMENT ON TABLE users_files IS 'Contains data of two "virtual columns": documents_photos and portfolio';


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
DROP TABLE IF EXISTS orders_files;
CREATE TABLE orders_files
(
    uuid      UUID PRIMARY KEY,
    order     UUID REFERENCES orders (uuid)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    file      UUID REFERENCES file_description (uuid)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    dt_create TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    dt_update TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

COMMENT ON TABLE orders_files IS 'Contains any additional materials for an order (videos and photos)';

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
