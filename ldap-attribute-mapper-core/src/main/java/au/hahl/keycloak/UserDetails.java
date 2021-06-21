package au.hahl.keycloak;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.ToString;

/**
 * Represents a user and the users attributes.
 * @author Mark Hahl <mark@hahl.id.au>
 */
@ToString
public class UserDetails {

    @Getter
    public String username;
    public Map<String, String[]> attributes;
    public List<String> groups;

    /**
     * Return a list of values fr an atribute.
     * @param attributeName
     * @return Set<String> of attribute values.
     */
    public Set<String> getAttributeValues(String attributeName) {
        return Set.of(attributes.get(attributeName));
    }

    /**
     * Return a list of all attrbutes.
     * @return Set<String> of atributes.
     */
    public Set<String> getAttrbutes() {
        return attributes.keySet();
    }

}