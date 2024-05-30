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
        var parser = new OpenAPIParser();
        //var result = parser.readLocation("https://petstore3.swagger.io/api/v3/openapi.json", null, null);
        var result = parser.readLocation("C:/home/digivet2.yaml", null, null);
        var openAPIObject = new OpenAPIObject(result.getOpenAPI());
        var astahRepresentation = Mockito.mock(AstahRepresentation.class);

        when(astahRepresentation.createBlock(any(), any())).thenReturn(Mockito.mock(IBlock.class));
        when(astahRepresentation.createValueAttribute(any(), any(), any())).thenReturn(Mockito.mock(IAttribute.class));
        when(astahRepresentation.createPartRelationship(any(), any(), any())).thenReturn(Mockito.mock(IAttribute.class));

        when(astahRepresentation.findElementByPath(any(), eq("Integer"), any())).thenReturn(Mockito.mock(IValueType.class));
        when(astahRepresentation.findElementByPath(any(), eq("Boolean"), any())).thenReturn(Mockito.mock(IValueType.class));
        when(astahRepresentation.findElementByPath(any(), eq("String"), any())).thenReturn(Mockito.mock(IValueType.class));
        when(astahRepresentation.findElementByPath(any(), eq("DateTime"), any())).thenReturn(Mockito.mock(IValueType.class));
        when(astahRepresentation.findElementByPath(any(), eq("Number"), any())).thenReturn(Mockito.mock(IValueType.class));

        var store = new ModelElementsStore();
        var resolver = new TypeResolver(astahRepresentation, store);

        BuildContext instruction = Mockito.mock(BuildContext.class);
        when(instruction.astah()).thenReturn(astahRepresentation);
        when(instruction.typeResolver()).thenReturn(resolver);
        when(instruction.store()).thenReturn(store);

        openAPIObject.build(instruction);
    }

}
