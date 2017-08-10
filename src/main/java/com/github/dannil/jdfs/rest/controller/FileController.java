package com.github.dannil.jdfs.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.github.dannil.jdfs.model.File;
import com.github.dannil.jdfs.service.FileService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/files")
public class FileController {

    private static final Logger LOGGER = LogManager.getLogger(FileController.class);

    private FileService service;

    public FileController() {
        this.service = new FileService();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "Index";
    }

    @GET
    @Path("get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context HttpServletRequest request) {
        LOGGER.info("Request for files information from {}", request.getRemoteAddr());

        List<File> files = this.service.getAll();
        if (files != null) {
            GenericEntity<List<File>> generic = new GenericEntity<List<File>>(files) {
            };
            return Response.ok().entity(generic).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path("get/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@Context HttpServletRequest request, @PathParam("id") int id) {
        LOGGER.info("Request for file information with ID {} from {}", id, request.getRemoteAddr());

        File file = this.service.getById(id);
        if (file != null) {
            GenericEntity<File> generic = new GenericEntity<File>(file) {
            };
            return Response.ok().entity(generic).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @POST
    @Path("get/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getByIdOctetStream(@Context HttpServletRequest request, @PathParam("id") int id) {
        LOGGER.info("Request for file with ID {} from {}", id, request.getRemoteAddr());

        LOGGER.info("HELLO!");

        File f = this.service.getById(id);

        return Response.status(403).build();
    }

    // public <T> GenericEntity<List<T>> genericWrap(List<T> files) {
    // GenericEntity<List<File>> generic = new GenericEntity<>(files) {
    // };
    //
    // return generic;
    // }

}