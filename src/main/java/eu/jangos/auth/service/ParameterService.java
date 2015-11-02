package eu.jangos.auth.service;

import eu.jangos.auth.controller.ParameterController;
import eu.jangos.auth.dto.AuthParameterDTO;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.EntityExistsException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.exception.rest.AppException;
import eu.jangos.auth.exception.rest.AppExceptionMapper;
import eu.jangos.auth.model.Parameter1;
import eu.jangos.auth.utils.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * ParameterService is the REST API to handle queries towards the database for
 * the parameter entities.
 *
 * @author Warkdev
 * @version 1.0
 */
@Stateless
@Path("/parameter")
@Api(value = "/parameter", tags = "parameter")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParameterService {

    @EJB
    ParameterController pc;

    @GET
    @Path("{id}")
    @ApiOperation(value = "Find parameter by id",
            notes = "For a valid response, the id must exist in the database.")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Parameter not found")
    })
    /**
     * Find a parameter with the corresponding ID.
     */
    public Response findByID(
            @ApiParam(value = "ID of the parameter to find", required = true) @PathParam("id") int id)
            throws AppException {

        Response response = Utils.checkPermission("parameters:find");

        if (response != null) {
            return response;
        }

        Parameter1 parameter;

        try {
            parameter = pc.getParameter(id);
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }

        AuthParameterDTO parameterDTO = Utils.getParameterDTO(parameter);
        return Response.status(Response.Status.OK).entity(parameterDTO).build();
    }

    @GET
    @Path("/findByName/{key}")
    @ApiOperation(value = "Find parameter by key",
            notes = "For a valid response, the name must exist in the database.")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "Invalid name"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Parameter not found")
    })
    /**
     * Find a locale with the corresponding name.
     */
    public Response findByName(
            @ApiParam(value = "Key of the parameter to find", required = true) @PathParam("key") String key)
            throws AppException {

        Response response = Utils.checkPermission("parameters:find");

        if (response != null) {
            return response;
        }

        Parameter1 parameter;
        try {
            parameter = pc.getParameter(key);
        } catch (InvalidArgumentException iae) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    404,
                    iae.getMessage(),
                    iae.getDeveloperMessage(),
                    ""));
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }

        AuthParameterDTO parameterDTO = Utils.getParameterDTO(parameter);
        return Response.status(Response.Status.OK).entity(parameterDTO).build();
    }

    @GET
    @Path("/all")
    @ApiOperation(value = "Returns all parameters")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),})
    /**
     * Returns all locale available in the database.
     */
    public Response getAll()
            throws AppException {

        Response response = Utils.checkPermission("parameters:find");

        if (response != null) {
            return response;
        }

        List<AuthParameterDTO> listDTO = new ArrayList<>();
        List<Parameter1> listLocale = pc.getAll();

        listLocale.stream().forEach((p) -> {
            listDTO.add(Utils.getParameterDTO(p));
        });

        return Response.status(Response.Status.OK).entity(listDTO).build();
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Delete the parameter with the given ID")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Parameter not found")
    })
    /**
     * Delete the parameter with the given ID.
     */
    public Response delete(
            @ApiParam(value = "ID of the parameter to be deleted", required = true) @PathParam("id") int id) {

        Response response = Utils.checkPermission("parameters:delete");

        if (response != null) {
            return response;
        }

        try {
            this.pc.delete(id);
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }

        return Response.status(Response.Status.OK).build();
    }

    @POST
    @ApiOperation(value = "Create the parameter according to the given information")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "Invalid input or parameter already exists"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Parameter not found")
    })
    /**
     * Create a new parameter according to the given information.
     */
    public Response create(
            @ApiParam(value = "Parameter to be created", required = true) AuthParameterDTO parameter) {

        Response response = Utils.checkPermission("parameters:create");

        if (response != null) {
            return response;
        }

        try {
            return Response.status(Response.Status.OK).entity(Utils.getParameterDTO(this.pc.create(parameter))).build();
        } catch (InvalidArgumentException iae) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    400,
                    iae.getMessage(),
                    iae.getDeveloperMessage(),
                    ""));
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        } catch (EntityExistsException eee) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    400,
                    eee.getMessage(),
                    "",
                    ""));
        }
    }

    @PUT
    @ApiOperation(value = "Update the parameter according to the given information")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "Invalid input"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Parameter not found")
    })
    /**
     * Update a parameter according to the given information.
     */
    public Response update(
            @ApiParam(value = "Parameter to be updated", required = true) AuthParameterDTO parameter) {

        Response response = Utils.checkPermission("parameters:update");

        if (response != null) {
            return response;
        }

        try {
            return Response.status(Response.Status.OK).entity(Utils.getParameterDTO(this.pc.update(parameter))).build();
        } catch (InvalidArgumentException iae) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    400,
                    iae.getMessage(),
                    iae.getDeveloperMessage(),
                    ""));
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }
    }
}
