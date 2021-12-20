package repository;

import model.Section;

import java.util.List;

public interface SectionDAO {

    Section create(Section section);
    void update(Section section);
    void delete(Long id);
    Section getById(Long id);
    Section getByName(String name);
    List<Section> getAll();
    List<Section> getAllForRepresentative(String email);

}
