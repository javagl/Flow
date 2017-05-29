# flow-module-creation

Classes related to the creation of `Module` instances, using implementations
of the
[`ModuleCreator`](https://github.com/javagl/Flow/blob/master/flow-module-creation/src/main/java/de/javagl/flow/module/creation/ModuleCreator.java)
 interface, which can be discovered with a simple plugin concept
 

## The `ModuleCreator` interface

The `flow-core` package contains the `Module` interface, and it is possible
to create `Module` instances by implementing this interface or using the
static factory methods of the `Modules` class. But in the context of a 
larger application, the creation of `Module` instances must be possible
dynamically, at runtime. 
 
The `ModuleCreator` interface is responsible for this kind of creation of 
`Module` instances: It has the main method `createModule`, which creates 
the `Module` instance.

### Creating `ModuleCreator` instances

The `ModuleCreators` class contains static factory methods to create
predefined types of `ModuleCreator` instances:

- `ModuleCreators#createForMethod`
- `ModuleCreators#createForConstructor`

### Creation of `ModuleView` instances with a `ModuleCreator`

When `Module` instances have to be created dynamically in an application,
there will usually be a visual representation of these `Module` instances.
This visual representation may consist of multiple parts (or components)
that show different aspects of the `Module`. Thus, the `ModuleCreator`
interface also offers methods to create `ModuleView` instances for 
a certain type of `Module`. See the section about the `ModuleView`
interface for details.

### Custom `ModuleCreator` implementations 

Custom implementations of the `ModuleCreator` interface should usually
be created by extending the `AbstractModuleCreator` class. This class
offers basic functionality that is required for the persistence of 
the `ModuleCreator` types. 

**Important implementation note:** In order to make sure that the persistence 
of `ModuleCreator` implementations works as expected, each custom
`ModuleCreator` implementation should be a `public` class with
a `public` default (no-arguments) constructor!


### Persistence of `ModuleCreator` instances

This information is an implementation detail and should not be relevant
for clients, but mentioned here for completeness: 

For persistence, each `ModuleCreator` has an associated "instantiation
string". This is a string that describes (in an **unspecified** form!)
how to create an instance of the `ModuleCreator`. This string may
be passed to the `ModuleCreatorInstantiator`, to create a 
`ModuleCreator` instance.

Particularly, for custom `ModuleCreator` implementations, this 
instantiation string will contain the **class name** of the implementing
class, which will be used for creating the actual instance. This is
why the custom `ModuleCreator` implementations must be `public` 
classes with a `public` default (no-arguments) constructor.
 

### Providing multiple `ModuleCreator` types with a plugin

Implementors of custom modules usually will provide a set of module types
that cover a certain application case. For example, someone might offer
modules for image processing tasks, including the task of loading an
image from a file, performing arithmetic operations on multiple images,
or changing the image size.

In order to offer a collection of `ModuleCreator` implementations for
a certain category of tasks, the `ModuleCreatorSource` interface can
be implemented. This implementation will usually be based on the 
`AbstractModuleCreatorSource` class, which offers a method to
summarize multiple `ModuleCreator` instances:

    public class ImageModuleCreatorSource 
        extends AbstractModuleCreatorSource 
        implements ModuleCreatorSource
    {
        public BasicModuleCreatorSource()
        {
            super("Image Operations");
        }
    
        protected void addModuleCreators()
        {
            add(new ImageLoaderModuleCreator());
            add(new ImageViewerModuleCreator());
      
            get("Filter").add(new ImageEdgeDetectorModuleCreator()); 
            get("Filter").add(new ImageEmbossModuleCreator()); 
        }
    }

These instances will then eventually be offered as a hierarchical `Category`.
 
Implementations of the `ModuleCreatorSource` interface will automatically
be discovered when they are on the classpath, using a 
[`ServiceLoader`](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html). Therefore,
the library may provide a file

    META-INF/services/e.javagl.flow.plugin.ModuleCreatorSource
    
that contains the names of the implementation - for example,

    com.example.ImageModuleCreatorSource
    
The `ModuleCreatorServices` class offers a convenience method for putting
the `ModuleCreator` implementations that are provided by these services
into an existing `Category` hierarchy.


## The `ModuleView` interface

**TODO** Details.

**Note** The `ModuleView` interface does not belong here. It may be moved 
into a different project in the future.

 

