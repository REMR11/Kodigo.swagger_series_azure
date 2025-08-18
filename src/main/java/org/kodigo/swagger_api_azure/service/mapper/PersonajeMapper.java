/**
 * Interfaz de mapeo para Personaje.
 * MapStruct generará automáticamente la implementación en tiempo de compilación.
 */
package org.kodigo.swagger_api_azure.service.mapper;

import org.kodigo.swagger_api_azure.dto.CreatePersonajeDTO;
import org.kodigo.swagger_api_azure.dto.PersonajeDTO;
import org.kodigo.swagger_api_azure.entity.Personaje;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonajeMapper {
    /**
     * Convierte una entidad Personaje a su DTO.
     * Mapea el ID y el título de la serie del objeto anidado.
     * @param personaje la entidad Personaje.
     * @return el DTO del Personaje.
     */
    @Mapping(target = "serieId", source = "serie.id")
    @Mapping(target = "serieTitulo", source = "serie.titulo")
    PersonajeDTO toDto(Personaje personaje);

    /**
     * Convierte un DTO de Personaje a su entidad.
     * El campo de serie en la entidad se ignora ya que se establece en el servicio.
     * @param personajeDto el DTO del Personaje.
     * @return la entidad Personaje.
     */
    @Mapping(target = "serie", ignore = true) // la relación se gestiona en el servicio
    Personaje toEntity(PersonajeDTO personajeDto);

    /**
     * Convierte un DTO de creación de Personaje a su entidad.
     * Ignora el campo de serie, ya que se establece en el servicio.
     * @param createPersonajeDto el DTO de creación.
     * @return la entidad Personaje.
     */
    @Mapping(target = "serie", ignore = true)
    Personaje toEntity(CreatePersonajeDTO createPersonajeDto);
}
