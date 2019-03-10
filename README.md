[ ![jcenter](https://api.bintray.com/packages/rrdev/RxUseCase/digital.bakehouse.rxusecase/images/download.svg) ](https://bintray.com/rrdev/RxUseCase/digital.bakehouse.rxusecase/_latestVersion)[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [![Build Status](https://img.shields.io/travis/bakehousedigital/rxusecase/master.svg)](https://img.shields.io/travis/bakehousedigital/rxusecase/master.svg)

# RxUseCase

RxUseCase is a tiny Java library for wrapping business logic into reactive streams which expose data in a standardized way.

The library provides the core components for implementing the Use Cases (also known as Interactors) of  
[Uncle Bob's Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) approach, which orchestrate the flow of data to and from the entities and represent the essence of a typical domain layer. 
<br/>
<p align="center">
  <img src='blob/doc-clean-small.PNG' alt='Clean Architecture Onion' align='middle' width='80%' />
</p>

## Download

### Gradle

```groovy
implementation 'digital.bakehouse:rxusecase:0.9'
```

### Maven
```xml
<dependency>
	<groupId>digital.bakehouse</groupId>
	<artifactId>rxusecase</artifactId>
	<version>0.9</version>
	<type>pom</type>
</dependency>
```

## How it works
<p align="center">
  <img src='blob/doc-how-simple.PNG' alt='How it works - Basic' align='middle' width='60%' />
</p>

<br/>

**RxUseCase** wraps the logic / action / mechanism that the use-case implements, into an Observable that can be subscribed to. 
It can act as an interactor, communicating to with internal or external components or systems for:
* validating data, current state or context
* retrieving or setting data, local or remote
* subscribing to continuous data changes
* doing any sort of operations, long or short running, one-shot or continously emitting

It should be thought of as a self-contained operation with single contextual responsibility. 

<br/>

**RxUseCase** has a standard interface of passing and getting data. It:
* operates with the **Request** parameter, which wraps the input required by the use-case
* emits a **Response** object which encapsulates either a successful output or the failure returned by the use-case

This enables another way of describing use-cases as entities which act as a standard input - standard output proxies to the outside world.

<br/>

<p align="center">
  <img src='blob/doc-how-details.PNG' alt='How it works - Complex' align='middle' width='80%'/>
</p>


## Usage
Extend RxUseCase class or one of its existing implemenations.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://github.com/bakehousedigital/rxusecase/blob/master/LICENSE)
