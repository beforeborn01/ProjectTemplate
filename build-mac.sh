#!/bin/bash
mvn clean archetype:create-from-project
find ./target -type f -name "pom.xml" | xargs sed -i '.bak' "s/ProjectTemplate/\${parentArtifactId}/g"
find ./ -type f -name "*.bak" | xargs rm
