/**
 * Interfaz de mapeo para Serie.
 * MapStruct generará automáticamente la implementación en tiempo de compilación.
 */
package org.kodigo.swagger_api_azure.service.mapper;

import org.kodigo.swagger_api_azure.dto.SerieDTO;
import org.kodigo.swagger_api_azure.entity.Serie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring", uses = { PersonajeMapper.class })
public interface SerieMapper {
    SerieMapper INSTANCE = Mappers.getMapper(SerieMapper.class);

    /**
     * Convierte una entidad Serie a su DTO correspondiente.
     * Incluye la lista de personajes.
     * @param serie la entidad Serie.
     * @return el DTO de la Serie.
     */
    SerieDTO toDto(Serie serie);

    /**
     * Convierte una entidad Serie a su DTO correspondiente sin personajes.
     * @param serie la entidad Serie.
     * @return el DTO de la Serie sin personajes.
     */
    @Mapping(target = "personajes", ignore = true)
    SerieDTO toDtoWithoutPersonajes(Serie serie);

    /**
     * Convierte un DTO de Serie a su entidad correspondiente.
     * @param serieDto el DTO de la Serie.
     * @return la entidad Serie.
     */
    Serie toEntity(SerieDTO serieDto);
}
