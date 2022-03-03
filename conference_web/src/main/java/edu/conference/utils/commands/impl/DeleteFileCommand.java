package edu.conference.utils.commands.impl;

import edu.conference.model.Paper;
import edu.conference.utils.commands.Command;
import edu.conference.utils.commands.CommandException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static edu.conference.utils.Constants.PDF_SUFFIX;
import static edu.conference.utils.Constants.UPLOAD_DIRECTORY;

public class DeleteFileCommand implements Command {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteFileCommand.class);

    HttpServletRequest req;
    Paper paper;

    public DeleteFileCommand(HttpServletRequest req, Paper paper) {
        this.req = req;
        this.paper = paper;
    }

    @Override
    public void execute() throws CommandException {
        String path = req.getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY + File.separator + paper.getUuid() + PDF_SUFFIX;
        File toDelete = new File(path);

        if (toDelete.exists()) {
            // paper instanciat kapunk, semmi szukseg a directory checkre
            // Files.delete-t is hasznalhatnank, de nekem a boolean ertek megfelelo
            boolean verdict = toDelete.delete();

            if (verdict) {
                LOG.info("Successfully deleted paper {}.", paper.getUuid());
            } else {
                throw new CommandException("Paper " + paper.getUuid() + " could not be deleted.");
            }
        } else {
            LOG.error("Tried to delete non-existing file {}.", paper.getUuid());
            throw new CommandException("Non-existing file.");
        }
    }
}
