package org.kodigo.swagger_api_azure.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Estadísticas de series")
public class SerieStatsResponse {
    @Schema(description = "Número total de series", example = "150")
    private final long totalSeries;

    @Schema(description = "Número de géneros únicos", example = "12")
    private final long uniqueGenres;

    public SerieStatsResponse(long totalSeries, long uniqueGenres) {
        this.totalSeries = totalSeries;
        this.uniqueGenres = uniqueGenres;
    }
}
