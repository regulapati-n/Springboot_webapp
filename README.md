# webservice
## project info
Basic spring-boot CRUD API with maven setup. Added Basic auth settings to the project.
## steps to run this project
1. git clone git@github.com:regulapati-n/webapp.git
2. Install maven in your system and java 17 openjdk and Mysql
3. change to project directory in terminal and run the below commands.
4. mvn clean install
5. mvn spring-boot:run
6. check the logs for the application urll.
7. Default port is 8080 and runs in localhost.
8. press command+c to stop the serverr.
9. if you wish not to install maven.You can run following commands on respective OS.
10. ./mvnw spring-boot:run  (unix)
11. ./mvnw.cmd spring-boot:run  (windows)
## running test cases
1. change to the repo directory.
2. run the below command.
3. mvn test
## urls 
1. http://nixor.me/healthz (health check api)
2. http://localhost:8080/v1/user (POST -- basic auth user creation)
3. http://localhost:8080/v1/user/self (GET -- get current logged user information)
4. http://localhost:8080/v1/user/self (PUT -- update user information)



Added Integration Tests
