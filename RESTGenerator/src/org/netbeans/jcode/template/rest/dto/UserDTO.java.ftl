<#if package??>package ${package};</#if>

import ${Constants_FQN};
import ${entityPackage}.Authority;
import ${entityPackage}.User;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO implements Serializable {

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email(regexp = Constants.EMAIL_REGEX, message = Constants.EMAIL_REGEX_MESSAGE)
    @Size(min = 5, max = 100)
    private String email;

    private boolean activated = false;

    @Size(min = 2, max = 5)
    private String langKey;

    private Set<String> authorities;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this(user.getLogin(), user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getActivated(), user.getLangKey(),
                user.getAuthorities().stream().map(Authority::getName)
                .collect(Collectors.toSet()));
    }

    public UserDTO(String login, String firstName, String lastName,
            String email, boolean activated, String langKey, Set<String> authorities) {

        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.activated = activated;
        this.langKey = langKey;
        this.authorities = authorities;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActivated() {
        return activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    @Override
    public String toString() {
        return "UserDTO{"
                + "login='" + login + '\''
                + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", email='" + email + '\''
                + ", activated=" + activated
                + ", langKey='" + langKey + '\''
                + ", authorities=" + authorities
                + "}";
    }

    /**
     * @param login the login to set
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @param activated the activated to set
     */
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    /**
     * @param langKey the langKey to set
     */
    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    /**
     * @param authorities the authorities to set
     */
    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

}
