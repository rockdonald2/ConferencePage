package edu.conference.utils.commands.impl;

import edu.conference.utils.commands.Command;
import edu.conference.utils.commands.CommandException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static edu.conference.utils.Constants.UPLOAD_DIRECTORY;

public class DownloadFileCommand implements Command {

    private static final Logger LOG = LoggerFactory.getLogger(DownloadFileCommand.class);
    private static final int SIZE = 1024;

    private HttpServletResponse resp;
    private HttpServletRequest req;
    private String path;

    public DownloadFileCommand(HttpServletResponse resp, HttpServletRequest req, String path) {
        this.resp = resp;
        this.path = path;
        this.req = req;
    }

    @Override
    public void execute() {
        resp.setContentType("text/plain");
        resp.setHeader("Content-disposition", "attachment; filename=" + path);

        try (InputStream in = req.getServletContext().getResourceAsStream(File.separator +UPLOAD_DIRECTORY + File.separator + path);
             OutputStream out = resp.getOutputStream()) {

            byte[] buffer = new byte[SIZE];

            int numBytesRead;
            while ((numBytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, numBytesRead);
            }
        } catch (IOException e) {
            LOG.error("Failed to access file by path {}.", path);
            throw new CommandException("Failed to access file by path " + path + ".");
        }
    }

}
