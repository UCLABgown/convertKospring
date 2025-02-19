package team16.spring_project1.standard.page.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;

import java.util.List;

@Getter
public class PageDto<T> {
    @NonNull
    private final long totalItems;
    @NonNull
    private final List<T> items;
    @NonNull
    private final long totalPages;
    @NonNull
    private final int currentPageNumber;
    @NonNull
    private final int pageSize;

    public PageDto(Page<T> page) {
        this.totalItems = page.getTotalElements();
        this.items = page.getContent();
        this.totalPages = page.getTotalPages();
        this.currentPageNumber = page.getNumber() + 1;
        this.pageSize = page.getSize();
    }
}
