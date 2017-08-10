package com.github.dannil.jdfs.rest.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.dannil.jdfs.dba.FileDBA;
import com.github.dannil.jdfs.model.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/files")
public class FileController {

    private static final Logger LOGGER = LogManager.getLogger(FileController.class);

    private FileDBA dba;

    public FileController() {
        this.dba = new FileDBA();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "Index";
    }

    @GET
    @Path("get")
    @Produces(MediaType.APPLICATION_JSON)
    public List<File> getAll() {
        LOGGER.info("hello2");

        return this.dba.getAll();
    }

    @GET
    @Path("get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public File getById(@PathParam("id") int id) {
        return this.dba.getById(id);
    }

}