# Credit Module

### Run
How to run the application.
1. Clone the repository to your local machine.
2. Download the <code>jar</code> version of H2 database from [here](https://www.h2database.com/html/download.html).
3. Rename the downloaded file to <code>h2.jar</code> and place it in to the root folder of project.
4. Build a Docker image using the following command: 
   - <code>docker build -t credit-module .</code>
5. To start project 
   - <code>run -p 8080:8080 -p 8082:8082 -p 9092:9092 credit-module</code>

The <code>user</code> table has been added to manage authentication and authorization.
During the Docker build process, the schema is created in the database, and the <code>user</code> and <code>customer</code> tables are initialized with sample data.
All initial SQL scripts are located in the <code>init-db.sql</code> file.

### Test
A Postman collection is available in the project's <code>documents</code> folder for API testing.

- A JWT token must be generated first.
- The token should be included in the request headers for authentication.
- Use the following format for authentication:
  - <bold>Key:</bold> Authorization
  - <bold>Value:</bold> Bearer {JWT_TOKEN}


You can generate a JWT token using the <code>login</code> API with the credentials below:

| UserId | Password | Role      |
|--------| -------- |-----------|
| 1      | admin    | ADMIN     |
| 2      | admin    | ADMIN     |
| 3      | customer | CUSTOMER  | 
| 4      | customer | CUSTOMER  |

### Notes
For the loan filter API, indexing was not applied to the <code>isPaid</code> and <code>numberOfInstallment</code> columns, as they are low-cardinality fields.
A composite partitioned index combining <code>isPaid</code> and <code>numberOfInstallment</code>, and global index on <code>customerId</code> would be more efficient, but it was not implemented because H2 does not support partitioned indexing.
An alternative approach would be to use a separate database replica for reporting or analytics purposes.

