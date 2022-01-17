package edu.conference.service;

import edu.conference.service.exception.ServiceException;
import edu.conference.model.User;

import java.util.List;

public interface UserService {

    boolean register(User user) throws ServiceException;
    boolean login(User user) throws ServiceException;
    void changePwd(User user) throws ServiceException;
    boolean arePapersUploaded(User user) throws ServiceException;
    boolean arePapersJudged(User user) throws ServiceException;

    void update(User user) throws ServiceException;
    void delete(Long id) throws ServiceException;
    User getById(Long id) throws ServiceException;
    User getByEmail(String email) throws ServiceException;
    List<User> getAll() throws ServiceException;
    List<User> getByRole(String role) throws ServiceException;

}
