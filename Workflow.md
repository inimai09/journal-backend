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

Got it buddy. I'll convert those added sections into the same **humanized paragraph style** as your documentation instead of bullet points.

Add this after your existing documentation:

---

# Understanding Spring IoC Container and ApplicationContext

One of my biggest questions while learning Spring Boot was, "Where are all these objects actually created?" In normal Java programming, if one class needs another class, I would manually create the object using the `new` keyword. For example, I would create a `UserRepository` object and pass it into `UserService` myself. However, Spring works differently because it follows Inversion of Control (IoC). Instead of classes creating their own dependencies, Spring creates and manages these objects for us. These managed objects are called Beans.

During application startup, Spring scans the application and detects classes annotated with `@Controller`, `@Service`, `@Repository`, and `@Component`. It creates objects of these classes and stores them inside the ApplicationContext. The ApplicationContext is Spring's IoC container, which acts as a storage area containing all Spring-managed objects. For example, objects like `UserController`, `UserService`, `UserRepository`, `JournalService`, `JwtUtil`, and `JwtFilter` are created and managed by Spring.

When a class needs another object, it does not create it manually. Instead, it requests that dependency through constructor injection. For example, `UserService` requires `UserRepository`, so it declares it in its constructor. Spring checks the ApplicationContext, finds the `UserRepository` bean, and automatically provides it. This makes classes loosely coupled because they only know what dependency they need, not how that dependency is created. This improves maintainability, testing, and flexibility.

---

# Difference Between Bean Creation and Bean Validation

While learning validation, I initially confused Spring Beans with Bean Validation because both contain the word "Bean". However, they are completely different concepts.

A Spring Bean is an object managed by Spring's IoC container. These are created using annotations like `@Service`, `@Repository`, or manually using `@Bean`. For example, a `PasswordEncoder` object can be created as a Spring Bean using a configuration class.

Bean Validation is different. It is used to define rules for incoming data. Annotations like `@NotBlank`, `@Size`, and `@Email` do not create objects. They simply define conditions that data must satisfy. The validation happens when the controller uses `@Valid` before accepting a DTO.

For example, when a user sends a journal with an empty title, Spring checks the validation rules in `JournalRequest`. If the title violates the `@NotBlank` rule, Spring throws a `MethodArgumentNotValidException` before the controller method executes.

---

# Understanding Custom Exceptions and super(message)

While handling errors, I created a custom exception called `ResourceNotFoundException`. This represents cases where requested data does not exist. For example, if a user requests a journal with an ID that is not present in the database, instead of returning `null`, the service throws a custom exception.

When writing the exception class, I used:

```java
super(message);
```

At first, I wondered what this meant. Since `ResourceNotFoundException` extends `RuntimeException`, it inherits the features of the parent exception class. Calling `super(message)` sends the error message to the parent `RuntimeException` class so that Java's exception system can store and handle that message.

When we write:

```java
throw new ResourceNotFoundException("Journal not found");
```

we are creating a new exception object and immediately sending it upward. The exception moves back through the layers until Spring finds a class that knows how to handle it.

---

# How Exceptions Reach GlobalExceptionHandler

When an exception is thrown inside the service layer, it does not directly go to the frontend. Spring catches the exception and checks whether there is a handler available for that type of exception.

The flow is:

The service throws `ResourceNotFoundException`.

The exception moves upward because the service cannot handle it.

Spring detects the exception.

Spring checks classes marked with `@ControllerAdvice`.

It searches for a method with a matching `@ExceptionHandler`.

The matching handler executes and creates the response sent back to the frontend.

This is why `@ControllerAdvice` works automatically. Spring manages this class and uses it whenever an exception occurs.

---

# Why Login Does Not Need mapToResponse()

While working with DTOs, I initially wondered why we use `mapToResponse()` for users and journals but not for login.

The reason is that login has a different purpose.

During registration, the backend receives a `RegisterRequest` and creates a User entity. After saving, we return a `UserResponse` because the frontend may need user information.

During login, the frontend only needs authentication information. The backend receives a `LoginRequest` containing email and password. After verification, the backend generates a JWT token and returns it using `LoginResponse`.

The backend does not return the User entity during login because the frontend does not need the entire user object. It only needs the token that proves authentication.

---

# Why mapToResponse() Exists

One of my biggest questions was:

"If Spring already converts JSON into objects using `@RequestBody`, why do we need mapToResponse()?"

The answer is that `@RequestBody` and `mapToResponse()` solve different problems.

`@RequestBody` converts incoming JSON from the frontend into a Java object.

For example:

Frontend JSON:

```json
{
"title":"My day",
"content":"Learning Spring"
}
```

becomes:

```java

```

However, `mapToResponse()` is used when sending data back.

Database entities contain internal information. For example, a Journal entity contains the User relationship because the database needs to know who owns the journal.

If we directly return the Journal entity, Spring may expose unnecessary information such as the entire User object.

Therefore, the flow becomes:

Journal Entity → mapToResponse() → JournalResponse → JSON response

The entity is used internally by the backend and database, while the response DTO controls what information leaves the application.

---

# Complete Protected Request Flow

After login, every protected request follows a specific flow.

The frontend sends a request with the JWT token inside the Authorization header.

Before reaching the controller, the request enters Spring Security's filter chain.

The JwtFilter reads the token and sends it to JwtUtil for validation.

JwtUtil checks whether the signature is correct and whether the token has expired.

If valid, the filter extracts the user's email from the token.

The filter uses UserRepository to find the corresponding User from the database.

The authenticated User object exists temporarily inside the filter, so the filter creates a `UsernamePasswordAuthenticationToken` and stores it inside SecurityContext.

Now the controller and service can access the logged-in user.

The service retrieves the user from SecurityContext and performs database operations only for that user.

After the response is completed, SecurityContext is cleared.

The next request repeats the same process because JWT authentication is stateless.

---

