package edu.conference.repository;

import edu.conference.model.Role;
import edu.conference.model.User;
import edu.conference.repository.exception.RepositoryException;

import java.util.List;

public interface UserDAO {

    User create(User user) throws RepositoryException;
    void update(User user) throws RepositoryException;
    void delete(Long id) throws RepositoryException;
    User getById(Long id) throws RepositoryException;
    User getByEmail(String email) throws RepositoryException;
    List<User> getAll() throws RepositoryException;
    List<User> getByRole(Role role) throws RepositoryException;

}
