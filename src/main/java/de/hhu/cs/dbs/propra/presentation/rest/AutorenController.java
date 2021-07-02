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

public class AutorenController {

    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("autoren")
    @GET
    public Response getAutor(@QueryParam("vorname") String Vorname,
                             @QueryParam("nachname") String Nachname) {

        try (Connection connection = dataSource.getConnection()) {

            String sql =    "SELECT [AUTOR].[ROWID] AS [autorid], " +
                            "[vorname], " +
                            "[nachname] \n" +
                            "FROM [AUTOR]\n" +
                            "WHERE [vorname] LIKE ifnull('%' || ? || '%',[vorname]) AND [nachname] like ifnull('%' || ? || '%',[nachname]);";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setString(1, Vorname);
            preparedStatement.setString(2, Nachname);
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData resultSetMD = resultSet.getMetaData();
            List<Map<String, Object>> entities = new ArrayList<>();
            Map<String, Object> entity;
            while (resultSet.next()) {
                entity = new HashMap<>();
                for(int i = 1; i <= 3; i++) {
                    entity.put(resultSetMD.getColumnName(i), resultSet.getObject(i));
                }
                entities.add(entity);
            }
            resultSet.close();
            connection.close();

            return Response.status(Response.Status.OK).entity(entities).build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @Path("autoren")
    @RolesAllowed({"BIBLIOTHEKAR"})
    @POST
    public Response postGenre(@FormDataParam("vorname") String Vorname,
                              @FormDataParam("nachname") String Nachname) {

        try (Connection connection = dataSource.getConnection()){
            String sql =    "INSERT INTO [AUTOR] ([vorname], [nachname]) VALUES (?, ?);";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setString(1, Vorname);
            preparedStatement.setString(2, Nachname);
            preparedStatement.executeUpdate();
            String rowID = preparedStatement.getGeneratedKeys().getString(1);
            //return Response.status(Response.Status.CREATED).location(uriInfo.getAbsolutePathBuilder().path(rowID).build()).build();
            return Response.created(uriInfo.getAbsolutePathBuilder().path(rowID).build()).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @Path("autoren/{autorid}")
    @RolesAllowed("BIBLIOTHEKAR")
    @DELETE
    public Response deleteExemplar(@PathParam("autorid") Integer AutorID) {


        try (Connection connection = dataSource.getConnection()) {
            String sql_check = "SELECT COUNT(*) FROM [AUTOR] WHERE ROWID = ?";
            PreparedStatement preparedStatement_check = connection.prepareStatement(sql_check);
            preparedStatement_check.closeOnCompletion();
            try{preparedStatement_check.setInt(1, AutorID);}catch(Exception e){}
            ResultSet resultSet = preparedStatement_check.executeQuery();

            if (resultSet.getInt(1) < 1) {
                return Response.status(Response.Status.NOT_FOUND).entity(new SQLException("Kein Autor unter dieser ID vorhanden")).build();
            }

            String sql = "DELETE FROM [AUTOR] WHERE ROWID = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            try{preparedStatement.setInt(1, AutorID);}catch (Exception e){}
            preparedStatement.executeUpdate();
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }
    //curl -u NineToFiveNancy@UniBib.de:3ndlichFreitag -X DELETE http://localhost:8080/autoren/20

}
