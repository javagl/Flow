# flow-repository

**Note** : This package might be moved into a standalone library in the future!

A simple repository implementation. A repository is similar to a `Map`,
but the key for each value is computed using a function, and there is
an internal reference counter for the values.

In the context of the Flow library, the `Repository` is used as a 
repository of all available `ModuleCreator` instances, indexed 
by the `ModuleInfo` (so the specific type that is used here is
`Repository<ModuleInfo, ModuleCreator>`). 

The repository may be looked up, in order to find the `ModuleCreator`
that created a certain `Module` instance, by passing the `ModuleInfo`
of the `Module` to the repository:

    Module module = ...;
    ModuleCreator thatCreatedTheModule =
        repository.get(module.getModuleInfo());

        