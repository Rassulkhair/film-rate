DROP TABLE IF EXISTS film_likes;
DROP TABLE IF EXISTS films_genres;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS mpa;
DROP TABLE IF EXISTS user_friends;
DROP TABLE IF EXISTS users;



create table if not exists users
(
    id       bigint generated always as identity
    primary key,
    email    varchar(255) not null,
    login    varchar(100) not null,
    name     varchar(255),
    birthday date
    );

create table if not exists user_friends
(
    user_id   bigint not null
    references users,
    friend_id bigint not null
    references users,
    primary key (user_id, friend_id)
    );



create table if not exists mpa
(
    id   bigserial
    primary key,
    name varchar(10) not null
    );


create table if not exists films
(
    id           bigserial
    primary key,
    name         varchar(255) not null,
    description  varchar(255) not null,
    release_date date         not null,
    duration     integer      not null,
    mpa_id       bigint       not null
    references mpa
    );



create table if not exists genres
(
    id   bigserial
    primary key,
    name varchar(255) not null
    );



create table if not exists films_genres
(
    film_id  bigint not null
    references films,
    genre_id bigint not null
    references genres,
    primary key (film_id, genre_id)
    );



create table if not exists film_likes
(
    film_id bigint not null
    references films,
    user_id bigint not null
    references users,
    primary key (film_id, user_id)
    );
