package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IAttribute;
import io.swagger.v3.oas.models.media.Schema;

public interface SchemaPropertyMetadata {

    void applyTo(String propertyName, Schema<?> blockSchema, Schema<?> propertySchema, IAttribute attribute);

}
