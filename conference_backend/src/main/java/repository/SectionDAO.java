package repository;

import model.Section;

public interface SectionDAO {

    Section create(Section section);
    void update(Section section);
    void delete(Long id);
    Section getById();
    Section getByName();
    Section getAll();

}
