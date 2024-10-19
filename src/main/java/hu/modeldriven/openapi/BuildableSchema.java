package hu.modeldriven.openapi;

public interface BuildableSchema {

    void buildSchema(String name, BuildContext context);
    void buildProperties(String name, BuildContext context);

}