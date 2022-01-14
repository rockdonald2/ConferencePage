package service;

import model.User;
import service.exception.ServiceException;

import java.util.List;

public interface UserService {

    void register(User user) throws ServiceException;
    boolean login(User user) throws ServiceException;

    void update(User user) throws ServiceException;
    void delete(Long id) throws ServiceException;
    User getById(Long id) throws ServiceException;
    User getByEmail(String email) throws ServiceException;
    List<User> getAll() throws ServiceException;
    List<User> getByRole(String role) throws ServiceException;

}
