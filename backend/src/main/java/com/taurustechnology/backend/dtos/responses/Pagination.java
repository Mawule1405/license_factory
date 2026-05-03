package com.taurustechnology.backend.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagination<T> {

    private int totalPages;
    private long totalElements;
    private int page;
    private int size;
    private List<T> content;

    // Méthode utilitaire pour convertir une Page Spring en ton DTO
    public static <T> Pagination<T> of(org.springframework.data.domain.Page<T> springPage) {
        return new Pagination<>(
                springPage.getTotalPages(),
                springPage.getTotalElements(),
                springPage.getNumber()+1, // Spring commence à 0
                springPage.getSize(),
                springPage.getContent()
        );
    }
}
