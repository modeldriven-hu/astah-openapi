package hu.modeldriven.astah.openapi.transform.model.type.resolver;

import com.change_vision.jude.api.inf.model.IValueType;
import hu.modeldriven.astah.core.AstahModel;
import hu.modeldriven.astah.core.PackageNotFoundException;

public class AstahTypeResolver implements TypeResolver {

    public static final String OPEN_API_PATH = "OpenAPI";
    private final AstahModel model;

    public AstahTypeResolver() {
        this.model = new AstahModel();
    }

    @Override
    public IValueType findByName(CoreType coreType) throws TypeNotFoundException {

        try {

            switch (coreType) {

                case STRING:
                    return model.findElementByPath(OPEN_API_PATH, "String", IValueType.class);

                case BOOLEAN:
                    return model.findElementByPath(OPEN_API_PATH, "Boolean", IValueType.class);

                case INTEGER:
                    return model.findElementByPath(OPEN_API_PATH, "Integer", IValueType.class);

                case DATETIME:
                    return model.findElementByPath(OPEN_API_PATH, "DateTime", IValueType.class);

            }

        } catch (PackageNotFoundException e) {
            throw new TypeNotFoundException(e);
        }

        return null;
    }
}
