<#if package??>package ${package};</#if>

import java.util.Set;
import ${entityPackage}.User;
import java.time.Instant;
import javax.validation.constraints.Size;

/**
 * A DTO extending the UserDTO, which is meant to be used in the user management
 * UI.
 */
public class ManagedUserDTO extends UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;
    public static final int PASSWORD_MAX_LENGTH = 100;

    private Long id;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    public ManagedUserDTO() {
    }

    public ManagedUserDTO(User user) {
        super(user);
        this.id = user.getId();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.password = null;
    }

    public ManagedUserDTO(Long id, String login, String password, String firstName, String lastName,
            String email, boolean activated, String langKey, Set<String> authorities, Instant createdDate, String lastModifiedBy, Instant lastModifiedDate) {
        super(login, firstName, lastName, email, activated, langKey, authorities);
        this.id = id;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "ManagedUserDTO{"
                + "id=" + id
                + ", createdDate=" + createdDate
                + ", lastModifiedBy='" + lastModifiedBy + '\''
                + ", lastModifiedDate=" + lastModifiedDate
                + "} " + super.toString();
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
