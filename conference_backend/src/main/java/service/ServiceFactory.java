package service;

import service.impl.PaperServiceImpl;
import service.impl.UserServiceImpl;

public class ServiceFactory {

    public UserService getUserService() {
        return new UserServiceImpl();
    }

    public SectionService getSectionService() {
        throw new UnsupportedOperationException();
    }

    public PaperService getPaperService() {
        return new PaperServiceImpl();
    }

    public static ServiceFactory getInstance() {
        return new ServiceFactory();
    }

}
