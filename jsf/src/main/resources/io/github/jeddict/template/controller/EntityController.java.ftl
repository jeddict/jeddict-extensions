package ${package};

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import ${EntityClass_FQN};
import ${RepositoryClass_FQN};
import ${JsfUtil_FQN};
import ${PaginationHelper_FQN};
<#list attributes as attribute>
<#if (attribute.getClass().getSimpleName()) == "Embedded">
import ${embeddableFQN}.${attribute.name?cap_first};
</#if>
<#if (attribute.getClass().getSimpleName()) == "Basic">
<#if attribute.enumerated??>
<#if (attribute.enumerated == "STRING") || (attribute.enumerated == "ORDINAL") || (attribute.enumerated == "DEFAULT")>
import ${attribute.attributeType};
</#if>
</#if>
</#if>   
</#list>   

@Named("${entityInstance}Controller")
@RequestScoped
public class ${EntityController} implements Serializable{

    private ${Entity} ${entityInstance};
    private DataModel items = null;
    @Inject
    private ${EntityRepository} ${RepositoryInstance};
    private int selectedItemIndex;
    private PaginationHelper pagination;

    <#list attributes as attribute>
    <#if (attribute.getClass().getSimpleName()) == "Embedded">
    private ${attribute.name?cap_first} ${attribute.name};
    </#if>           
    <#if (attribute.getClass().getSimpleName()) == "Basic">
    <#if attribute.enumerated??>
    <#if (attribute.enumerated == "STRING") || (attribute.enumerated == "ORDINAL") || (attribute.enumerated == "DEFAULT")>
    public ${attribute.attributeType}[] get${attribute.name}() {
       return ${attribute.attributeType}.values();
    }
    </#if>
    </#if>
    </#if>
    </#list>

    public ${Entity} getSelected() {
        if (${entityInstance} == null) {
            ${entityInstance} = new ${Entity}();
        <#list attributes as attribute>
        <#if (attribute.getClass().getSimpleName()) == "Embedded">
            ${attribute.name} = new ${attribute.name?cap_first}();
            ${entityInstance}.set${attribute.name?cap_first}(${attribute.name});
        </#if>
        </#list>   
            selectedItemIndex = -1;
        }
        return ${entityInstance};
    }
    public void setSelected(${Entity} selected) {
        this.${entityInstance} = selected;
    }

    private ${EntityRepository} getRepository() {
        return ${RepositoryInstance};
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getRepository().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getRepository().findRange(getPageFirstItem(), getPageFirstItem() + getPageSize()));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "list${Entity}";
    }

    public String prepareView() {
        ${entityInstance} = (${Entity}) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "view${Entity}";
    }

    public String prepareCreate() {
        ${entityInstance} = new ${Entity}();
        selectedItemIndex = -1;
        return "create${Entity}";
    }

    public String create() {
        try {
            getRepository().create(${entityInstance});
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("${Entity}Created"));
            return prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareUpdate() {
        ${entityInstance} = (${Entity}) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "update${Entity}";
    }

    public String update() {
        try {
            getRepository().edit(${entityInstance});
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("${Entity}Updated"));
            return "list${Entity}";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        ${entityInstance} = (${Entity}) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return prepareList();
    }

   public String destroyAndView() {
        performDestroy();
        recreateModel();
        if (selectedItemIndex >= 0) {
            return "list${Entity}";
        } else {
            recreateModel();
            return "list${Entity}";
        }
    }

    private void performDestroy() {
        try {
            getRepository().remove(${entityInstance});
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("${Entity}Deleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getRepository().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            ${entityInstance} = getRepository().findRange(selectedItemIndex, selectedItemIndex + 1).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }
    
    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "list${Entity}";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "list${Entity}";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(${RepositoryInstance}.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(${RepositoryInstance}.findAll(), true);
    }

    public ${Entity} get${Entity}(long id) {
        return ${RepositoryInstance}.find(id);
    }

   @FacesConverter(forClass = ${Entity}.class)
    public static class ${EntityController}Converter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ${EntityController} controller = (${EntityController}) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "${EntityController}");
            return controller.get${Entity}(getKey(value));
        }

        long getKey(String value) {
            long key;
            key = Long.parseLong(value);
            return key;
        }

        String getStringKey(long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof ${Entity}) {
                ${Entity} o = (${Entity}) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ${Entity}.class.getName());
            }
        }

    }

}
