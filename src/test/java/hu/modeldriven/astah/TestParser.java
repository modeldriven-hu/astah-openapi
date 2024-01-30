package hu.modeldriven.astah;

import hu.modeldriven.openapi.impl.OpenAPISchemas;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public class TestParser {

    public static void main(String[] args) throws Exception {
        OpenAPIParser parser = new OpenAPIParser();

        SwaggerParseResult result = parser.readLocation("https://petstore3.swagger.io/api/v3/openapi.json", null, null);

        Components components = result.getOpenAPI().getComponents();

        OpenAPISchemas schemas = new OpenAPISchemas(components.getSchemas());
        schemas.build();

        components.getSchemas().keySet().forEach(System.err::println);
    }
}
