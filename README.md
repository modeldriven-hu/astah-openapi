# Astah SysML OpenAPI importer

The plugin enables importing OpenAPI specifications into Astah SysML tool in order to model software systems.

# Usage

- Install the plugin
- Create a project
- Create a new package
- Select the package
- Select Tools -> OpenAPI -> Import OpenAPI
- Select the OpenAPI specification (either json or yaml, for example https://petstore3.swagger.io/api/v3/openapi.json )
- Press Open

# How does it work

- If not exists, a package with name OpenAPI will be created in the project
- This package will contain the basic types (Boolean, DateTime, Email, Integer, etc.)
- For each component (a kind of DTO) a block will be created with the corresponding fields, types and multiplicity in the selected package
- For each endpoint's operation a Request and Response block will be created with the name of the operation as prefix
- For each tag an interface block is created with << REST Interface >> stereotype
- Each endpoint's operation an operation of the corresponding block will be created with matching name
- This block operation will have a request parameter of type <Operation>Request and return parameter of type <Operation>Response
- The operation will be also stereotyped according to the HTTP Action (Get, Post, Put, etc.)
- For each operation a separate block definition diagram is created

 # Screenshot

 ![image](https://github.com/modeldriven-hu/astah-openapi/assets/8182138/d102f3d0-3c7a-4cd5-89b3-08730ddb66f3)

 # Limitations

 - It is required to have an operation for every endpoint, otherwise the corresponding block operation cannot be created
 - Due to limitations of Astah API adding the additional metadata (minLength, url, etc.) as a tagged value of a stereotype is not yet possible
 - The situation when two component reference each other is not handled - yet: remove one of the references and add them by hand
 - Update of existing model is not yet handled
 - There might be situations which are not yet handled
 - Code is not as nice as it should be

