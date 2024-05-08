package hu.modeldriven.astah.openapi.transform.model.constraint;

import com.change_vision.jude.api.inf.model.IValueAttribute;

public interface AstahConstraint {
    void apply(IValueAttribute attribute);
}
