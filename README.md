# Smart Shop

Smart Shop is an e-commerce platform built with Java Spring Boot for the backend and Angular for the frontend. It supports various roles such as users, sellers, and admins, and includes features like product variants, JWT authentication, and a recommendation system.

## Features

- **User roles**: User, Seller, Admin
- **Product Management**: EAV model to handle product variants
- **Authentication**: JWT-based authentication, Google, and Facebook login via OAuth2
- **Messaging**: Real-time messaging between users and sellers using Spring WebSocket
- **Image Upload**: Cloudinary integration for product images
- **Recommendation System**: Personalized product suggestions
- **Database**: PostgreSQL for product and user data

## Tech Stack
- **Backend**: Java 21, Spring Boot 3.3.4
  <img src="https://upload.wikimedia.org/wikipedia/commons/3/30/Java_logo_%282015%29.svg" width="50" height="50">
  <img src="https://upload.wikimedia.org/wikipedia/commons/0/0b/Spring_logo.svg" width="50" height="50">

- **Frontend**: Angular
  <img src="https://upload.wikimedia.org/wikipedia/commons/c/cf/Angular_full_color_logo.svg" width="50" height="50">

- **Database**: PostgreSQL
  <img src="https://upload.wikimedia.org/wikipedia/commons/2/29/Postgresql_elephant.svg" width="50" height="50">

- **Image Storage**: Cloudinary
  <img src="https://res.cloudinary.com/djovgxuqy/image/upload/v1625562880/logo-2021-02-01_f44rqm.svg" width="50" height="50">

- **Authentication**: JWT, OAuth2 (Google/Facebook)
  <img src="https://upload.wikimedia.org/wikipedia/commons/e/e5/JSON_Web_Token_logo.svg" width="50" height="50">

- **Real-Time Communication**: Spring WebSocket
  <img src="https://upload.wikimedia.org/wikipedia/commons/a/a2/Spring_Framework_Logo_2018.svg" width="50" height="50">

- **Recommendation System**: Content-based recommendation
  <img src="https://upload.wikimedia.org/wikipedia/commons/8/87/Machine_learning_icon.svg" width="50" height="50">
  
## Setup

### Prerequisites

- Java 21
- Spring Boot 3.3.4
- Node.js and npm for frontend
- Cloudinary account for image uploads
- PostgreSQL for the database

### Backend Setup

1. Clone the repository:
   ```bash
   git clone git@github.com:dangvanthua/shop-backend.git

cd shop-backend
./mvnw clean install

spring.datasource.url=jdbc:postgresql://localhost:5432/smartshop
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

./mvnw spring-boot:run

cd frontend
npm install
ng serve


### Key Changes:
- **Database**: I updated the database description to use PostgreSQL.
- **Configuration**: Added details for configuring PostgreSQL in the `application.yml` or `application.properties` file for the backend setup section.

This should now provide all the necessary information for setting up both the backend and frontend while including PostgreSQL as the database.
