package hu.modeldriven.astah.openapi.transform.model.element;

import com.change_vision.jude.api.inf.model.IClass;
import hu.modeldriven.astah.openapi.transform.model.constraint.AstahConstraint;

public interface AstahModelElement {

    IClass getType();

    AstahConstraint getConstraint();

}
