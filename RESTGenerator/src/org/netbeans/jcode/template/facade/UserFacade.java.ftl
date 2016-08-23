<#if package??>package ${package};</#if>

import ${User_FQN};
import java.util.Collections;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
@Named("user")
public class ${UserFacade} extends AbstractFacade<User> {

    @PersistenceContext(unitName = "${PU}")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ${UserFacade}() {
        super(User.class);
    }

    public Optional<User> findOneByLogin(String login) {
        return findSingleByNamedQuery("findUserByLogin", Collections.singletonMap("login", login), User.class);
    }

    public Optional<User> findOneByEmail(String email) {
        return findSingleByNamedQuery("findUserByEmail", Collections.singletonMap("email", email), User.class);
    }

    public Optional<User> findOneByResetKey(String resetKey) {
        return findSingleByNamedQuery("findUserByResetKey", Collections.singletonMap("resetKey", resetKey), User.class);
    }

    public Optional<User> findOneByActivationKey(String activationKey) {
        return findSingleByNamedQuery("findUserByActivationKey", Collections.singletonMap("activationKey", activationKey), User.class);
    }

<#--public List<User> findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime dateTime){
       TypedQuery<User> query = em.createNamedQuery("findUserByActivationKey", User.class);
       query.setParameter("createdDate", false);
       return query.getResultList();
    }    -->
    public Optional<User> findOneById(Long userId) {
        return findSingleByNamedQuery("findUserByUserId", Collections.singletonMap("id", userId), User.class);
    }

}
