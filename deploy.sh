/opt/apache-maven-3.8.1/bin/mvn clean package -X
rm /root/keycloak-13.0.1/standalone/deployments/*
cp /root/ldap-attribute-mapper/ldap-attribute-mapper-ear/target/ldap-attribute-mapper-ear.ear /root/keycloak-13.0.1/standalone/deployments/

