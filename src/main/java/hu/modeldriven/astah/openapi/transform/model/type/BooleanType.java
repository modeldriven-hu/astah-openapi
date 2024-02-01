package hu.modeldriven.astah.openapi.transform.model.type;

import hu.modeldriven.astah.openapi.transform.model.constraint.NoConstraint;
import hu.modeldriven.astah.openapi.transform.model.element.AstahModelElement;
import hu.modeldriven.astah.openapi.transform.model.element.DefaultAstahModelElement;
import hu.modeldriven.astah.openapi.transform.model.modelapi.ModelBuildingException;
import hu.modeldriven.astah.openapi.transform.model.resolver.TypeResolver;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.Schema;

public class BooleanType implements OpenAPIType{

    private final TypeResolver resolver;

    public BooleanType(TypeResolver resolver){
        this.resolver = resolver;
    }

    @Override
    public boolean appliesTo(Schema schema) {
        return schema instanceof BooleanSchema;
    }

    @Override
    public AstahModelElement create(Schema parent, Schema element) throws ModelBuildingException {
        try {
            return new DefaultAstahModelElement(
                    resolver.findCoreTypeByName(TypeResolver.CoreType.BOOLEAN),
                    new NoConstraint());
        } catch (Exception e){
            throw new ModelBuildingException(e);
        }
    }
}
