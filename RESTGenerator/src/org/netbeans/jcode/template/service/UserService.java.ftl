<#if package??>package ${package};</#if>

import ${AuthorityRepository_FQN};
import ${UserRepository_FQN};
import ${AuthoritiesConstants_FQN};
import ${PasswordEncoder_FQN};
import ${User_FQN};
import ${Authority_FQN};
import ${SecurityUtils_FQN};
import ${RandomUtil_FQN};
import ${AuthenticationException_FQN};
import ${UserAuthenticationToken_FQN};
import ${UserDTO_FQN};
import java.time.Instant;
import java.time.ZoneId;
import org.slf4j.Logger;
import java.time.ZonedDateTime;
import javax.inject.Inject;
import java.util.*;
import javax.ejb.Stateless;
import static java.util.stream.Collectors.toSet;

/**
 * Service class for managing users.
 */
@Stateless
public class UserService {

    @Inject
    private Logger log;

    @Inject
    private SecurityUtils securityUtils;

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
                .filter(user -> {
                    ZonedDateTime oneDayAgo = ZonedDateTime.now().minusHours(24);
                    ZonedDateTime resetDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(user.getResetDate().getTime()), ZoneId.systemDefault());
                    return resetDate.isAfter(oneDayAgo);
                })
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
                    user.setResetDate(new Date());
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
        String currentLogin = securityUtils.getCurrentUserLogin();
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
        user.setResetDate(new Date());
        user.setActivated(true);
        ${userRepository}.create(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public void updateUser(String firstName, String lastName, String email, String langKey) {
        ${userRepository}.findOneByLogin(securityUtils.getCurrentUserLogin()).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            u.setLangKey(langKey);
            ${userRepository}.edit(u);
            log.debug("Changed Information for User: {}", u);
        });
    }

    public void deleteUserInformation(String login) {
        ${userRepository}.findOneByLogin(login).ifPresent(u -> {
            ${userRepository}.remove(u);
            log.debug("Deleted User: {}", u);
        });
    }

    public void changePassword(String password) {
        ${userRepository}.findOneByLogin(securityUtils.getCurrentUserLogin()).ifPresent(u -> {
            String encryptedPassword = passwordEncoder.encode(password);
            u.setPassword(encryptedPassword);
            ${userRepository}.edit(u);
            log.debug("Changed password for User: {}", u);
        });
    }

    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return ${userRepository}.findOneWithAuthoritiesByLogin(login);
    }

    public User getUserWithAuthorities(Long id) {
        return ${userRepository}.findOneWithAuthoritiesById(id).orElse(null);
    }

    public User getUserWithAuthorities() {
        if (securityUtils.getCurrentUserLogin() == null) {
            return null;
        }
        return ${userRepository}.findOneWithAuthoritiesByLogin(securityUtils.getCurrentUserLogin()).orElse(null);
    }

    public User authenticate(UserAuthenticationToken authenticationToken) throws AuthenticationException {
        Optional<User> userOptional = ${userRepository}.findOneWithAuthoritiesByLogin(authenticationToken.getPrincipal());
        if (userOptional.isPresent() && userOptional.get().getActivated() && userOptional.get().getPassword().equals(passwordEncoder.encode(authenticationToken.getCredentials()))) {
            return userOptional.get();
        } else {
            throw new AuthenticationException();
        }
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
