package edu.conference.service;

import edu.conference.model.Section;
import edu.conference.service.exception.ServiceException;

import java.util.List;

public interface SectionService {

    void create(Section section) throws ServiceException;
    void update(Section section) throws ServiceException;
    void delete(Long id) throws ServiceException;
    Section getById(Long id) throws ServiceException;
    Section getByName(String name) throws ServiceException;
    List<Section> getAll() throws ServiceException;
    List<Section> getAllForRepresentative(String email) throws ServiceException;

}
