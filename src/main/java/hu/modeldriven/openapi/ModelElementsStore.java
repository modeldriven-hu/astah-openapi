package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IClass;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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

    public Set<Map.Entry<String, IClass>> entrySet() {
        return this.modelElements.entrySet();
    }

    public IClass computeIfAbsent(String key, Function<? super String, ? extends IClass> mappingFunction) {
        return this.modelElements.computeIfAbsent(key, mappingFunction);
    }

}
