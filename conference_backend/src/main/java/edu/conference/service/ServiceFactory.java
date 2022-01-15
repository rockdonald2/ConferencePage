package edu.conference.service;

import edu.conference.service.impl.SectionServiceImpl;
import edu.conference.service.impl.PaperServiceImpl;
import edu.conference.service.impl.UserServiceImpl;

public class ServiceFactory {

    public UserService getUserService() {
        return new UserServiceImpl();
    }

    public SectionService getSectionService() {
        return new SectionServiceImpl();
    }

    public PaperService getPaperService() {
        return new PaperServiceImpl();
    }

    public static ServiceFactory getInstance() {
        return new ServiceFactory();
    }

}
