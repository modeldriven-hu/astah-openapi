package hu.modeldriven.astah;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public class TestParser {

    public static void main(String[] args) throws Exception {
        OpenAPIParser parser = new OpenAPIParser();

        SwaggerParseResult result = parser.readLocation("https://petstore3.swagger.io/api/v3/openapi.json", null, null);

        result.getMessages().forEach(System.out::println);
    }
}
