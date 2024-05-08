package hu.modeldriven.astah.openapi.transform.model.type;

import hu.modeldriven.astah.openapi.transform.model.type.resolver.AstahTypeResolver;
import hu.modeldriven.astah.openapi.transform.model.type.resolver.TypeResolver;

import java.util.Arrays;
import java.util.List;

public class TypeLibrary {

    public List<OpenAPIType> getTypes() {
        TypeResolver resolver = new AstahTypeResolver();

        return Arrays.asList(
                new StringType(resolver),
                new BooleanType(resolver),
                new Integer32Type(resolver),
                new Integer64Type(resolver),
                new DateTimeType(resolver)
        );
    }

}
