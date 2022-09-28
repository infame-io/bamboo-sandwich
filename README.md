.credentials
```bash
username=<username>
password=<password>
```
```bash
mvn archetype:generate -B \
    -DarchetypeGroupId=com.atlassian.bamboo \
    -DarchetypeArtifactId=bamboo-specs-archetype \
    -DarchetypeVersion=8.2.6 \
    -DgroupId=com.atlassian.bamboo \
    -DartifactId=bamboo-sandwich -Dversion=0.0.1-TEST \
    -Dpackage=sandwich -Dtemplate=minimal
```
