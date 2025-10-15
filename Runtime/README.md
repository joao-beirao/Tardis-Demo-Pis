## DCR Runtime
___
The DCR Runtime provides support for the deployment of 
[DCR-based](https://codelab.fct.unl.pt/di/research/tardis/wp3/TaRDIS-DCR-Compiler) 
endpoints over the [Babel](https://codelab.fct.unl.pt/di/research/tardis/wp6) framework.

## Setup

Running this project requires a working Java installation (version 21 or higher) along 
with a few other dependencies (crucially, Babel). The suggested approach to set up and run
this project is through Maven.

### Configuration

#### Endpoint Specifications

An endpoint specification defines the (parameterizable) behaviour associated to a specific 
swarm participant, based on its **value-dependent role**. Endpoint specifications must be
placed under `src/main/resources/protocols/application`.

Endpoint specifications are encoded as `.json` files, following the schema found in
`src/main/resources/protocols/dcr-schema.json`. The suggested (and expected) approach to 
generate such specifications is through the companion 
[compiler](https://codelab.fct.unl.pt/di/research/tardis/wp3/TaRDIS-DCR-Compiler) tool,
 which automatically derives these based on a larger (global) specification:
a **choreography**.

The project currently contains specifications for two parameterizable roles, based on 
EDP's *Energy Communities* use case:
 - `P.json`: a **P**rosumer role expecting **id** and **cid** parameters
   - `P(id:String; cid:Integer)`
  - `CO.json`: a **C**ommunity *O*rchestrator expecting a **cid** parameter
    - `CO(cid:String)`

#### Babel

Endpoint deployment depends on the Babel framework. Babel-related configurations can be
found under `resources/config.properties`. The instructions that follow assume the current 
configuration.

### Usage

Compile and package the compiled code into a JAR by running the following command
```
mvn clean package
```

---
##### Launching a single endpoint 
Based on the endpoint-specifications already made available in the project you can, 
for instance, launch the endpoint for a single Prosumer by running 
(adjust the parameter `interface` accordingly):
```
java -jar target/babel-backend.jar interface=en0 role=P id=1 cid=1 
```

A REST web server for this endpoint will subsequently be available at
`localhost:8080/rest`.

---
##### Launching multiple endpoints
The suggested approach to launch multiple endpoints locally, to test the current example,
is to use Docker containers and the `docker-compose.yml` already made available.

From the root directory of the project, simply run the following command:
```
mvn clean package && docker compose up --build -d
```

This will launch 6 Prosumer endpoints, according to the parameters defined in the 
`docker-compose.yml`. REST webservices for each endpoint will be available at host ports
ranging from 1234 to 1240.

To stop the containers and remove intermediate containers:
```
docker compose down && docker image prune
```
