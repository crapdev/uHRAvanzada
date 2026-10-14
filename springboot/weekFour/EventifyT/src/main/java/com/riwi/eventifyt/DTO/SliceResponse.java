package com.riwi.eventifyt.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Slice;

import java.util.List;

// Fragmento de resultados SIN total de registros (no hay SELECT COUNT): solo sabemos si hay pagina anterior/siguiente
@Schema(description = "Fragmento (Slice) de resultados. No incluye el total de registros para evitar el COUNT(*)")
public record SliceResponse<T>(List<T> content,
                               int page,
                               int size,
                               boolean hasPrevious,
                               boolean hasNext) {

    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return new SliceResponse<>(slice.getContent(),
                slice.getNumber(),
                slice.getSize(),
                slice.hasPrevious(),
                slice.hasNext());
    }
}
