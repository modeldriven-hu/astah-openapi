package hu.modeldriven.astah.openapi.transform.model.type.resolver;

import com.change_vision.jude.api.inf.model.IValueType;

public interface TypeResolver {

    IValueType findByName(CoreType coreType) throws TypeNotFoundException;

    enum CoreType {
        BOOLEAN, INTEGER, STRING, DATETIME
    }

}
