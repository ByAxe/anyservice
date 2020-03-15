create database anyservice_test_db;

\c anyservice_db

drop schema if exists anyservice_test cascade;
create schema if not exists anyservice_test;

-- FILE_DESCRIPTION
drop table if exists file_description cascade;
create table file_description
(
    uuid      uuid                        NOT NULL primary key,
    name      character(255)              NOT NULL,
    md5       character(64),
    size      bigint,
    extension character(50),
    dt_create timestamp without time zone NOT NULL,
    state     character(50),
    summary   character(255),
    storage   jsonb
);
comment on table file_description is 'Таблица хранения метаданных файлов';
comment on column file_description.uuid is 'Первичный ключ и суррогатный идентификатор файла';
comment on column file_description.name is 'Описание файла';
comment on column file_description.md5 is 'Хеш файла по алгоритму MD5';
comment on column file_description.size is 'Размер файла в байтах';
comment on column file_description.extension is 'Расширение файла';
comment on column file_description.dt_create is 'Дата/время создания файла';
comment on column file_description.state is 'Состояние файла (TEMP или иное)';
comment on column file_description.summary is 'Сводка об файле';
comment on column file_description.storage is 'Описание местоположения файла в файловом хранилище';

drop table if exists users cascade;
create table if not exists users
(
    uuid                     uuid primary key,
    dt_create                timestamptz not null default now(),
    dt_update                timestamptz not null default now(),
    user_name                varchar(50) not null unique,
    initials                 jsonb       not null,
    password                 varchar     not null,
    description              text,
    contacts                 jsonb,
    legal_status             varchar(50),
    is_verified              boolean,
    is_legal_status_verified boolean,
    profile_photo            uuid references file_description
);

-- users_files
drop table if exists users_files;
create table users_files
(
    uuid      UUID primary key,
    user_uuid UUID references users (uuid)
        on update cascade
        on delete cascade,
    file_uuid UUID references file_description (uuid)
        on update cascade
        on delete cascade,
    dt_create timestamptz,
    dt_update timestamptz
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
