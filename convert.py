import re

def convert_to_maven_dependency(line):
    print("Splitting line" + line)
    parts = line.strip().split(':')
    group_id = parts[0]
    artifact_id = parts[1]
    version = parts[3]
    return f'<dependency>\n' \
           f'    <groupId>{group_id}</groupId>\n' \
           f'    <artifactId>{artifact_id}</artifactId>\n' \
           f'    <version>{version}</version>\n' \
           f'</dependency>'

def convert_file_to_maven_dependencies(file_path):
    with open(file_path, 'r') as file:
        lines = file.readlines()

    maven_dependencies = [convert_to_maven_dependency(line) for line in lines]

    return '\n'.join(maven_dependencies)

if __name__ == "__main__":
    input_file_path = "dependency-list.txt"
    output = convert_file_to_maven_dependencies(input_file_path)

    with open("dependencies.xml", 'w') as output_file:
        output_file.write(output)

