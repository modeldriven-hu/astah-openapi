package hu.modeldriven.openapi.impl;

import com.change_vision.jude.api.inf.model.IClass;

public interface AstahModelElement {

    IClass getType();

    AstahConstraint getConstraint();

}
