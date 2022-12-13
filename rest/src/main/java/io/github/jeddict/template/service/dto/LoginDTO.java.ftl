package ${package};

import static ${appPackage}${Constants_FQN}.LOGIN_REGEX;
import static ${appPackage}${Constants_FQN}.PASSWORD_MAX_LENGTH;
import static ${appPackage}${Constants_FQN}.PASSWORD_MIN_LENGTH;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * A DTO representing a user's credentials
 */
public class LoginDTO {

    @Pattern(regexp = LOGIN_REGEX)
    @NotNull
    @Size(min = 1, max = 50)
    private String username;

    @NotNull
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    private Boolean rememberMe = true;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    @Override
    public String toString() {
        return "LoginDTO{"
                + ", username='" + username + '\''
                + ", rememberMe=" + rememberMe
                + '}';
    }
}
