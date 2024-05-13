package hu.modeldriven.openapi;

import com.change_vision.jude.api.inf.model.IPackage;
import hu.modeldriven.astah.core.AstahRepresentation;
import hu.modeldriven.openapi.metadata.MultiplicityMetadata;

import java.util.List;

public class BuildContext {

    private final IPackage targetPackage;
    private final AstahRepresentation astah;
    private final TypeResolver typeResolver;
    private final List<SchemaPropertyMetadata> schemaPropertyMetadata;
    private final ModelElementsStore store;

    public BuildContext(IPackage targetPackage, AstahRepresentation astah) {
        this.targetPackage = targetPackage;
        this.astah = astah;
        this.store = new ModelElementsStore();
        this.typeResolver = new TypeResolver(astah, store);
        this.schemaPropertyMetadata = createSchemaPropertyMetadata(astah);
    }

    private List<SchemaPropertyMetadata> createSchemaPropertyMetadata(AstahRepresentation astahRepresentation) {
        return List.of(new MultiplicityMetadata(astahRepresentation));
    }

    public IPackage targetPackage() {
        return targetPackage;
    }

    public AstahRepresentation astah() {
        return astah;
    }

    public TypeResolver typeResolver() {
        return typeResolver;
    }

    public List<SchemaPropertyMetadata> schemaPropertyMetadata() {
        return schemaPropertyMetadata;
    }

    public ModelElementsStore store() {
        return store;
    }

}
