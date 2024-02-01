package hu.modeldriven.astah.core;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

import java.util.Arrays;

public class AstahModel {

    private final ProjectAccessor projectAccessor;

    public AstahModel() {
        try {
            this.projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public <T extends INamedElement> T findElementByPath(String path, String name, Class<T> typeClass) throws PackageNotFoundException {

        IPackage rootPackage = this.findPackage(path);

        for (INamedElement element : rootPackage.getOwnedElements()) {
            if (name.equals(element.getName()) && typeClass.isInstance(element)) {
                return typeClass.cast(element);
            }
        }

        return null;
    }

    public IPackage findPackage(String path) throws PackageNotFoundException {
        try {
            IPackage root = projectAccessor.getProject();
            return findPackage(root, path);
        } catch (ProjectNotFoundException e) {
            throw new PackageNotFoundException(e);
        }
    }

    public IPackage findPackage(IPackage rootPackage, String path) throws PackageNotFoundException {

        String[] pathElements = path.split("/");

        IPackage lastMatchingPackage = rootPackage;

        for (String currentSubPathName : pathElements) {

            lastMatchingPackage = Arrays.stream(lastMatchingPackage.getOwnedElements())
                    .filter(IPackage.class::isInstance)
                    .map(IPackage.class::cast)
                    .filter(p -> currentSubPathName.equals(p.getName()))
                    .findFirst()
                    .orElse(null);

            if (lastMatchingPackage == null) {
                throw new PackageNotFoundException("Package not found in path " + path + ", subpath " + currentSubPathName);
            }
        }

        return lastMatchingPackage;
    }

}
