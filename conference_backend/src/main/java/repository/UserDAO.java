package repository;

import model.Role;
import model.User;
import repository.exception.RepositoryException;

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
