package eu.jangos.auth.service;

import eu.jangos.auth.controller.RealmTimeZoneController;
import eu.jangos.auth.dto.RealmTimeZoneDTO;
import eu.jangos.auth.exception.DatabaseIntegrityException;
import eu.jangos.auth.exception.rest.AppException;
import eu.jangos.auth.exception.rest.AppExceptionMapper;
import eu.jangos.auth.model.RealmTimeZone;
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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * RealmTimezoneService is the REST API to handle queries towards the database
 * for the Realm time zones entities.
 *
 * @author Warkdev
 * @version 1.0
 */
@Stateless
@Path("/timezone")
@Api(value = "/timezone", tags = "timezone")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RealmTimezoneService {

    @EJB
    RealmTimeZoneController rtzc;

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Find a realm time zone by id",
            notes = "For a valid response, the id must exist in the database.")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),
        @ApiResponse(code = 404, message = "Realm time zone not found")
    })
    /**
     * Find a realm time zone with the corresponding ID.
     */
    public Response findByID(
            @ApiParam(value = "ID of the realm time zone to find", required = true) @PathParam("id") int id)
            throws AppException {

        Response response = Utils.checkPermission("timezone:find");

        if (response != null) {
            return response;
        }
        
        RealmTimeZone zone;
        try {
            zone = rtzc.get(id);
        } catch (DatabaseIntegrityException die) {
            return new AppExceptionMapper().toResponse(new AppException(Response.Status.NOT_FOUND.getStatusCode(),
                    404,
                    die.getMessage(),
                    "",
                    ""));
        }

        return Response.status(Response.Status.OK).entity(Utils.getRealmTimeZoneDTO(zone)).build();
    }

    @GET
    @Path("/all")
    @ApiOperation(value = "Returns all realm time zones")
    @ApiResponses(value = {
        @ApiResponse(code = 403, message = "Forbidden access"),})
    /**
     * Returns all locale available in the database.
     */
    public Response getAll()
            throws AppException {

        Response response = Utils.checkPermission("timezone:find");

        if (response != null) {
            return response;
        }
        
        List<RealmTimeZoneDTO> listDTO = new ArrayList<>();
        List<RealmTimeZone> listZone = rtzc.getAll();

        listZone.stream().forEach((r) -> {
            listDTO.add(Utils.getRealmTimeZoneDTO(r));
        });

        return Response.status(Response.Status.OK).entity(listDTO).build();
    }
}
