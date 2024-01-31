package hu.modeldriven.openapi.impl;

import com.change_vision.jude.api.inf.model.IValueAttribute;

public class AstahMultiplicity implements AstahConstraint{

    private final Multiplicity multiplicity;

    public AstahMultiplicity(Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    public void apply(IValueAttribute attribute){
    }

}
