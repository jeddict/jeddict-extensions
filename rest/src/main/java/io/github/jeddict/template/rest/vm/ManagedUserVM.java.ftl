package ${package};

import java.time.Instant;
import java.util.Set;
import javax.validation.constraints.Size;
import ${appPackage}${UserDTO_FQN};
import static ${appPackage}${Constants_FQN}.PASSWORD_MAX_LENGTH;
import static ${appPackage}${Constants_FQN}.PASSWORD_MIN_LENGTH;

/**
 * A VM extending the UserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserVM extends UserDTO {

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    public ManagedUserVM() {
    }

    public ManagedUserVM(
            Long        id,
            String      login,
            String      password,
            String      firstName,
            String      lastName,
            String      email,
            boolean     activated,
            String      langKey,
            String      createdBy,
            Instant     createdDate,
            String      lastModifiedBy,
            Instant     lastModifiedDate,
            Set<String> authorities
    ) {
        super(
                id,
                login,
                firstName,
                lastName,
                email,
                activated,
                langKey,
                createdBy,
                createdDate,
                lastModifiedBy,
                lastModifiedDate,
                authorities
        );
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "ManagedUserVM : " + super.toString();
    }
}
