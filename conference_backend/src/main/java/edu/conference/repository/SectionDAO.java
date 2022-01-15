package edu.conference.repository;

import edu.conference.model.Section;
import edu.conference.repository.exception.RepositoryException;

import java.util.List;

public interface SectionDAO {

    Section create(Section section) throws RepositoryException;
    void update(Section section) throws RepositoryException;
    void delete(Long id) throws RepositoryException;
    Section getById(Long id) throws RepositoryException;
    Section getByName(String name) throws RepositoryException;
    List<Section> getAll() throws RepositoryException;
    List<Section> getAllForRepresentative(String email) throws RepositoryException;

}
