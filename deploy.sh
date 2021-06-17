mvn clean package -X
rm ./keycloak-13.0.1/standalone/deployments/*
cp ./ldap-attribute-mapper-ear/target/ldap-attribute-mapper-ear.ear ./keycloak-13.0.1/standalone/deployments/

