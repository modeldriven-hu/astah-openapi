package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IClass;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModelElementsStore {

    private final Map<String, IClass> modelElements;

    public ModelElementsStore() {
        this.modelElements = new LinkedHashMap<>();
    }

    public void put(String key, IClass value) {
        this.modelElements.put(key, value);
    }

    public IClass get(String name) {
        return this.modelElements.get(name);
    }
}
