# external-api-mapper

Set a list of user attributes by calling a external service.

# Installation

Place the `ldap-attribute-mapper-ear.ear` into your keycloak `standalone/deploymens` directory.

## How it works

When a user is synced from ldap, the extension will call a external webservice with the following format:

```
POST /<webservice_url>

{ "userName": "<userName>" } 
```

The service should respond with a list of attributes in the following format:


```
Content-Type: application/json

{
    "userName": "<userName>"
    "attributes": {
        "myAttributeName": [
            "myValue1", "myValue2"
        ],
        "example_test": [
            "value" 
        ]
    }
}
```

If any of the attributes are null they will be removed.

```
{
    ...
    "attributes": {
        "myAttribute": null
    }
}

# Development

Download keycloak into the project directory and extract. Modify the `deploy.sh` to copy the 
`ear` files into the correct path.

To build run:
* `mvn clean package`

To run tests:
* `mvn test`

