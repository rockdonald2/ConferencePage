package edu.conference.utils.commands.impl;

import edu.conference.model.Paper;
import edu.conference.utils.commands.Command;
import edu.conference.utils.commands.CommandException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static edu.conference.utils.Constants.PDF_SUFFIX;
import static edu.conference.utils.Constants.UPLOAD_DIRECTORY;

public class UploadFileCommand implements Command {

    private static final Logger LOG = LoggerFactory.getLogger(UploadFileCommand.class);

    private Part file;
    private ServletContext ctx;
    private Paper paper;
    private String path;

    public UploadFileCommand(Part file, ServletContext ctx, Paper paper) {
        this.file = file;
        this.ctx = ctx;
        this.paper = paper;

        path = ctx.getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
    }

    @Override
    public void execute() {
        String fileName = paper.getUuid() + PDF_SUFFIX;

        try {
            file.write(path + File.separator + fileName);
        } catch (IOException e) {
            LOG.error("Failed to upload file {}.", fileName);
            throw new CommandException("Failed to upload file " + fileName + ".");
        }

        paper.setDoc(fileName);
    }

}
