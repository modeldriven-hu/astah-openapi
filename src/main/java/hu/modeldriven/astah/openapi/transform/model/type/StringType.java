package hu.modeldriven.astah.openapi.transform.model.type;

import hu.modeldriven.astah.openapi.transform.model.ModelBuildingException;
import hu.modeldriven.astah.openapi.transform.model.element.AstahModelElement;
import hu.modeldriven.astah.openapi.transform.model.resolver.TypeResolver;
import hu.modeldriven.astah.openapi.transform.model.constraint.NoConstraint;
import hu.modeldriven.astah.openapi.transform.model.element.DefaultAstahModelElement;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

public class StringType implements OpenAPIType {

    private final TypeResolver resolver;

    public StringType(TypeResolver resolver){
        this.resolver = resolver;
    }

    @Override
    public boolean appliesTo(Schema schema) {
        return schema instanceof StringSchema && schema.getType() == null;
    }

    @Override
    public AstahModelElement create(Schema parent, Schema element) throws ModelBuildingException {
        try {

            return new DefaultAstahModelElement(
                    resolver.findCoreTypeByName(TypeResolver.CoreType.STRING),
                    new NoConstraint());
        } catch (Exception e){
            throw new ModelBuildingException(e);
        }
    }
}
