/**
 * Copyright [2016] Gaurav Gupta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.netbeans.jcode.jpa;

/**
 *
 * @author Gaurav Gupta
 */
public class JPAConstants {

    public static final String PERSISTENCE_PACKAGE = "javax.persistence.";
    public static final String QUERY_TYPE = "Query";
    public static final String ENTITY_MANAGER_TYPE = "EntityManager";
    public static final String ENTITY_MANAGER_FACTORY = "EntityManagerFactory";
    public static final String ENTITY_TRANSACTION = "EntityTransaction";
    public static final String PERSISTENCE = "Persistence";
    public static final String PERSISTENCE_CONTEXT_ANNOTATION = "PersistenceContext";
    public static final String PERSISTENCE_CONTEXT = PERSISTENCE_PACKAGE + PERSISTENCE_CONTEXT_ANNOTATION;
    public static final String NO_RESULT_EXCEPTION = "NoResultException";

    public static final String ENTITY = "Entity";
    public static final String ENTITY_FQN = PERSISTENCE_PACKAGE + ENTITY;

    public static final String TABLE = "Table";
    public static final String TABLE_FQN = PERSISTENCE_PACKAGE + TABLE;

    public static final String ID = "Id";
    public static final String ID_FQN = PERSISTENCE_PACKAGE + ID;

    public static final String BASIC = "Basic";
    public static final String BASIC_FQN = PERSISTENCE_PACKAGE + BASIC;

    public static final String EMBEDDED_ID = "EmbeddedId";
    public static final String EMBEDDED_ID_FQN = PERSISTENCE_PACKAGE + EMBEDDED_ID;

    public static final String GENERATED_VALUE = "GeneratedValue";
    public static final String GENERATED_VALUE_FQN = PERSISTENCE_PACKAGE + GENERATED_VALUE;

    public static final String ORDER_BY = "OrderBy";
    public static final String ORDER_COLUMN = "OrderColumn";
    public static final String MAP_KEY = "MapKey";
    public static final String MAP_KEY_CLASS = "MapKeyClass";

    public static final String MAP_KEY_TEMPORAL = "MapKeyTemporal";
    public static final String MAP_KEY_TEMPORAL_FQN = PERSISTENCE_PACKAGE + MAP_KEY_TEMPORAL;
    public static final String MAP_KEY_ENUMERATED = "MapKeyEnumerated";
    public static final String MAP_KEY_ENUMERATED_FQN = PERSISTENCE_PACKAGE + MAP_KEY_ENUMERATED;

    public static final String MAP_KEY_ATTRIBUTE_OVERRIDE = "MapKeyAttributeOverride";
    public static final String MAP_KEY_COLUMN = "MapKeyColumn";
    public static final String MAP_KEY_JOIN_COLUMN = "MapKeyJoinColumn";
    public static final String COLUMN = "Column";
    public static final String ENUMERATED = "Enumerated";
    public static final String ENUMERATED_FQN = PERSISTENCE_PACKAGE + ENUMERATED;
    public static final String ENUM_TYPE = "EnumType";
    public static final String ENUM_TYPE_FQN = PERSISTENCE_PACKAGE + ENUM_TYPE;
    public static final String ENUM_TYPE_ORDINAL = "EnumType.ORDINAL";
    public static final String ENUM_TYPE_STRING = "EnumType.STRING";
    
    public static final String FETCH_TYPE = "javax.persistence.FetchType";
    public static final String CASCADE_TYPE = "javax.persistence.CascadeType";

    public static final String TEMPORAL = "Temporal";
    public static final String TEMPORAL_FQN = PERSISTENCE_PACKAGE + TEMPORAL;
    public static final String TEMPORAL_TYPE = "TemporalType";
    public static final String TEMPORAL_TYPE_FQN = PERSISTENCE_PACKAGE + TEMPORAL_TYPE;
    public static final String TEMPORAL_DATE = "TemporalType.DATE";
    public static final String TEMPORAL_TIME = "TemporalType.TIME";
    public static final String TEMPORAL_TIMESTAMP = "TemporalType.TIMESTAMP";

    public static final String LOB = "Lob";
    public static final String ATTRIBUTE_OVERRIDE = "AttributeOverride";
    public static final String ASSOCIATION_OVERRIDE = "AssociationOverride";
    public static final String COLLECTION_TABLE = "CollectionTable";

}
