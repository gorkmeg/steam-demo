package com.steampowered.steam_demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PagedResponse<T> {
    private final List<T> data;
    private final PageMetaResponse page;

    public static <T> PagedResponse<T> from(Page<T> source) {
        return new PagedResponse<>(
                source.getContent(),
                new PageMetaResponse(
                        source.getNumber(),
                        source.getSize(),
                        source.getTotalElements(),
                        source.getTotalPages(),
                        source.hasNext(),
                        source.hasPrevious()
                )
        );
    }
}
