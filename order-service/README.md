# springboot-react-keycloak

The goal of this project is to secure `erp-app` using [`Keycloak`](https://www.keycloak.org/)(with PKCE). `erp-app` consists of two applications: one is a [Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) Rest API called `movies-api` and another is a [React](https://react.dev/) application called `movies-ui`.

## Proof-of-Concepts & Articles

On [ivangfr.github.io](https://ivangfr.github.io), I have compiled my Proof-of-Concepts (PoCs) and articles. You can easily search for the technology you are interested in by using the filter. Who knows, perhaps I have already implemented a PoC or written an article about what you are looking for.

## Additional Readings

- \[**Medium**\] [**Implementing and Securing a Simple Spring Boot REST API with Keycloak**](https://medium.com/@ivangfr/how-to-secure-a-spring-boot-app-with-keycloak-5a931ee12c5a)
- \[**Medium**\] [**Implementing and Securing a Simple Spring Boot UI (Thymeleaf + RBAC) with Keycloak**](https://medium.com/spring-boot/how-to-secure-a-simple-spring-boot-ui-thymeleaf-rbac-with-keycloak-ba9f30b9cb2b)
- \[**Medium**\] [**Implementing and Securing a Spring Boot GraphQL API with Keycloak**](https://medium.com/javarevisited/implementing-and-securing-a-spring-boot-graphql-api-with-keycloak-c461c86e3972)
- \[**Medium**\] [**Setting Up OpenLDAP With Keycloak For User Federation**](https://medium.com/@ivangfr/setting-up-openldap-with-keycloak-for-user-federation-82c643b3a0e6)
- \[**Medium**\] [**Integrating GitHub as a Social Identity Provider in Keycloak**](https://medium.com/@ivangfr/integrating-github-as-a-social-identity-provider-in-keycloak-982f521a622f)
- \[**Medium**\] [**Integrating Google as a Social Identity Provider in Keycloak**](https://medium.com/@ivangfr/integrating-google-as-a-social-identity-provider-in-keycloak-c905577ec499)
- \[**Medium**\] [**Building a Single Spring Boot App with Keycloak or Okta as IdP: Introduction**](https://medium.com/@ivangfr/building-a-single-spring-boot-app-with-keycloak-or-okta-as-idp-introduction-2814a4829aed)

## Project diagram

![project-diagram](documentation/project-diagram.jpeg)

## Applications

- ### movies-api

  `Spring Boot` Web Java backend application that exposes a REST API to manage **movies**. Its secured endpoints can just be accessed if an access token (JWT) issued by `Keycloak` is provided.
  
  `movies-api` stores its data in a [`Mongo`](https://www.mongodb.com/) database.

  `movie-api` has the following endpoints

  | Endpoint                                                          | Secured | Roles                       |
  |-------------------------------------------------------------------|---------|-----------------------------|
  | `GET /api/userextras/me`                                          | Yes     | `MOVIES_MANAGER` and `USER` |
  | `POST /api/userextras/me -d {avatar}`                             | Yes     | `MOVIES_MANAGER` and `USER` | 
  | `GET /api/movies`                                                 | No      |                             |
  | `GET /api/movies/{imdbId}`                                        | No      |                             |
  | `POST /api/movies -d {"imdb","title","director","year","poster"}` | Yes     | `MOVIES_MANAGER`            |
  | `DELETE /api/movies/{imdbId}`                                     | Yes     | `MANAGE_MOVIES`             |
  | `POST /api/movies/{imdbId}/comments -d {"text"}`                  | Yes     | `MOVIES_MANAGER` and `USER` |

- ### movies-ui

  `React` frontend application where `users` can see and comment movies and `admins` can manage movies. In order to access the application, `user` / `admin` must login using his/her username and password. Those credentials are handled by `Keycloak`. All the requests coming from `movies-ui` to secured endpoints in `movies-api` have a access token (JWT) that is generated when `user` / `admin` logs in.
  
  `movies-ui` uses [`Semantic UI React`](https://react.semantic-ui.com/) as CSS-styled framework.

## Prerequisites

- [`Java 17+`](https://www.oracle.com/java/technologies/downloads/#java17)
- [`npm`](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm)
- [`Docker`](https://www.docker.com/)
- [`Docker-Compose`](https://docs.docker.com/compose/install/)
- [`jq`](https://stedolan.github.io/jq)
- [`OMDb API`](https://www.omdbapi.com/) KEY

  To use the `Wizard` option to search and add a movie, you need to get an API KEY from OMDb API. In order to do it, access https://www.omdbapi.com/apikey.aspx and follow the steps provided by the website.

  Once you have the API KEY, create a file called `.env.local` in `springboot-react-keycloak/movies-ui` folder with the following content 
  ```
  REACT_APP_OMDB_API_KEY=<your-api-key>
  ```

## PKCE

As `Keycloak` supports [`PKCE`](https://tools.ietf.org/html/rfc7636) (`Proof Key for Code Exchange`) since version `7.0.0`, we are using it in this project. 

## Start Environment

- In a terminal and inside `springboot-react-keycloak` root folder run
  ```
  docker-compose up -d
  ```

- Wait for all Docker containers to be up and running. To check it, run
  ```
  docker-compose ps
  ```

## Running movies-app using Maven & Npm

- **movies-api**

  - Open a terminal and navigate to `springboot-react-keycloak/movies-api` folder

  - Run the following `Maven` command to start the application
    ```
    ./mvnw clean spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=9080"
    ```

    Once the startup finishes, `KeycloakInitializerRunner.java` class will run and initialize `erp-realm` realm in `Keycloak`. Basically, it will create:
    - Realm: `erp-realm`
    - Client: `erp-app`
    - Client Roles: `ERP_MANAGER` and `USER`
    - Two users
      - `admin`: with roles `ERP_MANAGER` and `USER`
      - `user`: only with role `USER`

  - We can also configure **Social Identity Providers** such as, `GitHub`, `Google`, `Facebook` and `Instagram`. I've written two articles in **Medium** where I explain step-by-step how to integrate [GitHub](https://medium.com/@ivangfr/integrating-github-as-a-social-identity-provider-in-keycloak-982f521a622f) and [Google](https://medium.com/@ivangfr/integrating-google-as-a-social-identity-provider-in-keycloak-c905577ec499).

- **movies-ui**

  - Open another terminal and navigate to `springboot-react-keycloak/movies-ui` folder

  - Run the command below if you are running the application for the first time
    ```
    npm install
    ```

  - Run the `npm` command below to start the application
    ```
    npm start
    ```

## Applications URLs

| Application | URL                                   | Credentials                           |
|-------------|---------------------------------------|---------------------------------------|
| movie-api   | http://localhost:9080/swagger-ui.html | [Access Token](#getting-access-token) |
| movie-ui    | http://localhost:3000                 | `admin/admin` or `user/user`          |
| Keycloak    | http://localhost:8080/admin           | `admin/admin`                         |

## Demo

- The gif below shows an `admin` logging in and adding one movie using the wizard feature

  ![demo-admin](documentation/demo-admin.gif)

- The gif below shows a `user` logging in using his Github account; then he changes his avatar and comment a movie

  ![demo-user-github](documentation/demo-user-github.gif)

## Testing movies-api endpoints

You can manage movies by accessing directly `movies-api` endpoints using the Swagger website or `curl`. However, for the secured endpoints like `POST /api/movies`, `PUT /api/movies/{id}`, `DELETE /api/movies/{id}`, etc, you need to inform an access token issued by `Keycloak`.

### Getting Access Token

- Open a terminal

- Run the following commands to get the access token
  ```
  ACCESS_TOKEN="$(curl -s -X POST \
    "http://localhost:8080/realms/erp-realm/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "username=admin" \
    -d "password=admin" \
    -d "grant_type=password" \
    -d "client_id=erp-app" | jq -r .access_token)"

  echo $ACCESS_TOKEN
  ```
  > **Note**: In [jwt.io](https://jwt.io), you can decode and verify the `JWT` access token

### Calling movies-api endpoints using curl

- Trying to add a movie without access token
  ```
  curl -i -X POST "http://localhost:9080/api/movies" \
    -H "Content-Type: application/json" \
    -d '{ "imdbId": "tt5580036", "title": "I, Tonya", "director": "Craig Gillespie", "year": 2017, "poster": "https://m.media-amazon.com/images/M/MV5BMjI5MDY1NjYzMl5BMl5BanBnXkFtZTgwNjIzNDAxNDM@._V1_SX300.jpg"}'
  ```

  It should return
  ```
  HTTP/1.1 401
  ```

- Trying again to add a movie, now with access token (obtained at #getting-access-token)
  ```
  curl -i -X POST "http://localhost:9080/api/movies" \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{ "imdbId": "tt5580036", "title": "I, Tonya", "director": "Craig Gillespie", "year": 2017, "poster": "https://m.media-amazon.com/images/M/MV5BMjI5MDY1NjYzMl5BMl5BanBnXkFtZTgwNjIzNDAxNDM@._V1_SX300.jpg"}'
  ```

  It should return
  ```
  HTTP/1.1 201
  {
    "imdbId": "tt5580036",
    "title": "I, Tonya",
    "director": "Craig Gillespie",
    "year": "2017",
    "poster": "https://m.media-amazon.com/images/M/MV5BMjI5MDY1NjYzMl5BMl5BanBnXkFtZTgwNjIzNDAxNDM@._V1_SX300.jpg"
  }
  ```

- Getting the list of movies. This endpoint does not requires access token
  ```
  curl -i http://localhost:9080/api/movies
  ```

  It should return
  ```
  HTTP/1.1 200
  [
    {
      "imdbId": "tt5580036",
      "title": "I, Tonya",
      "director": "Craig Gillespie",
      "year": "2017",
      "poster": "https://m.media-amazon.com/images/M/MV5BMjI5MDY1NjYzMl5BMl5BanBnXkFtZTgwNjIzNDAxNDM@._V1_SX300.jpg",
      "comments": []
    }
  ]
  ```

### Calling movies-api endpoints using Swagger

- Access `movies-api` Swagger website, http://localhost:9080/swagger-ui.html

- Click `Authorize` button.

- In the form that opens, paste the `access token` (obtained at [getting-access-token](#getting-access-token)) in the `Value` field. Then, click `Authorize` and `Close` to finalize.

- Done! You can now access the secured endpoints

## Useful Commands

- **MongoDB**

  List all movies
  ```
  docker exec -it mongodb mongosh moviesdb
  db.movies.find()
  ```
  > Type `exit` to get out of MongoDB shell

## Shutdown

- To stop `movies-api` and `movies-ui`, go to the terminals where they are running and press `Ctrl+C`

- To stop and remove docker-compose containers, network and volumes, go to a terminal and, inside `springboot-react-keycloak` root folder, run the command below
  ```
  docker-compose down -v
  ```

## How to upgrade movies-ui dependencies to latest version

- In a terminal, make sure you are in `springboot-react-keycloak/movies-ui` folder

- Run the following commands
  ```
  npm upgrade
  npm i -g npm-check-updates
  ncu -u
  npm install
  ```
