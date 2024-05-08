package hu.modeldriven.astah;

import hu.modeldriven.astah.openapi.transform.model.modelapi.ModelAPI;
import hu.modeldriven.astah.openapi.transform.model.schema.OpenAPISchemas;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public class TestParser {

    public static void main(String[] args) throws Exception {
        OpenAPIParser parser = new OpenAPIParser();

        SwaggerParseResult result = parser.readLocation("https://petstore3.swagger.io/api/v3/openapi.json", null, null);

        Components components = result.getOpenAPI().getComponents();

        ModelAPI modelAPI = null;

        OpenAPISchemas schemas = new OpenAPISchemas(components.getSchemas());
        schemas.build(modelAPI);

        components.getSchemas().keySet().forEach(System.err::println);
    }
}
