package repository;

import model.Paper;

import java.util.List;

public interface PaperDAO {

    Paper create(Paper paper);
    void update(Paper paper);
    void delete(Long id);
    Paper getById(Long id);
    Paper getByPath(String path);
    List<Paper> getAll();
    List<Paper> getAllForPresenter(String email);
    List<Paper> getAllForSection(Long id);

}
