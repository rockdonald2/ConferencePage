package service;

import model.User;

import java.util.List;

public interface UserService {

    void register(User user);
    boolean login(User user);

    void update(User user);
    void delete(Long id);
    User getById(Long id);
    User getByEmail(String email);
    List<User> getAll();
    List<User> getByRole(String role);

}
