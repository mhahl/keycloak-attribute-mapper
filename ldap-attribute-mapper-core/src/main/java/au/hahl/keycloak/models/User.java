package au.hahl.keycloak;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a user and the users attributes.
 * 
 */
public class User {

    public String name;
    public Map<String, String[]> attributes;

    public Set<String> getAttributeValues(String attributeName) {
        return Set.of(attributes.get(attributeName));
    }

    public Set<String> getAttrbutes() {
        return attributes.keySet();
    }

}