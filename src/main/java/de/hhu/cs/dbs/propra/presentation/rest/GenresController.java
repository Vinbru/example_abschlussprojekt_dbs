package de.hhu.cs.dbs.propra.presentation.rest;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class GenresController {

    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("genres")
    @GET
    public Response getGenre(@QueryParam("bezeichnung") String Bezeichnung) {

        try (Connection connection = dataSource.getConnection()) {

            String sql =    "SELECT [GENRE].[ROWID] AS [genreid], " +
                            "[bezeichnung] \n" +
                            "FROM [GENRE]\n" +
                            "WHERE [bezeichnung] LIKE ifnull('%' || ? || '%', [bezeichnung]);";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setString(1, Bezeichnung);
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData resultSetMD = resultSet.getMetaData();
            List<Map<String, Object>> entities = new ArrayList<>();
            Map<String, Object> entity;
            while (resultSet.next()) {
                entity = new HashMap<>();
                for(int i = 1; i <= 2; i++) {
                    entity.put(resultSetMD.getColumnName(i), resultSet.getObject(i));
                }
                entities.add(entity);
            }
            resultSet.close();
            connection.close();

            return Response.status(Response.Status.OK).entity(entities).build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @Path("genres")
    @RolesAllowed({"BIBLIOTHEKAR"})
    @POST
    public Response postGenre(@FormDataParam("bezeichnung") String Bezeichnung) {
        try (Connection connection = dataSource.getConnection()){
            String sql =    "INSERT INTO [GENRE] ([bezeichnung]) VALUES (?);";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setString(1, Bezeichnung);
            preparedStatement.executeUpdate();
            String rowID = preparedStatement.getGeneratedKeys().getString(1);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(rowID).build()).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }
}
