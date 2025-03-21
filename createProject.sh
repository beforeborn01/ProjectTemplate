# 根据模板创建测试项目
cd ~/code/ttt || exit
rm -rf test
mvn archetype:generate -DarchetypeCatalog=local -DgroupId=com.bc.meet  -Dversion=1.0-SNAPSHOT  -DartifactId=meet -DProjectName=Meet
