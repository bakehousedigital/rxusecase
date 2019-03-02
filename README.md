[ ![jcenter](https://api.bintray.com/packages/rrdev/RxUseCase/digital.bakehouse.rxusecase/images/download.svg?version=0.9) ](https://bintray.com/rrdev/RxUseCase/digital.bakehouse.rxusecase/0.9/link)[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
# RxUseCase

RxUseCase is a tiny Java library for wrapping business logic into reactive streams which expose data in a standardized way.

The library provides the core components for implementing the Use Cases (also known as Interactors) of  
[Uncle Bob's Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) approach, which orchestrate the flow of data to and from the entities and represent the essence of a typical domain layer. 

<p align="center">
  <img src='https://antonioleiva.com/wp-content/uploads/2018/09/clean-architecture-graph.png' alt='Clean Architecture Onion' align='middle' />
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

## Usage
Extend RxUseCase class or one of its existing implemenations.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
