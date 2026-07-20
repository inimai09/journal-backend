# DevJourney API v1

A secure RESTful Journal Management API built using **Spring Boot**, **Spring Security**, **JWT Authentication**, and **PostgreSQL**. The project demonstrates modern backend development practices, including authentication, authorization, validation, exception handling, and relational database management.

---

## Features

### Authentication & Security

* User registration
* User login
* JWT (JSON Web Token) authentication
* BCrypt password hashing
* Stateless authentication using Spring Security
* Protected APIs using JWT Filter
* User identity managed through `SecurityContext`

### Journal Management

* Create journal entries
* View all journals belonging to the authenticated user
* View a journal by ID
* Update journal entries
* Delete journal entries

### Authorization

* Users can only access and modify **their own journals**
* Ownership is enforced using authenticated user information rather than trusting the client

### Validation

* Bean Validation using:

  * `@NotBlank`
  * `@Email`
  * `@Size`
* Automatic request validation using `@Valid`

### Exception Handling

* Global exception handling using `@ControllerAdvice`
* Custom `ResourceNotFoundException`
* Clean validation error responses

---

## Tech Stack

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA (Hibernate)
* PostgreSQL
* JWT (JJWT)
* Maven
* Postman

## API Endpoints

### Authentication

| Method | Endpoint       | Description           |
| ------ | -------------- | --------------------- |
| POST   | `/users`       | Register a new user   |
| POST   | `/users/login` | Login and receive JWT |

### Journals

| Method | Endpoint             | Description                             |
| ------ | -------------------- | --------------------------------------- |
| GET    | `/api/journals`      | Get all journals for authenticated user |
| GET    | `/api/journals/{id}` | Get journal by ID                       |
| POST   | `/api/journals`      | Create journal                          |
| PUT    | `/api/journals/{id}` | Update journal                          |
| DELETE | `/api/journals/{id}` | Delete journal                          |

---

## Example Login Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login successful"
}
```

## Future Improvements (v2)

* Refresh Tokens
* Swagger/OpenAPI Documentation
* Docker Support
* Pagination & Sorting
* Journal Search
* Tags & Categories
* File/Image Uploads
* Unit & Integration Tests
* Deployment to Cloud

