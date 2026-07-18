this files for me to understand whatever ive built and its workflow, so i can understand what ive built so far and i can also revise :))


# How I Built and Understand My Spring Boot Backend Project
# Understanding Spring Boot Project Architecture

Initially, I had different files like controllers, services, repositories, entities, and DTOs, but I did not fully understand why they were separated.

I learned that each layer has a specific responsibility.

The controller is the entry point of the application. It communicates with the frontend. It receives HTTP requests, extracts data from them, and sends the request to the service layer.

The controller should not contain business logic. Its job is only to handle communication.

The service layer contains the actual application logic. This is where decisions happen, such as encrypting passwords, creating journals, checking ownership, updating data, and converting objects.

The repository layer communicates with the database. Instead of manually writing SQL queries for every operation, Spring Data JPA allows repositories to interact with database tables using Java methods.

The entity classes represent database tables. They define how objects are stored and how relationships between tables work.

The DTO layer controls what information enters and leaves the application.

Understanding this separation helped me realize that a backend is not one huge file. Each layer has a specific responsibility.

---

# Understanding Dependency Injection and Spring Beans

One of my biggest questions while learning Spring Boot was:

"Where are these objects actually created?"

For example, UserService requires UserRepository. Normally in Java, I would create an object manually using the new keyword.

However, Spring works differently.

Spring creates and manages objects called beans.

Classes marked with annotations like @Service, @Repository, and @Controller are automatically detected by Spring during application startup.

Spring creates these objects and stores them inside its Application Context.

When another class needs a dependency, Spring automatically provides that object.

For example, UserService does not create UserRepository itself. Instead, it asks for UserRepository through constructor injection, and Spring provides it.

The reason we use dependency injection is because classes do not need to know how their dependencies are created. They only need to know what they need.

This makes the application easier to maintain, test, and modify.

---

# User Registration Flow

When a new user registers, the frontend sends information such as username, email, and password.

The request first reaches the UserController.

Initially, I was returning User directly from the controller. Later, I understood that exposing entities directly is not a good practice because entities represent internal database structures.

Instead, I introduced RegisterRequest and UserResponse DTOs.

RegisterRequest represents data entering the backend.

UserResponse represents data leaving the backend.

The controller receives RegisterRequest because it contains only the information a user is allowed to provide.

The controller passes this request to UserService.

The service creates a new User entity from the RegisterRequest.

The service then encrypts the password using BCryptPasswordEncoder.

The password is never stored directly in the database.

After encryption, the User entity is passed to UserRepository.

The repository saves the user into PostgreSQL.

At this point, the account exists, but the user is not logged in.

Registration and authentication are two separate processes.

Creating an account does not automatically mean the user is authenticated.

---

# Understanding Password Encryption with BCrypt

Initially, I wondered:

"If we need the password during login, why don't we just store it?"

The reason is security.

Passwords should never be stored as plain text.

If someone gains access to the database, they should not be able to see everyone's passwords.

BCrypt does not encrypt passwords because encryption can be reversed.

Instead, it hashes passwords.

During registration:

The original password is converted into a hash.

During login:

The user enters their password again.

BCrypt compares the entered password with the stored hash.

If they match, authentication succeeds.

The backend never needs to know the original password.

---

# Login Flow and JWT Creation

Login works differently from registration.

The frontend sends email and password using LoginRequest.

The controller receives this request and sends it to UserService.

The service searches for the user using the email.

If the user does not exist, login fails.

If the user exists, BCrypt compares the entered password with the stored encrypted password.

If the password is correct, the service calls JwtUtil.

JwtUtil creates a JWT token.

The token contains information such as:

The user's email.

Expiration time.

A signature to verify that the token has not been modified.

The backend returns this token through LoginResponse.

The frontend stores this token.

Now the user is logged in.

---

# Understanding JWT Filter

One of my biggest questions was:

"Why do we need JwtFilter if JWT is already created during login?"

The answer is that JwtFilter is not used during login.

During login:

Frontend sends credentials.

Backend verifies them.

JwtUtil creates a token.

Token is returned.

JwtFilter is used only after login.

For example:

The user wants to view journals.

The frontend sends a request with the JWT inside the Authorization header.

Before the request reaches the controller, it enters Spring Security's filter chain.

The JwtFilter reads the token.

It asks JwtUtil to validate:

Is the signature correct?

Has the token expired?

Is the token valid?

If the token is valid, JwtUtil extracts the user's email.

The filter then uses UserRepository to retrieve the User from the database.

Now the filter knows who is making the request.

---

# Why SecurityContext Exists

At this point, the authenticated User object exists only inside JwtFilter.

My confusion was:

"How can the service know the logged-in user if the variable exists only inside the filter?"

The answer is SecurityContext.

A local variable inside a method cannot be accessed by other classes.

So Spring Security provides SecurityContext as temporary storage for the current request.

The JwtFilter creates a UsernamePasswordAuthenticationToken containing the authenticated user.

It stores this object inside SecurityContext.

Now controllers and services can access the authenticated user.

For example, inside JournalService:

The service asks SecurityContext:

"Who is currently logged in?"

SecurityContext returns the authenticated User.

The service can now associate journals with that user.

The SecurityContext exists only for the current incoming HTTP request.

It is not used when sending responses.

After the request finishes, it is cleared.

When another request arrives, JWT validation happens again and SecurityContext is recreated.

