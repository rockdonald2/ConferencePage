package repository;

import model.User;

import java.util.List;

public interface UserDAO {

    User create(User user);
    void update(User user);
    void delete(Long id);
    User getById(Long id);
    User getByEmail(String email);
    List<User> getAll();

}
