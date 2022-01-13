package repository;

import model.Section;
import repository.exception.RepositoryException;

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
