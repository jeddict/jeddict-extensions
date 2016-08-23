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
package org.netbeans.jcode.ng.main.domain;

/**
 *
 * @author jGauravGupta
 */
public class EntityConfig {

    private String pagination; // no
    private String dto;//no
//    service


    /**
     * @return the pagination
     */
    public String getPagination() {
        if(pagination==null){
            pagination = AnswerType.NO.toString();
        }
        return pagination;
    }

    /**
     * @param pagination the pagination to set
     */
    public void setPagination(String pagination) {
        this.pagination = pagination;
    }

    /**
     * @return the dto
     */
    public String getDto() {
        if(dto==null){
            dto = AnswerType.NO.toString();
        }
        return dto;
    }

    /**
     * @param dto the dto to set
     */
    public void setDto(String dto) {
        this.dto = dto;
    }

}
