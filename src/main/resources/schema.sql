-- SQL schema for recipes table (PostgreSQL)
CREATE TABLE IF NOT EXISTS recipes (
  id bigserial PRIMARY KEY,
  cuisine varchar(255),
  title varchar(1000),
  rating double precision,
  prep_time integer,
  cook_time integer,
  total_time integer,
  description text,
  nutrients jsonb,
  serves varchar(255),
  calories_int integer
);
