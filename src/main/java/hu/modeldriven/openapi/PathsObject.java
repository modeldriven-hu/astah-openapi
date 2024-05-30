package hu.modeldriven.openapi;

import hu.modeldriven.astah.core.AstahLogger;
import io.swagger.v3.oas.models.Paths;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PathsObject {

    private final Map<String, PathObject> pathObjects;

    public PathsObject(Paths paths) {
        if (paths == null) {
            this.pathObjects = new HashMap<>();
        } else {
            this.pathObjects = paths.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> new PathObject(entry.getValue())));
        }
    }

    public void build(BuildContext context) {
        for (var entry : pathObjects.entrySet()) {
            entry.getValue().build(entry.getKey(), context);
        }
    }
}
