# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
I would like to refactor the `Store` that is using a `Active Record Pattern` this useful for simplify code,
but it is harder to test, has not flexibility and introduces coupling between the domain and persistence logic.
Additionally, the `LocationGateway` is using an in memory store, which should be moved to a database to enhance scaling,
persistence, and data manipulation, making the system more flexible and allowing for more robust handling of location data.

I prefer the repository patter strategy that encapsulates all the persistence logic.
The advantage are:
- It allows the separation of the domain model and the persistence layer.
- It is more flexible to change. You can modify the repository without changin the domain.
- It is testeable, it is posible to mock the repository and create unit test.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
- Open API
Pros:
  * Standarization
  * Code generation
  * Easy mantainace
Cons:
  * Limited Flexibility
  * Setting up the code generation

- Direct coding
Pros:
  * Full control
  * Faster
  * Flexible
Cons:
  * Lack of documentation
  * Risk of inconsistence
  * Manual work
  
My preference is to follow the OpenAPI specification approach. While it may take more time initially to set up 
the tool for auto-generating code, it leads to easier maintenance, reduces the risk of inconsistencies and errors,
and provides a standardized structure across all services.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
The priority order tha I would like to follow is this one:
1. Unit test
2. Integration test
3. E2E test
4. Non-Functional test

Initially, our focus should be on unit and integration tests to thoroughly cover the business logic.
Once these are in place, we should implement e2e tests to validate the entire application flow.
Finally, we will address non-functional aspects such as performance and usability through dedicated tests.

In order to ensuring the test coverage, it is important to do:
- Run unit, integration, and E2E tests in the CI/CD pipeline. Ensure that feature branches are not merged if any tests fail.
- Use code coverage tools like jacoco and track progress with platforms like Sonar. Set up minimal coverge to maintain code quality.
- Developers and reviewers should ensure that tests not only achieve code coverage but also effectively validate the functionality.
```
