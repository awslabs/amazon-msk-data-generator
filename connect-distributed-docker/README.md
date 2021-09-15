# Building and Publishing MSK Data Generator Image to AWS ECR

### Improvement opportunities

You'll notice hard-code of "msk-data-generator-0.4-jar-with-dependencies.jar" 
in the Dockerfile. 

This means you need to run `mvn package` and copy the jar from target/ to here, before 
building the image.

Could this better and avoid the manual step?  Probably, yes. Let us know if you have suggestions.  


