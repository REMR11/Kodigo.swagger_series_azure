/**
 * Servicio para gestionar Series
 * Aplica principios SOLID:
 * - SRP: Solo responsable de la lógica de negocio de Series
 * - OCP: Extensible mediante herencia o composición
 * - DIP: Depende de abstracciones (interfaces) no de implementaciones concretas
 */
package org.kodigo.swagger_api_azure.service;

import jakarta.transaction.Transactional;
import org.kodigo.swagger_api_azure.dto.PersonajeDTO;
import org.kodigo.swagger_api_azure.dto.SerieDTO;
import org.kodigo.swagger_api_azure.entity.Personaje;
import org.kodigo.swagger_api_azure.entity.Serie;
import org.kodigo.swagger_api_azure.repository.PersonajeRepository;
import org.kodigo.swagger_api_azure.repository.SerieRepository;
import org.kodigo.swagger_api_azure.service.mapper.SerieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SerieService {
    private final SerieRepository serieRepository;
    private final SerieMapper serieMapper;
    private final PersonajeRepository personajeRepository;

    @Autowired
    public SerieService(SerieRepository serieRepository, SerieMapper serieMapper, PersonajeRepository personajeRepository) {
        this.serieRepository = serieRepository;
        this.serieMapper = serieMapper;
        this.personajeRepository = personajeRepository;
    }

    /**
     * Obtiene todas las series
     * @return Lista de SerieDto
     */
    @Transactional
    public List<SerieDTO> findAll() {
        return serieRepository.findAllWithPersonajes()
                .stream()
                .map(serieMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una serie por ID
     * @param id ID de la serie
     * @return SerieDto si existe
     */
    @Transactional
    public Optional<SerieDTO> findById(Long id) {
        return serieRepository.findByIdWithPersonajes(id)
                .map(serieMapper::toDto);
    }

    /**
     * Crea una nueva serie
     * @param serieDto Datos de la serie a crear
     * @return SerieDto creada
     * @throws IllegalArgumentException si ya existe una serie con el mismo título
     */
    public SerieDTO create(SerieDTO serieDto) {
        if (serieDto.getId() != null && serieDto.getId() > 0) {
            throw new IllegalArgumentException("El ID debe ser null para crear una nueva serie");
        }

        Serie serie = serieMapper.toEntity(serieDto);
        serie.setId(null);

        managePersonajes(serie, serieDto);

        Serie savedSerie = serieRepository.save(serie);
        return serieMapper.toDto(savedSerie);
    }

    /**
     * Actualiza una serie existente
     * @param id ID de la serie a actualizar
     * @param serieDto Nuevos datos de la serie
     * @return SerieDto actualizada
     * @throws RuntimeException si la serie no existe
     */
    public SerieDTO update(Long id, SerieDTO serieDto) {
        Serie existingSerie = serieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada con ID: " + id));

        if (!existingSerie.getTitulo().equalsIgnoreCase(serieDto.getTitulo())) {
            validateSerieTitle(serieDto.getTitulo());
        }

        existingSerie.setTitulo(serieDto.getTitulo());
        existingSerie.setGenero(serieDto.getGenero());
        existingSerie.setFechaEstreno(serieDto.getFechaEstreno());

        managePersonajes(existingSerie, serieDto);

        Serie updatedSerie = serieRepository.save(existingSerie);
        return serieMapper.toDto(updatedSerie);
    }

    /**
     * Elimina una serie por ID
     * @param id ID de la serie a eliminar
     * @throws RuntimeException si la serie no existe
     */
    public void deleteById(Long id) {
        if (!serieRepository.existsById(id)) {
            throw new RuntimeException("Serie no encontrada con ID: " + id);
        }
        serieRepository.deleteById(id);
    }

    /**
     * Busca series por género
     * @param genero Género a buscar
     * @return Lista de SerieDto del género especificado
     */
    @Transactional
    public List<SerieDTO> findByGenero(String genero) {
        return serieRepository.findByGeneroIgnoreCase(genero)
                .stream()
                .map(serieMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca series por título (parcial)
     * @param titulo Título o parte del título
     * @return Lista de SerieDto que coinciden
     */
    @Transactional
    public List<SerieDTO> findByTitulo(String titulo) {
        return serieRepository.findByTituloContainingIgnoreCase(titulo)
                .stream()
                .map(serieMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Sincroniza la lista de personajes de una serie a partir del DTO
     */
    private void managePersonajes(Serie serie, SerieDTO serieDto) {
        List<Personaje> personajesGestionados = new ArrayList<>();

        if (serieDto.getPersonajes() != null && !serieDto.getPersonajes().isEmpty()) {
            for (PersonajeDTO personajeDto : serieDto.getPersonajes()) {
                if (personajeDto.getId() != null && personajeDto.getId() > 0) {
                    Personaje personajeExistente = personajeRepository.findById(personajeDto.getId())
                            .orElseThrow(() -> new RuntimeException("Personaje con ID " + personajeDto.getId() + " no encontrado"));

                    personajeExistente.setNombre(personajeDto.getNombre());
                    personajeExistente.setDescripcion(personajeDto.getDescripcion());
                    personajeExistente.setSerie(serie);

                    personajesGestionados.add(personajeExistente);
                } else {
                    Personaje nuevo = new Personaje();
                    nuevo.setNombre(personajeDto.getNombre());
                    nuevo.setDescripcion(personajeDto.getDescripcion());
                    nuevo.setSerie(serie);

                    personajesGestionados.add(nuevo);
                }
            }
        }

        serie.getPersonajes().clear();
        serie.getPersonajes().addAll(personajesGestionados);
    }

    /**
     * Valida que no exista una serie con el mismo título
     * @param titulo Título a validar
     * @throws IllegalArgumentException si ya existe
     */
    private void validateSerieTitle(String titulo) {
        if (serieRepository.existsByTituloIgnoreCase(titulo)) {
            throw new IllegalArgumentException("Ya existe una serie con el título: " + titulo);
        }
    }

}
