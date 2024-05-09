package hu.modeldriven.openapi.metadata;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IMultiplicityRange;
import hu.modeldriven.astah.core.AstahRepresentation;
import hu.modeldriven.openapi.SchemaPropertyMetadata;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;

public class MultiplicityMetadata implements SchemaPropertyMetadata {

    private final AstahRepresentation astah;

    public MultiplicityMetadata(AstahRepresentation astah) {
        this.astah = astah;
    }

    @Override
    public void applyTo(String propertyName, Schema<?> propertySchema, Schema<?> blockSchema, IAttribute attribute) {
        if (propertySchema instanceof ArraySchema) {
            astah.setMultiplicity(attribute, 0, IMultiplicityRange.UNLIMITED);
        } else {
            if (blockSchema.getRequired() != null && blockSchema.getRequired().contains(propertyName)) {
                astah.setMultiplicity(attribute, 1);
            } else {
                astah.setMultiplicity(attribute, 0, 1);
            }
        }
    }
}
