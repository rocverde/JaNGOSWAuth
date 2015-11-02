package eu.jangos.auth.service;

import eu.jangos.auth.controller.RealmController;
import eu.jangos.auth.dto.AuthParameterDTO;
import eu.jangos.auth.dto.RealmDTO;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.EntityExistsException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.exception.rest.AppException;
import eu.jangos.auth.exception.rest.AppExceptionMapper;
import eu.jangos.auth.model.Parameter1;
import eu.jangos.auth.model.Realm;
import eu.jangos.auth.utils.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.ArrayList;
import java.util.List;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RealmService is the REST API to handle queries towards the database for
 * the realm entities.
 *
 * @author Warkdev
 * @version 1.0
 */
@Stateless
@Path("/realm")
@Api(value = "/realm", tags = "realm")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RealmService {
    
    private static final Logger logger = LoggerFactory.getLogger(RealmService.class);
    
    @EJB
    RealmController rc;
    
    @GET
    @Path("{id}")
    @ApiOperation(value = "Find realm by id",
            notes = "For a valid response, the id must exist in the database.")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Realm not found")
    })
    /**
     * Find a parameter with the corresponding ID.
     */
    public Response findByID(
            @ApiParam(value = "ID of the parameter to find", required = true) @PathParam("id") int id)
            throws AppException {

        Response response = Utils.checkPermission("realms:find");

        if (response != null) {
            return response;
        }

        Realm realm;

        try {
            realm = rc.get(id);
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }

        RealmDTO realmDTO = Utils.getRealmDTO(realm);
        return Response.status(Response.Status.OK).entity(realmDTO).build();
    }
    
    @GET
    @Path("/findByName/{name}")
    @ApiOperation(value = "Find a realm by his name",
            notes = "For a valid response, the name must exist in the database.")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "Invalid name"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Realm not found")
    })
    /**
     * Find a realm with the corresponding name.
     */
    public Response findByName(
            @ApiParam(value = "Name of the realm to find", required = true) @PathParam("name") String name)
            throws AppException {

        Response response = Utils.checkPermission("realms:find");

        if (response != null) {
            return response;
        }

        Realm realm;
        try {
            realm = rc.get(name);
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

        RealmDTO realmDTO = Utils.getRealmDTO(realm);
        return Response.status(Response.Status.OK).entity(realmDTO).build();
    }
    
    @GET
    @Path("/all")
    @ApiOperation(value = "Returns all realms")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),})
    /**
     * Returns all realms available in the database.
     */
    public Response getAll()
            throws AppException {

        Response response = Utils.checkPermission("realms:find");

        if (response != null) {
            return response;
        }

        List<RealmDTO> listDTO = new ArrayList<>();
        List<Realm> listLocale = rc.getAllRealms();

        listLocale.stream().forEach((r) -> {
            listDTO.add(Utils.getRealmDTO(r));
        });

        return Response.status(Response.Status.OK).entity(listDTO).build();
    }
    
    @POST
    @ApiOperation(value = "Create the realm according to the given information")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "Invalid input or realm already exists"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Realm not found")
    })
    /**
     * Create a new realm according to the given information.
     */
    public Response create(
            @ApiParam(value = "Realm to be created", required = true) RealmDTO realm) {

        Response response = Utils.checkPermission("realms:create");

        if (response != null) {
            return response;
        }

        try {
            return Response.status(Response.Status.OK).entity(Utils.getRealmDTO(this.rc.create(realm))).build();
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
    @ApiOperation(value = "Update the realm according to the given information")
    @ApiResponses(value = {
        @ApiResponse(code = 401, message = "Invalid input"),
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Realm not found")
    })
    /**
     * Update a realm according to the given information.
     */
    public Response update(
            @ApiParam(value = "Realm to be updated", required = true) RealmDTO realm) {

        Response response = Utils.checkPermission("realms:update");

        if (response != null) {
            return response;
        }

        try {
            return Response.status(Response.Status.OK).entity(Utils.getRealmDTO(this.rc.update(realm))).build();
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
    
    @DELETE
    @Path("{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Delete the realm with the given ID")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Realm not found")
    })
    /**
     * Delete the realm with the given ID.
     */
    public Response delete(
            @ApiParam(value = "ID of the realm to be deleted", required = true) @PathParam("id") int id) {

        Response response = Utils.checkPermission("realms:delete");

        if (response != null) {
            return response;
        }

        try {
            this.rc.delete(id);
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }

        return Response.status(Response.Status.OK).build();
    }
}
