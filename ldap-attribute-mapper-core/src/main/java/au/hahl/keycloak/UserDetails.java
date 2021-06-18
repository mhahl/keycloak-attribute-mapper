package au.hahl.keycloak;

import java.util.Map;
import java.util.Set;

import lombok.Getter;

/**
 * Represents a user and the users attributes.
 * 
 */
public class UserDetails {

    @Getter
    public String username;
    public Map<String, String[]> attributes;

    public Set<String> getAttributeValues(String attributeName) {
        return Set.of(attributes.get(attributeName));
    }

    public Set<String> getAttrbutes() {
        return attributes.keySet();
    }

}