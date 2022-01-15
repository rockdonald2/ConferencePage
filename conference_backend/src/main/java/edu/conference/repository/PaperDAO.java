package edu.conference.repository;

import edu.conference.model.Paper;
import edu.conference.repository.exception.RepositoryException;

import java.util.List;

public interface PaperDAO {

    Paper create(Paper paper) throws RepositoryException;
    void update(Paper paper) throws RepositoryException;
    void delete(Long id) throws RepositoryException;
    Paper getById(Long id) throws RepositoryException;
    Paper getByPath(String path) throws RepositoryException;
    List<Paper> getAll() throws RepositoryException;
    List<Paper> getAllForPresenter(String email) throws RepositoryException;
    List<Paper> getAllForSection(Long id) throws RepositoryException;

}
