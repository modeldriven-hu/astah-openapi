package hu.modeldriven.astah.openapi.transform.model.constraint;

import com.change_vision.jude.api.inf.model.IValueAttribute;

public class NoConstraint implements AstahConstraint {

    @Override
    public void apply(IValueAttribute attribute) {
        // do nothing
    }
}
