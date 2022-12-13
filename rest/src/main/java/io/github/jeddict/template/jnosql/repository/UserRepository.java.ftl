package ${package};

<#if cdi>import jakarta.enterprise.context.Dependent;</#if><#if !cdi>import jakarta.ejb.Stateless;</#if>
<#if named>import jakarta.inject.Named;</#if>
import ${appPackage}${User_FQN};
import static java.util.Collections.singletonMap;
import java.util.List;
import java.util.Optional;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

<#if cdi>@Dependent</#if><#if !cdi>@Stateless</#if>
<#if named>@Named("user")</#if>
public class ${UserRepository} extends ${AbstractRepository}<User, Long> {

    @Inject
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ${UserRepository}() {
        super(User.class);
    }

    public Optional<User> findOneByLogin(String login) {
        return findSingleByNamedQuery("findUserByLogin", singletonMap("login", login));
    }

    public Optional<User> findOneByEmail(String email) {
        return findSingleByNamedQuery("findUserByEmail", singletonMap("email", email));
    }

    public Optional<User> findOneByResetKey(String resetKey) {
        return findSingleByNamedQuery("findUserByResetKey", singletonMap("resetKey", resetKey));
    }

    public Optional<User> findOneByActivationKey(String activationKey) {
        return findSingleByNamedQuery("findUserByActivationKey", singletonMap("activationKey", activationKey));
    }

<#--public List<User> findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime dateTime){
       TypedQuery<User> query = em.createNamedQuery("findUserByActivationKey");
       query.setParameter("createdDate", false);
       return query.getResultList();
    }    
-->
    public Optional<User> findOneById(Long userId) {
        return findSingleByNamedQuery("findUserByUserId", singletonMap("id", userId));
    }

    public Optional<User> findOneWithAuthoritiesById(Long userId) {
        return findSingleByNamedQuery("findUserByUserId", "graph.user.authorities", singletonMap("id", userId));
    }

    public Optional<User> findOneWithAuthoritiesByLogin(String login) {
        return findSingleByNamedQuery("findUserByLogin", "graph.user.authorities", singletonMap("login", login));
    }
    
    public List<User> getUsersWithAuthorities(int startPosition, int size) {
        return findRange(startPosition, size, "graph.user.authorities");
    }

}
