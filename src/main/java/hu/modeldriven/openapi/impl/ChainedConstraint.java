package hu.modeldriven.openapi.impl;

import com.change_vision.jude.api.inf.model.IValueAttribute;

import java.util.Arrays;
import java.util.List;

public class ChainedConstraint implements AstahConstraint {

    private final List<AstahConstraint> constraints;

    public ChainedConstraint(AstahConstraint ... constraints) {
        this.constraints = Arrays.asList(constraints);
    }

    @Override
    public void apply(IValueAttribute attribute) {
        constraints.forEach(c -> c.apply((attribute)));
    }
}
