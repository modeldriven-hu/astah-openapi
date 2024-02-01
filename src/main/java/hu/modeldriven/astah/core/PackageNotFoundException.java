package hu.modeldriven.astah.core;

public class PackageNotFoundException extends Exception {

    public PackageNotFoundException(Exception e){
        super(e);
    }

    public PackageNotFoundException(String s) {
        super(s);
    }
}
