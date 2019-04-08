package ${package};

import org.jnosql.artemis.Repository;
import java.util.List;
import ${EntityClass_FQN};
<#if EntityPKClass_FQN!="">import ${EntityPKClass_FQN};</#if>

public interface ${EntityRepository} extends Repository<${EntityClass}, ${EntityPKClass}> {

    List<${EntityClass}> findAll();
    
}
