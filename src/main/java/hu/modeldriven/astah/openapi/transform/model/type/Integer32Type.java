package hu.modeldriven.astah.openapi.transform.model.type;

import hu.modeldriven.astah.openapi.transform.model.constraint.NoConstraint;
import hu.modeldriven.astah.openapi.transform.model.element.AstahModelElement;
import hu.modeldriven.astah.openapi.transform.model.element.DefaultAstahModelElement;
import hu.modeldriven.astah.openapi.transform.model.modelapi.ModelBuildingException;
import hu.modeldriven.astah.openapi.transform.model.resolver.TypeResolver;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;

public class Integer32Type implements OpenAPIType{

    private final TypeResolver resolver;

    public Integer32Type(TypeResolver resolver){
        this.resolver = resolver;
    }

    @Override
    public boolean appliesTo(Schema schema) {
        return schema instanceof IntegerSchema && "int32".equals(schema.getFormat());
    }

    @Override
    public AstahModelElement create(Schema parent, Schema element) throws ModelBuildingException {
        try {
            return new DefaultAstahModelElement(
                    resolver.findCoreTypeByName(TypeResolver.CoreType.INTEGER),
                    new NoConstraint());
        } catch (Exception e){
            throw new ModelBuildingException(e);
        }
    }
}
