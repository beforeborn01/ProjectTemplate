#!/bin/bash
mvn clean archetype:create-from-project
find ./target -type f -name "pom.xml" | xargs sed -i '.bak' "s/ProjectTemplate/\${parentArtifactId}/g"
find ./target -type f -name "pom.xml" | xargs sed -i '.bak' "s/template/\${rootArtifactId}/g"
#find ./target/**/*web -type f -name "pom.xml" | xargs sed -i '.bak' "s/com.bc/\${rootArtifactId}/g"
find ./target/generated-sources/archetype/src/main/resources/archetype-resources/__rootArtifactId__-web/pom.xml | xargs sed -i '.bak' "s/com.bc/\${groupId}/g"
find ./target/generated-sources/archetype/target/classes/archetype-resources/__rootArtifactId__-web/pom.xml | xargs sed -i '.bak' "s/com.bc/\${groupId}/g"
find ./ -type f -name "*.bak" | xargs rm
echo "***************create end***************"
cd target/generated-sources/archetype/
mvn clean install
mvn deploy
echo "***************archetype install end***************"


