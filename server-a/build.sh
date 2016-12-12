gradle clean
rm -rf build/
rm -rf */build/
rm -rf rest-api/src/main/idl/
rm -rf rest-api/src/main/snapshot/

gradle rest-api:build rest-impl:build server-a-war:build
gradle rest-client:build  ## client bindings has to be generated in a seperate gradle task
