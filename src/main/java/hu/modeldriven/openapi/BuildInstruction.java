package hu.modeldriven.openapi;

import hu.modeldriven.astah.core.AstahRepresentation;
import com.change_vision.jude.api.inf.model.IPackage;
import hu.modeldriven.openapi.metadata.MultiplicityMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildInstruction {

    private final IPackage targetPackage;
    private final AstahRepresentation astah;
    private final TypeResolver typeResolver;
    private final List<SchemaPropertyMetadata> schemaPropertyMetadata;

    public BuildInstruction(IPackage targetPackage) {
        this.targetPackage = targetPackage;
        this.astah = new AstahRepresentation();
        this.typeResolver = new TypeResolver(astah);
        this.schemaPropertyMetadata = createSchemaPropertyMetadata(astah);
     }

    private List<SchemaPropertyMetadata> createSchemaPropertyMetadata(AstahRepresentation astahRepresentation) {
        var result = new ArrayList<SchemaPropertyMetadata>();
        result.add(new MultiplicityMetadata(astahRepresentation));
        return result;
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

    List<SchemaPropertyMetadata> schemaPropertyMetadata() {
        return Collections.emptyList();
    }

}
