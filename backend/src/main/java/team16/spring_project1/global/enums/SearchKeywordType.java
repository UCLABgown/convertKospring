package team16.spring_project1.global.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SearchKeywordType {
    productName("productName"),
    category("category");

    private final String value;
}