package hu.modeldriven.astah.openapi.transform.model.resolver;

import com.change_vision.jude.api.inf.model.IValueType;

public interface TypeResolver {

    public enum CoreType {
        BOOLEAN, INTEGER, STRING
    }

    IValueType findCoreTypeByName(CoreType coreType) throws TypeNotFoundException;

}
