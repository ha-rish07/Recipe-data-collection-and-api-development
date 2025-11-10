# Recipe API and Frontend

This project implements a Spring Boot application to parse recipe JSON, store it in a PostgreSQL database (nutrients stored as JSONB), expose REST APIs for listing and searching, and provides a small static HTML frontend.

Main features
- Parses `src/main/resources/data/recipes.json` at startup and loads recipes into DB (skips/sets null for NaN values)
- Stores `nutrients` as JSONB and also extracts `caloriesInt` for numeric filtering
- API endpoints:
  - `GET /api/recipes?page=1&limit=10` — paginated, sorted by rating desc
  - `GET /api/recipes/search?calories=<=400&title=pie&rating=>=4.5&total_time=>=60&cuisine=Southern Recipes`
- Static frontend at `src/main/resources/static/index.html` (calls APIs)

Requirements
- Java 11+
- Maven
- PostgreSQL (recommended)

Database setup
1. Install PostgreSQL and create database `recipesdb` (or change `SPRING_DATASOURCE_URL`):

   - Create DB and user, for example:

     CREATE DATABASE recipesdb;
     CREATE USER recipes_user WITH PASSWORD 'yourpassword';
     GRANT ALL PRIVILEGES ON DATABASE recipesdb TO recipes_user;

2. Set environment variables (example PowerShell):

   $env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/recipesdb'
   $env:SPRING_DATASOURCE_USERNAME = 'recipes_user'
   $env:SPRING_DATASOURCE_PASSWORD = 'yourpassword'

Run the app

1. Build and run with Maven:

   mvn clean package
   mvn spring-boot:run

   The app runs on http://localhost:8080

API examples

- Get recipes (page 1, 10 per page):
  GET /api/recipes?page=1&limit=10

- Search by calories, title and rating:
  GET /api/recipes/search?calories=<=400&title=pie&rating=>=4.5

Notes & design decisions
- I used PostgreSQL JSONB for the `nutrients` column as requested and added an extra `calories_int` integer column for numeric filtering (since the `calories` field in the JSON is a string like "389 kcal").
- The loader converts common numeric-like fields and handles "NaN" by mapping to null.

Next steps / improvements
- Add more fields to the frontend search UI (rating range, calories operator input, total_time operator)
- Add more robust unit/integration tests
- Add docker-compose with postgres for easy local setup
