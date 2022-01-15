package edu.conference.service.impl;

import edu.conference.model.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.conference.repository.DAOFactory;
import edu.conference.repository.SectionDAO;
import edu.conference.repository.exception.RepositoryException;
import edu.conference.service.SectionService;
import edu.conference.service.exception.ServiceException;

import java.util.List;

public class SectionServiceImpl implements SectionService {

    private static final Logger LOG = LoggerFactory.getLogger(SectionServiceImpl.class);

    private final SectionDAO sDao;

    public SectionServiceImpl() {
        sDao = DAOFactory.getInstance().getSectionDAO();
    }

    @Override
    public void create(Section section) throws ServiceException {
        try {
            section = sDao.create(section);
            LOG.info("Successfully created new section {}.", section.getName());
        } catch (RepositoryException e) {
            LOG.error("Failed to create new section.");
            throw new ServiceException("Failed to create new section " + section.getName() + ".");
        }
    }

    @Override
    public void update(Section section) throws ServiceException {
        try {
            sDao.update(section);
            LOG.info("Successfully updated section {}.", section.getName());
        } catch (RepositoryException e) {
            LOG.error("Failed to update existing section {}.", section.getName());
            throw new ServiceException("Failed to update existing section " + section.getName() + ".");
        }
    }

    @Override
    public void delete(Long id) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Section getById(Long id) throws ServiceException {
        try {
            return sDao.getById(id);
        } catch (RepositoryException e) {
            LOG.error("Failed to query section by id {}.", id);
            throw new ServiceException("Failed to query section by id " + id + ".");
        }
    }

    @Override
    public Section getByName(String name) throws ServiceException {
        try {
            return sDao.getByName(name);
        } catch (RepositoryException e) {
            LOG.error("Failed to query section with name {}.", name);
            throw new ServiceException("Failed to query section with name " + name + ".");
        }
    }

    @Override
    public List<Section> getAll() throws ServiceException {
        try {
            return sDao.getAll();
        } catch (RepositoryException e) {
            LOG.error("Failed to query all sections.");
            throw new ServiceException("Failed to query all sections.");
        }
    }

    @Override
    public List<Section> getAllForRepresentative(String email) throws ServiceException {
        try {
            return sDao.getAllForRepresentative(email);
        } catch (RepositoryException e) {
            LOG.error("Failed to query all sections for representative {}.", email);
            throw new ServiceException("Failed to query all sections for representative " + email + ".");
        }
    }

}
