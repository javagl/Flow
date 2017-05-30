# Flow

Libraries for flow-based programming

**Note:** These libraries are still subject to change.

![FlowScreenshot01.png](/images/FlowScreenshot01.png)

## Quick start

Clone this repository, install it with Maven, create the samples package,
and start the demo application:

    git clone https://github.com/javagl/Flow.git
    cd Flow
    mvn clean install
    cd flow-samples
    mvn assembly:assembly
    cd target
    java -jar flow-samples-0.0.1-SNAPSHOT-demoJar.jar
    
That's it.

 

## Overview

This repository contains the following sub-projects:

* [flow-core](https://github.com/javagl/Flow/tree/master/flow-core) : 
  The core library, containing the basic classes for flow-based programming:
  A `Flow` that consists of `Module` objects, where two modules can be
  connected with a `Link`, and which may be executed with a `FlowExecutor`.

* [flow-module-creation](https://github.com/javagl/Flow/tree/master/flow-module-creation) : 
  Classes related to the `ModuleCreator` interface - a factory that can create
  `Module` instances.

* [flow-workspace](https://github.com/javagl/Flow/tree/master/flow-workspace) :
  Classes for managing the infrastructure that a flow-based-programming 
  application can be based upon: A `FlowWorkspace` that describes an 
  (editable) state of the `Flow` that was created with the application. 

* [flow-gui](https://github.com/javagl/Flow/tree/master/flow-gui) :
  An application offering a graphical user interface for visual-interactive
  flow-based programming
  
* [flow-io](https://github.com/javagl/Flow/tree/master/flow-io) :
  Classes for reading and writing XML files that contain 
  flow definitions. 

* [flow-module-definitions-basic](https://github.com/javagl/Flow/tree/master/flow-module-definitions-basic) :
  Some basic `Module` definitions, for modules that allow entering 
  values or print objects to the console.
  
* [flow-samples](https://github.com/javagl/Flow/tree/master/flow-samples) :
  Example applications showing how to use the flow library
  
See the `README.md` of each project for further information.



## The local Maven repository

Some of the libraries depend on other libraries that are not yet available
on Maven Central, or on SNAPSHOT versions of existing libraries. These
libraries are contained in the `localMavenRepository`, and the `pom.xml`
files of each sub-project points to this repository. The libraries are

- [Category](https://github.com/javagl/Category)
- [Common](https://github.com/javagl/Common/)
- [CommonUI](https://github.com/javagl/CommonUI/)
- [Reflection](https://github.com/javagl/Reflection)
- [Types](https://github.com/javagl/Types) 

As soon as these libraries are in Maven Central, the local repository
will be omitted.
 
 
## Implementing custom modules

**TODO** Explain


## Notes about type inference

**TODO** Just make clear that it is **really** hard...


## Design choices

**TODO** Elaborate

- No `public` classes for the model, no `public` constructors
- Everything is observable via `...Listener` interfaces
- ... 

## Future tasks

**TODO** Add some ideas here 
   
  
   
 


