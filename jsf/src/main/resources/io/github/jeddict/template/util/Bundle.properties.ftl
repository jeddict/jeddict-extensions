PersistenceErrorOccured=A persistence error occurred.
Create=Create
View=View
Edit=Edit
Delete=Delete
Close=Close
Cancel=Cancel
Save=Save
SelectOneMessage=Select One...
Home=Home
Maintenance=Maintenance

<#list Entities as Entity>
    AppName=${Entity} Registration Demo
    ${Entity}Created=${Entity} was successfully created.
    ${Entity}Updated=${Entity} was successfully updated.
    ${Entity}Deleted=${Entity} was successfully deleted.

    Create${Entity}Title=Create New ${Entity}
    Create${Entity}SaveLink=Save
    Create${Entity}ShowAllLink=Show All ${Entity} Items
    Create${Entity}IndexLink=Index

    Edit${Entity}Title=Edit ${Entity}
    Edit${Entity}SaveLink=Save
    Edit${Entity}ViewLink=View
    Edit${Entity}ShowAllLink=Show All ${Entity} Items
    Edit${Entity}IndexLink=Index

    View${Entity}Title=View ${Entity}
    View${Entity}DestroyLink=Destroy
    View${Entity}EditLink=Edit
    View${Entity}CreateLink=Create New ${Entity}
    View${Entity}ShowAllLink=Show All ${Entity} Items
    View${Entity}IndexLink=Index

    List${Entity}Title=List
    List${Entity}Empty=(No ${Entity} Items Found)
    List${Entity}DestroyLink=Destroy
    List${Entity}EditLink=Edit
    List${Entity}ViewLink=View
    List${Entity}CreateLink=Create New ${Entity}
    List${Entity}IndexLink=Index

</#list> 
