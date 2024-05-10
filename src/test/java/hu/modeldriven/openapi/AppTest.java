package hu.modeldriven.openapi;

import hu.modeldriven.astah.core.AstahRepresentation;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IValueType;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AppTest {

    @Test
    public void testParse() throws Exception {
        OpenAPIParser parser = new OpenAPIParser();

        SwaggerParseResult result = parser.readLocation("https://petstore3.swagger.io/api/v3/openapi.json", null, null);

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

        TypeResolver resolver = new TypeResolver(astahRepresentation);

        BuildContext instruction = Mockito.mock(BuildContext.class);
        when(instruction.astah()).thenReturn(astahRepresentation);
        when(instruction.typeResolver()).thenReturn(resolver);

        openAPIObject.build(instruction);
    }

}
