package hu.modeldriven.openapi.impl;

import java.util.Arrays;

public class SchemaReference {

    private final String reference;

    public SchemaReference(String reference){
        this.reference = reference;
    }

    String getName(){
        return Arrays.stream(reference.split("/"))
                .reduce((first, second) -> second)
                .orElse(null);
    }

}
