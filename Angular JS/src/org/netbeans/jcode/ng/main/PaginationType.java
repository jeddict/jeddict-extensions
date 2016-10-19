/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jcode.ng.main;

/**
 *
 * @author jGauravGupta
 */
public enum PaginationType {
    NO("no", "No"), PAGER("pager", "Pager"), PAGINATION("pagination", "Pagination"), INFINITE_SCROLL("infinite-scroll", "Infinite Scroll");

    private final String keyword;
    private final String title;

    private PaginationType(String keyword, String title) {
        this.keyword = keyword;
        this.title = title;
    }

    /**
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

}
