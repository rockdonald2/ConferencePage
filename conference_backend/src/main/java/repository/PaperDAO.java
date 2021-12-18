package repository;

import model.Paper;

public interface PaperDAO {

    Paper create(Paper paper);
    void update(Paper paper);
    void delete(Long id);
    Paper getById(Long id);
    Paper getByPath(String path);
    Paper getAll();
    Paper getAllForPresenter();
    Paper getAllForRepresentative();

}
