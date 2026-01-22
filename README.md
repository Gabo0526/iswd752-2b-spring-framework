# Spring Boot Example

`/GET /POST /PUT /DELETE` APIs for managing cakes.

## Tech stack

* Java 8
* Spring boot - CRUD operations for managing cakes; application exposed on port `8081`
* Spring boot security - basic authentication applied to all APIs
* H2 database - for production and test code
* Swagger
* OpenApi
* Lombok
* Maven
* Docker

## Prerequisites

In order to build the project, you will have to install the following:

* Java 8
* Maven
* This project includes **Lombok Annotations**, this means that in order for your IDE to correctly compile your project you'll need to add the Lombok plugin to your IDE and `Enable annotation processing` (for IntelliJ IDEA).


## Build

### Maven

```
mvn clean install
```
## Run

### Maven

```
mvn spring-boot:run
```

## Swagger / OpenApi

Swagger endpoint: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

OpenApi endpoint: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

_**Important**_: swagger/openapi dependency for spring-boot 3 is now `springdoc-openapi-starter-webmvc-ui`  

## APIs

All APIs are secured using basic auth. Use the following credentials when making requests:
```
username=gabo
password=cake-password-which-should-be-kept-in-a-secret-place-and-injected-when-application-is-deployed
```

* GET /cakes
```
curl 'localhost:8081/cakes' \
-u "gabo:cake-password-which-should-be-kept-in-a-secret-place-and-injected-when-application-is-deployed"
```

* GET /cakes/{cake_id}
```
curl 'localhost:8081/cakes/15' \
-u "gabo:cake-password-which-should-be-kept-in-a-secret-place-and-injected-when-application-is-deployed"
```

* POST /cakes
```
curl -X POST 'localhost:8081/cakes' \
-u "gabo:cake-password-which-should-be-kept-in-a-secret-place-and-injected-when-application-is-deployed" \
--header 'Content-Type: application/json' \
--data-raw '{
    "title": "some title",
    "description": "some description"
}'
```

* PUT /cakes/{cake_id}
```
curl -X PUT 'localhost:8081/cakes/15' \
-u "gabo:cake-password-which-should-be-kept-in-a-secret-place-and-injected-when-application-is-deployed" \
--header 'Content-Type: application/json' \
--data-raw '{
    "title": "some title updated",
    "description": "some description updated"
}'
```

* DELETE /cakes/{cake_id}
```
curl -X DELETE 'localhost:8081/cakes/15' \
-u "gabo:cake-password-which-should-be-kept-in-a-secret-place-and-injected-when-application-is-deployed"
```