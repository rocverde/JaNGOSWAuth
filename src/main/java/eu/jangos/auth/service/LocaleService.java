package eu.jangos.auth.service;

import eu.jangos.auth.controller.LocaleController;
import eu.jangos.auth.dto.LocaleDTO;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.InvalidArgumentException;
import eu.jangos.auth.exception.rest.AppException;
import eu.jangos.auth.exception.rest.AppExceptionMapper;
import eu.jangos.auth.model.Locale;
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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * LocaleService is the REST API to handle queries towards the database for the
 * locale entities.
 * @author Warkdev
 * @version 1.0
 */
@Stateless
@Path("/locale")
@Api(value = "/locale", tags = "locale")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocaleService {

    @EJB
    LocaleController lc;
    
    @GET
    @Path("/{id}")
    @ApiOperation(value = "Find locale by id",
            notes = "For a valid response, the id must exist in the database.")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "Locale not found")
    })
    /**
     * Find a locale with the corresponding ID.
     */
    public Response findByID(
            @ApiParam(value = "ID of the locale to find", required = true) @PathParam("id") int id)
            throws AppException {

        Locale locale;
        try {
            locale = lc.getLocale(id);
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }
        
        return Response.status(Response.Status.OK).entity(Utils.getLocaleDTO(locale)).build();
    }
    
    @GET
    @Path("/findByName/{name}")
    @ApiOperation(value = "Find locale by name",
            notes = "For a valid response, the name must exist in the database.")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Invalid name"),        
        @ApiResponse(code = 400, message = "Locale not found"), 
    })
    /**
     * Find a locale with the corresponding name.
     */
    public Response findByName(
            @ApiParam(value = "Name of the locale to find", required = true) @PathParam("name") String name)
            throws AppException {

        Locale locale;
        try {
            locale = lc.getLocaleForString(name);
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
        
        return Response.status(Response.Status.OK).entity(Utils.getLocaleDTO(locale)).build();
    }
    
    @GET
    @Path("/all")
    @ApiOperation(value = "Returns all locale")
    @ApiResponses(value = {

    })
    /**
     * Returns all locale available in the database.
     */
    public Response getAll()
            throws AppException {

        List<LocaleDTO> listDTO = new ArrayList<>();
        List<Locale> listLocale = lc.getAll();
        
        listLocale.stream().forEach((l) -> {
            listDTO.add(Utils.getLocaleDTO(l));
            });
                
        return Response.status(Response.Status.OK).entity(listDTO).build();
    }
}
