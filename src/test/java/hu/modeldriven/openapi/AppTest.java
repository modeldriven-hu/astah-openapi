package hu.modeldriven.openapi;

import astah.AstahRepresentation;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IValueType;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit test for simple App.
 */
public class AppTest {

    public static void main(String[] args) throws Exception {
        OpenAPIParser parser = new OpenAPIParser();

        SwaggerParseResult result = parser.readLocation("https://petstore3.swagger.io/api/v3/openapi.json", null, null);

        //SwaggerParseResult result = parser.readLocation("https://binance.github.io/binance-api-swagger/spot_api.yaml", null, null);

        OpenAPIObject openAPIObject = new OpenAPIObject(result.getOpenAPI());

        AstahRepresentation astahRepresentation = Mockito.mock(AstahRepresentation.class);

        when(astahRepresentation.createBlock(any(), any())).thenReturn(Mockito.mock(IBlock.class));
        when(astahRepresentation.createValueAttribute(any(), any(), any())).thenReturn(Mockito.mock(IAttribute.class));
        when(astahRepresentation.createPartRelationship(any(), any(), any())).thenReturn(Mockito.mock(IAttribute.class));

        when(astahRepresentation.findElementByPath(any(), eq("Integer"), any())).thenReturn(Mockito.mock(IValueType.class));
        when(astahRepresentation.findElementByPath(any(), eq("Boolean"), any())).thenReturn(Mockito.mock(IValueType.class));
        when(astahRepresentation.findElementByPath(any(), eq("String"), any())).thenReturn(Mockito.mock(IValueType.class));
        when(astahRepresentation.findElementByPath(any(), eq("DateTime"), any())).thenReturn(Mockito.mock(IValueType.class));
        when(astahRepresentation.findElementByPath(any(), eq("Number"), any())).thenReturn(Mockito.mock(IValueType.class));

        BuildInstruction instruction = Mockito.mock(BuildInstruction.class);
        when(instruction.astah()).thenReturn(astahRepresentation);
        when(instruction.typeResolver()).thenReturn(new TypeResolver(astahRepresentation));

        openAPIObject.build(instruction);
    }

}