This is why JWT authentication is stateless.

---

# Connecting User and Journal Entities

After authentication was working, I needed to connect users with their journals.

The requirement was:

One user can have many journals.

One journal belongs to one user.

This is a one-to-many relationship.

The User entity contains a list of journals.

The Journal entity contains a User object.

The Journal side contains the foreign key because every journal needs to know its owner.

JPA automatically creates a user_id column in the journals table.

I do not manually create or send userId.

The relationship handles it automatically.

---

# Why Frontend Should Never Send userId

One important security lesson I learned was:

The frontend should never decide ownership.

A bad design would be:

Frontend sends:

"Create this journal and assign it to userId 5."

The problem is that anyone could modify the request and send another user's ID.

Instead, ownership is decided by the backend.

When creating a journal:

The frontend only sends title and content.

The backend retrieves the authenticated user from SecurityContext.

The backend sets:

This journal belongs to this logged-in user.

The database stores the relationship.

This prevents users from creating data for other accounts.

---

# Protecting Journal Data

Initially, a simple database query would be:

Find journal by ID.

However, this creates a security problem.

Imagine:

User A owns journal ID 10.

User B guesses that ID and sends:

GET /journals/10

If the backend only checks the ID, User B can access User A's journal.

To prevent this, I changed the query.

Instead of:

Find by ID.

I use:

Find by ID and User.

The database checks two things:

Does this journal exist?

Does it belong to the currently authenticated user?

Only then is the journal returned.

The same idea applies to update and delete operations.

A user can only modify resources they own.

---

# Understanding DTOs

One of my biggest questions was:

"If Spring already converts JSON into objects using @RequestBody, why do we need DTOs?"

The answer is separation.

Entities represent database structure.

DTOs represent API communication.

They have different purposes.

For example:

Journal entity contains:

Journal information.

User relationship.

Database-specific information.

But the frontend does not need all of that.

So instead of sending the entity directly, I created:

JournalRequest.

JournalResponse.

---

# JournalRequest Flow

When creating or updating a journal:

Frontend sends:

Title.

Content.

The controller receives JournalRequest using @RequestBody.

The service converts this request into a Journal entity.

The service adds information the frontend should never control:

Authenticated user.

Creation time.

Other backend-controlled fields.

Then the entity is saved.

---

# JournalResponse and mapToResponse()

After saving, the repository returns a Journal entity.

My next question was:

"Why not directly return the entity?"

The problem is that entities can contain internal information.

For example:

A Journal contains the User object.

Returning it could expose unnecessary information.

So I created mapToResponse().

This method takes a Journal entity and creates a JournalResponse object.

It copies only the required fields:

ID.

Title.

Content.

Created time.

Then Spring converts JournalResponse into JSON and sends it to the frontend.

So:

JournalRequest = What enters the backend.

Journal = Internal object used by backend and database.

JournalResponse = What leaves the backend.

---

# Validation Using Bean Validation

After creating APIs, I wanted to prevent invalid data.

For example:

A journal should not have an empty title.

A password should not be too short.

An email should have a valid format.

Spring provides Bean Validation annotations.

Examples:

@NotBlank checks that a String is not null, empty, or only spaces.

@Size checks length.

@Email checks email format.

However, these annotations do nothing by themselves.

They only define rules.

The controller needs @Valid.

When @Valid is added:

Spring checks the DTO before entering the controller method.

If validation fails, Spring throws MethodArgumentNotValidException.

---

# Global Exception Handling

Initially, I wondered:

"When validation fails, where does the error go?"

The answer is GlobalExceptionHandler.

@ControllerAdvice tells Spring:

"This class handles exceptions globally."

Whenever an exception happens, Spring searches for a matching @ExceptionHandler method.

For validation errors:

MethodArgumentNotValidException occurs.

Spring calls the corresponding handler.

The handler collects all field errors and returns a clean JSON response.

Instead of every controller writing error handling code, one global place manages errors.

---

# Custom ResourceNotFoundException

For database operations, another common situation occurs.

Example:

User requests:

GET /journals/1000

But journal 1000 does not exist.

Instead of returning null everywhere, I created a custom exception.

ResourceNotFoundException represents:

"The requested data does not exist."

Inside the service, I use:

orElseThrow()

If data exists:

Continue normally.

If data does not exist:

Throw ResourceNotFoundException.

---

# Understanding throw and throws

I initially confused throw and throws.

throw is used when I actually create and send an exception.

Example:

Throw a new ResourceNotFoundException.

It immediately creates an exception object and sends it upward.

throws is used in a method signature to tell Java:

"This method may produce this exception."

It does not create the exception.

In this project, I mostly use throw because I want to actively create exceptions when something goes wrong.

---

# Final Understanding of My Project

After building this project, I understand that the backend works as a complete system.

A user registers.

The password is encrypted.

The user is stored in PostgreSQL.

The user logs in.

Credentials are verified.

JWT is generated.

The frontend stores the token.

Every future request sends the token.

JwtFilter validates the token.

The authenticated user is stored in SecurityContext.

The service retrieves that user.

Database operations always check ownership.

DTOs control what data enters and leaves.

Validation protects the API from invalid input.

Global exception handling creates clean error responses.

The backend does not trust the frontend for security decisions.

The backend decides:

Who is logged in.

What data they own.

What they can access.

What information they receive.


