// ! Will be deleted, just for testing

import model.Role;
import model.Section;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.DAOFactory;
import repository.SectionDAO;
import repository.UserDAO;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        DAOFactory factory = DAOFactory.getInstance();
        UserDAO userDao = factory.getUserDAO();

        User user = new User("lukacs.zsolt@email.com", "qwerty123", "Zsolt", "Lukacs", Role.get("admin"), "BBTE", "Diak", "BSc");

        userDao.create(user);

        User retrievedUser = userDao.getById(user.getId());

        LOG.info(String.valueOf(retrievedUser));

        retrievedUser.setAcademicDegree("MSc");
        userDao.update(retrievedUser);

        retrievedUser = userDao.getByEmail(retrievedUser.getEmail());

        LOG.info(String.valueOf(retrievedUser));

        //userDao.delete(retrievedUser.getId());

        List<User> users = userDao.getAll();
        LOG.info(String.valueOf(users));

        SectionDAO sectionDAO = factory.getSectionDAO();
        Section math = new Section("Matematika", "Matematika leírása", retrievedUser);
        sectionDAO.create(math);

        List<Section> sections = sectionDAO.getAll();

        List<Section> sections2 = sectionDAO.getAllForRepresentative(retrievedUser.getEmail());

        LOG.info(sections.toString());
        LOG.info(sections2.toString());
    }
}
