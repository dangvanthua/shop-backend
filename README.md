
# Smart Shop

Smart Shop is an e-commerce platform built with Java Spring Boot for the backend and Angular for the frontend. It supports various roles such as users, sellers, and admins, and includes features like product variants, JWT authentication, and a recommendation system.

## Features

- **User roles**: User, Seller, Admin
- **Product Management**: EAV model to handle product variants
- **Authentication**: JWT-based authentication, Google, and Facebook login via OAuth2
- **Messaging**: Real-time messaging between users and sellers using Spring WebSocket
- **Image Upload**: Cloudinary integration for product images
- **Recommendation System**: Personalized product suggestions
- **Database**: MySQL or PostgreSQL for product and user data

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.3.4
- **Frontend**: Angular
- **Database**: MySQL or PostgreSQL
- **Image Storage**: Cloudinary
- **Authentication**: JWT, OAuth2 (Google/Facebook)
- **Real-Time Communication**: Spring WebSocket
- **Recommendation System**: Content-based recommendation

## Setup

### Prerequisites

- Java 21
- Spring Boot 3.3.4
- Node.js and npm for frontend
- Cloudinary account for image uploads

### Backend Setup

1. **Clone the repository**:
   ```bash
   git clone git@github.com:dangvanthua/shop-backend.git
   ```

2. **Navigate to the backend directory**:
   ```bash
   cd shop-backend
   ```

3. **Build the project**:
   ```bash
   ./mvnw clean install
   ```

4. **Configure the database**: 
   Open `application.properties` or `application.yml` and add the following configuration:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/smartshop
   spring.datasource.username=your-username
   spring.datasource.password=your-password
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   ```

5. **Run the backend**:
   ```bash
   ./mvnw spring-boot:run
   ```

6. **Navigate to the frontend directory and install dependencies**:
   ```bash
   cd frontend
   npm install
   ```

7. **Run the frontend**:
   ```bash
   ng serve
   ```

### Key Changes:
- **Database**: I updated the database description to use PostgreSQL.
- **Configuration**: Added details for configuring PostgreSQL in the `application.yml` or `application.properties` file for the backend setup section.

This should now provide all the necessary information for setting up both the backend and frontend while including PostgreSQL as the database.
