/**
 * Servicio para gestionar Series
 * Aplica principios SOLID:
 * - SRP: Solo responsable de la lógica de negocio de Series
 * - OCP: Extensible mediante herencia o composición
 * - DIP: Depende de abstracciones (interfaces) no de implementaciones concretas
 */
package org.kodigo.swagger_api_azure.service;

import jakarta.transaction.Transactional;
import org.kodigo.swagger_api_azure.dto.SerieDTO;
import org.kodigo.swagger_api_azure.entity.Serie;
import org.kodigo.swagger_api_azure.repository.SerieRepository;
import org.kodigo.swagger_api_azure.service.mapper.SerieMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SerieService {
    private final SerieRepository serieRepository;
    private final SerieMapper serieMapper;

    @Autowired
    public SerieService(SerieRepository serieRepository, SerieMapper serieMapper) {
        this.serieRepository = serieRepository;
        this.serieMapper = serieMapper;
    }

    /**
     * Obtiene todas las series
     * @return Lista de SerieDto
     */
    @Transactional
    public List<SerieDTO> findAll() {
        return serieRepository.findAll()
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
        return serieRepository.findById(id)
                .map(serieMapper::toDto);
    }

    /**
     * Crea una nueva serie
     * @param serieDto Datos de la serie a crear
     * @return SerieDto creada
     * @throws IllegalArgumentException si ya existe una serie con el mismo título
     */
    public SerieDTO create(SerieDTO serieDto) {
        validateSerieTitle(serieDto.getTitulo());

        Serie serie = serieMapper.toEntity(serieDto);
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

        // Validar título solo si ha cambiado
        if (!existingSerie.getTitulo().equalsIgnoreCase(serieDto.getTitulo())) {
            validateSerieTitle(serieDto.getTitulo());
        }

        existingSerie.setTitulo(serieDto.getTitulo());
        existingSerie.setGenero(serieDto.getGenero());
        existingSerie.setFechaEstreno(serieDto.getFechaEstreno());

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
