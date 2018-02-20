package ${package};

import ${appPackage}${AuthorityRepository_FQN};
import ${appPackage}${UserRepository_FQN};
import ${appPackage}${AuthoritiesConstants_FQN};
import ${appPackage}${PasswordEncoder_FQN};
import ${appPackage}${User_FQN};
import ${appPackage}${Authority_FQN};
import ${appPackage}${SecurityHelper_FQN};
import ${appPackage}${RandomUtil_FQN};
import ${appPackage}${UserDTO_FQN};
import java.time.Instant;
import java.util.*;
import static java.util.stream.Collectors.*;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import org.slf4j.Logger;

/**
 * Service class for managing users.
 */
public class UserService {

    @Inject
    private Logger log;

    @Inject
    private SecurityHelper securityHelper;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private ${UserRepository} ${userRepository};

    @Inject
    private ${AuthorityRepository} ${authorityRepository};

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return ${userRepository}.findOneByActivationKey(key)
                .map(user -> {
                    // activate given user for the registration key.
                    user.setActivated(true);
                    user.setActivationKey(null);
                    ${userRepository}.edit(user);
                    log.debug("Activated user: {}", user);
                    return user;
                });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return ${userRepository}.findOneByResetKey(key)
                .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400))) // minus 24 hours
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetDate(null);
                    ${userRepository}.edit(user);
                    return user;
                });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return ${userRepository}.findOneByEmail(mail)
                .filter(User::getActivated)
                .map(user -> {
                    user.setResetKey(RandomUtil.generateResetKey());
                    user.setResetDate(Instant.now());
                    ${userRepository}.edit(user);
                    return user;
                });
    }

    public User createUser(String login, String password, String firstName, String lastName, String email,
            String langKey) {

        User newUser = new User();
        Authority authority = ${authorityRepository}.find(AuthoritiesConstants.USER);
        Set<Authority> authorities = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        String currentLogin = securityHelper.getCurrentUserLogin();
        newUser.setCreatedBy(currentLogin != null ? currentLogin : AuthoritiesConstants.ANONYMOUS);
        ${userRepository}.create(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        if (userDTO.getLangKey() == null) {
            user.setLangKey("en"); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        if (userDTO.getAuthorities() != null) {
            user.setAuthorities(userDTO.getAuthorities().stream().map(${authorityRepository}::find).collect(toSet()));
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        ${userRepository}.create(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public void updateUser(String firstName, String lastName, String email, String langKey) {
        ${userRepository}.findOneByLogin(securityHelper.getCurrentUserLogin())
            .ifPresent(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setLangKey(langKey);
                    ${userRepository}.edit(user);
                log.debug("Changed Information for User: {}", user);
            });
    }

    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(${userRepository}
                .find(userDTO.getId()))
                .map(user -> {
                    user.setLogin(userDTO.getLogin());
                    user.setFirstName(userDTO.getFirstName());
                    user.setLastName(userDTO.getLastName());
                    user.setEmail(userDTO.getEmail());
                    user.setActivated(userDTO.isActivated());
                    user.setLangKey(userDTO.getLangKey());
                    user.setAuthorities(userDTO.getAuthorities()
                            .stream()
                            .map(${authorityRepository}::find)
                            .collect(toSet())
                    );
                    ${userRepository}.edit(user);
                    log.debug("Changed Information for User: {}", user);
                    return user;
                })
                .map(UserDTO::new);
    }

    public void deleteUser(String login) {
        ${userRepository}.findOneByLogin(login).ifPresent(user -> {
            ${userRepository}.remove(user);
            log.debug("Deleted User: {}", user);
        });
    }

    public void changePassword(String password) {
        ${userRepository}.findOneByLogin(securityHelper.getCurrentUserLogin()).ifPresent(user -> {
            String encryptedPassword = passwordEncoder.encode(password);
            user.setPassword(encryptedPassword);
            ${userRepository}.edit(user);
            log.debug("Changed password for User: {}", user);
        });
    }

    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return ${userRepository}.findOneWithAuthoritiesByLogin(login);
    }

    public User getUserWithAuthorities(Long id) {
        return ${userRepository}.findOneWithAuthoritiesById(id).orElse(null);
    }

    public User getUserWithAuthorities() {
        return ${userRepository}.findOneWithAuthoritiesByLogin(securityHelper.getCurrentUserLogin()).orElse(null);
    }

    public User authenticate(UsernamePasswordCredential credential) throws AuthenticationException {
        Optional<User> userOptional = ${userRepository}.findOneWithAuthoritiesByLogin(credential.getCaller());
        return userOptional.filter(User::getActivated)
                .filter(user -> user.getPassword().equals(passwordEncoder.encode(credential.getPasswordAsString())))
                .orElseThrow(AuthenticationException::new);
    }

    /**
     * @return a list of all the authorities
     */
    public List<String> getAuthorities() {
        return ${authorityRepository}.findAll()
                .stream()
                .map(Authority::getName)
                .collect(toList());
    }

<#--    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
//    @Schedule(hour="0")
//    @Timeout
//    public void removeNotActivatedUsers() { todo timer
//        ZonedDateTime now = ZonedDateTime.now();
//        List<User> users = ${userRepository}.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
//        for (User user : users) {
//            log.debug("Deleting not activated user {}", user.getLogin());
//            ${userRepository}.remove(user);
//        }
//    } -->
}
