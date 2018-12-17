package statref.model;

import java.util.List;

// TODO
// I need a purpose now. Create Idea plugin. What for?
// For refactoring. What refactoring?
// Inline/extract variable. Flow visualization. Minimize flow.
// Should I create a separate project? It would be great. But not now.
// The main question is which context thing belong and how to move things from one context to another.

public interface SClass extends SType {
    // TODO it should actually always know its package or inclosing class from the context

    String getName();

    List<SType> getGenerics();
}
