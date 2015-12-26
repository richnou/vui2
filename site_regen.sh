
#mvn site -Dgithub.site.dryRun=true
mvn pre-site org.apache.maven.plugins:maven-site-plugin:site org.apache.maven.plugins:maven-site-plugin:stage org.apache.maven.plugins:maven-site-plugin:run 
