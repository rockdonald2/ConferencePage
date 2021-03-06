package edu.conference.service.impl;

import edu.conference.model.Paper;
import edu.conference.repository.PaperDAO;
import edu.conference.repository.exception.RepositoryException;
import edu.conference.service.PaperService;
import edu.conference.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.conference.repository.DAOFactory;

import java.util.List;

public class PaperServiceImpl implements PaperService {

    private static final Logger LOG = LoggerFactory.getLogger(PaperServiceImpl.class);

    private final PaperDAO pDao;

    public PaperServiceImpl() {
        DAOFactory factory = DAOFactory.getInstance();
        pDao = factory.getPaperDAO();
    }

    @Override
    public Paper register(Paper paper) {
        try {
            paper = pDao.create(paper);
            LOG.info("Successfully registered paper {} for presenter {}.", paper.getId(), paper.getPresenter().getEmail());
            return paper;
        } catch (RepositoryException e) {
            LOG.error("Failed to register new paper for presenter {}.", paper.getPresenter().getEmail());
            throw new ServiceException("Failed to register new paper for presenter " + paper.getPresenter().getEmail() + ".");
        }
    }

    @Override
    public void delete(Long id) throws ServiceException {
        try {
            pDao.delete(id);
            LOG.info("Successfully delete paper {}.", id);
        } catch (RepositoryException e) {
            LOG.error("Failed to delete paper {}.", id);
            throw new ServiceException("Failed to delete paper " + id + ".");
        }
    }

    @Override
    public void update(Paper paper) {
        try {
            pDao.update(paper);
            LOG.info("Successfully updated paper {}.", paper.getId());
        } catch (RepositoryException e) {
            LOG.error("Failed to update paper {}.", paper.getId());
            throw new ServiceException("Failed to update paper " + paper.getId() + ".");
        }
    }

    @Override
    public void revoke(Long id) {
        try {
            pDao.delete(id);
            LOG.info("Successfully revoked paper {}.", id);
        } catch (RepositoryException e) {
            LOG.error("Failed to revoke paper {}.", id);
            throw new ServiceException("Failed to revoke paper " + id + ".");
        }
    }

    @Override
    public Paper getById(Long id) {
        try {
            return pDao.getById(id);
        } catch (RepositoryException e) {
            LOG.error("Failed to query paper {}.", id);
            throw new ServiceException("Failed to query paper " + id + ".");
        }
    }

    @Override
    public Paper getByPath(String path) {
        try {
            return pDao.getByPath(path);
        } catch (RepositoryException e) {
            LOG.error("Failed to query paper by path {}.", path);
            throw new ServiceException("Failed to query paper by path " + path + ".");
        }
    }

    @Override
    public List<Paper> getAll() {
        try {
            return pDao.getAll();
        } catch (RepositoryException e) {
            LOG.error("Failed to query all papers.");
            throw new ServiceException("Failed to query all papers.");
        }
    }

    @Override
    public List<Paper> getAllForPresenter(String email) {
        try {
            return pDao.getAllForPresenter(email);
        } catch (RepositoryException e) {
            LOG.error("Failed to query papers for presenter {}.", email);
            throw new ServiceException("Failed to query papers for presenter " + email + ".");
        }
    }

    @Override
    public List<Paper> getAllForSection(Long id) {
        try {
            return pDao.getAllForSection(id);
        } catch (RepositoryException e) {
            LOG.error("Failed to query papers for section {}.", id);
            throw new ServiceException("Failed to query papers for section " + id + ".");
        }
    }

    @Override
    public void upload(String path) {
        throw new UnsupportedOperationException();
    }

}
