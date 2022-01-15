package edu.conference.service.impl;

import edu.conference.model.*;
import edu.conference.repository.exception.RepositoryException;
import edu.conference.service.PaperService;
import edu.conference.service.SectionService;
import edu.conference.service.ServiceFactory;
import edu.conference.service.UserService;
import edu.conference.service.exception.ServiceException;
import edu.conference.util.PasswordEncrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.conference.repository.DAOFactory;
import edu.conference.repository.UserDAO;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDAO uDao;
    private final PaperService pService;
    private final SectionService sService;

    public UserServiceImpl() {
        uDao = DAOFactory.getInstance().getUserDAO();
        pService = ServiceFactory.getInstance().getPaperService();
        sService = ServiceFactory.getInstance().getSectionService();
    }

    @Override
    public void register(User user) {
        try {
            if (!Objects.isNull(uDao.getByEmail(user.getEmail()))) {
                LOG.info("User already exists by email {}.", user.getEmail());
                return;
            }

            user.setPwd(PasswordEncrypter.generateHashedPassword(user.getPwd(), user.getUuid()));

            uDao.create(user);

            LOG.info("Successfully created user with email {}.", user.getEmail());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NullPointerException e) {
            LOG.error("Failed to encrypt user password.");
            throw new ServiceException("Failed to encrypt user password.");
        }
    }

    @Override
    public boolean login(User user) {
        try {
            User retrievedUser = uDao.getByEmail(user.getEmail());

            if (Objects.isNull(retrievedUser)) {
                LOG.info("Non-existing user by email {}.", user.getEmail());
                return false;
            }

            user.setPwd(PasswordEncrypter.generateHashedPassword(user.getPwd(), retrievedUser.getUuid()));

            boolean successful = user.getPwd().equals(retrievedUser.getPwd());

            if (successful) {
                LOG.info("Successfully loged in user with email {}.", user.getEmail());
            } else {
                LOG.info("Failed to log in user with email {}.", user.getEmail());
            }

            return successful;
        } catch (RepositoryException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOG.error("Failed to log in user with email {}.", user.getEmail());
            throw new ServiceException("Failed to log in user with email " + user.getEmail() + ".");
        }
    }

    @Override
    public boolean arePapersUploaded(User user) throws ServiceException {
        try {
            List<Paper> papers = pService.getAllForPresenter(user.getEmail());
            return papers.stream().noneMatch(paper -> paper.getDoc() == null);
        } catch (ServiceException | RepositoryException e) {
            LOG.error("Failed to verify if all papers are uploaded for {}.", user.getEmail());
            throw new ServiceException("Failed to verify if all papers are uploaded for " + user.getEmail() + ".");
        }
    }

    @Override
    public boolean arePapersJudged(User user) throws ServiceException {
        try {
            List<Section> sections = sService.getAllForRepresentative(user.getEmail());
            return sections.stream().map(section -> {
                List<Paper> papers = pService.getAllForSection(section.getId());
                return papers.stream().noneMatch(paper -> paper.getStatus().equals(Status.NEW));
            }).noneMatch(elem -> elem.equals(false));
        } catch (RepositoryException | ServiceException e) {
            LOG.error("Failed to verify if all papers are judged for {}.", user.getEmail());
            throw new ServiceException("Failed to verify if all papers are judged for " + user.getEmail() + ".");
        }
    }

    @Override
    public void update(User user) {
        try {
            uDao.update(user);
            LOG.info("Successfully updated user {}.", user.getEmail());
        } catch (RepositoryException e) {
            LOG.error("Failed to update user with email {}.", user.getEmail());
            throw new ServiceException("Failed to update user with email " + user.getEmail() + ".");
        }
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public User getById(Long id) {
        try {
            return uDao.getById(id);
        } catch (RepositoryException e) {
            LOG.error("Failed to query user by id {}.", id);
            throw new ServiceException("Failed to query user by id " + id + ".");
        }
    }

    @Override
    public User getByEmail(String email) {
        try {
            return uDao.getByEmail(email);
        } catch (RepositoryException e) {
            LOG.error("Failed to query user by email {}.", email);
            throw new ServiceException("Failed to query user by email " + email + ".");
        }
    }

    @Override
    public List<User> getAll() {
        try {
            return uDao.getAll();
        } catch (RepositoryException e) {
            LOG.error("Failed to query users.");
            throw new ServiceException("Failed to query users.");
        }
    }

    @Override
    public List<User> getByRole(String role) {
        try {
            Role actualRole = Role.get(role);
            return uDao.getByRole(actualRole);
        } catch (ModelException e) {
            LOG.error("Invalid role given.");
            throw new ServiceException("Invalid role given to query users.");
        }
    }

}
