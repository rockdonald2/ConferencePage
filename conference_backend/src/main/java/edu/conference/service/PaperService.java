package edu.conference.service;

import edu.conference.model.Paper;
import edu.conference.service.exception.ServiceException;

import java.util.List;

public interface PaperService {

    Paper register(Paper paper) throws ServiceException;
    void update(Paper paper) throws ServiceException;
    void revoke(Long id) throws ServiceException;
    Paper getById(Long id) throws ServiceException;
    Paper getByPath(String path) throws ServiceException;
    List<Paper> getAll() throws ServiceException;
    List<Paper> getAllForPresenter(String email) throws ServiceException;
    List<Paper> getAllForSection(Long id) throws ServiceException;

    void upload(String path) throws ServiceException;

}
