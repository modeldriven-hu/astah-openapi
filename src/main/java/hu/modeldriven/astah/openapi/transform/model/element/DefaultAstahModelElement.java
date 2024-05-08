package hu.modeldriven.astah.openapi.transform.model.element;

import com.change_vision.jude.api.inf.model.IClass;
import hu.modeldriven.astah.openapi.transform.model.constraint.AstahConstraint;

public class DefaultAstahModelElement implements AstahModelElement {

    private final IClass type;
    private final AstahConstraint constraint;

    public DefaultAstahModelElement(IClass type, AstahConstraint constraint) {
        this.type = type;
        this.constraint = constraint;
    }

    @Override
    public IClass getType() {
        return type;
    }

    @Override
    public AstahConstraint getConstraint() {
        return constraint;
    }
}
