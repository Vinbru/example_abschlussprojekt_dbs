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

public class ExemplareController {

    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("exemplare")
    @GET
    public Response getExemplar(@QueryParam("preis") Double Preis,
                                @QueryParam("ausgeliehen") Boolean Ausgeliehen) {

        try (Connection connection = dataSource.getConnection()) {


            String sql = "SELECT DISTINCT [ARTIKEL].[ROWID] AS [artikelid], " +
                    "[EXEMPLAR].[ROWID] AS [exemplarid], " +
                    "[preis] \n" +
                    "FROM [ARTIKEL] NATURAL JOIN [EXEMPLAR]\n" +
                    "WHERE [preis] >= ifnull(?,[preis]) AND " +
                    "ifnull(([EXEMPLAR].[isbn], [EXEMPLAR].[Nummer]) IN (SELECT [isbn], [Nummer] FROM [AUSLEIHE] WHERE [zurueckgegeben] = 0) = ?,true);";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            try{preparedStatement.setDouble(1, Preis);}catch (Exception e){}
            try{preparedStatement.setBoolean(2,Ausgeliehen);}catch(Exception e){}
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData resultSetMD = resultSet.getMetaData();
            List<Map<String, Object>> entities = new ArrayList<>();
            Map<String, Object> entity;
            while (resultSet.next()) {
                entity = new HashMap<>();
                for (int i = 1; i <= 3; i++) {
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

    @Path("exemplare")
    @RolesAllowed({"BIBLIOTHEKAR"})
    @POST
    public Response postExemplar(@FormDataParam("artikelid") Integer ArtikelID,
                                 @FormDataParam("preis") Double Anschaffungspreis,
                                 @FormDataParam("regal") Integer Regal,
                                 @FormDataParam("etage") Integer Etage) {

        try (Connection connection = dataSource.getConnection()) {

            String sql = "INSERT INTO [EXEMPLAR] ([isbn], [preis], [StandortID], [Nummer]) " +
                    "VALUES ((SELECT [isbn] FROM [ARTIKEL] WHERE ROWID = ?), ?, " +
                    "(SELECT [ID] FROM [STANDORT] WHERE [etage] = ? AND [regal] = ?)," +
                    "(SELECT ifnull(MAX(Nummer),0) + 1 FROM EXEMPLAR WHERE [isbn] = (SELECT [isbn] FROM [ARTIKEL] WHERE ROWID = ?)));";


            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            try{preparedStatement.setInt(1, ArtikelID);}catch(Exception e){}
            try{preparedStatement.setDouble(2, Anschaffungspreis);}catch(Exception e){}
            try{preparedStatement.setInt(3, Etage);}catch(Exception e){}
            try{preparedStatement.setInt(4, Regal);}catch(Exception e){}
            try{preparedStatement.setInt(5, ArtikelID);}catch(Exception e){}

            preparedStatement.executeUpdate();

            String rowID = preparedStatement.getGeneratedKeys().getString(1);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(rowID).build()).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
        //curl -u NineToFiveNancy@UniBib.de:3ndlichFreitag -X POST http://localhost:8080/exemplare -F "artikelid=1" -F "preis=2" -F "regal=5" -F "etage=-1" -v

    }

    @Path("exemplare/{exemplarid}")
    @RolesAllowed("BIBLIOTHEKAR")
    @DELETE
    public Response deleteExemplar(@PathParam("exemplarid") Integer ExemplarID) {


        try (Connection connection = dataSource.getConnection()) {
            String sql_check = "SELECT COUNT(*) FROM [EXEMPLAR] WHERE ROWID = ?";
            PreparedStatement preparedStatement_check = connection.prepareStatement(sql_check);
            preparedStatement_check.closeOnCompletion();
            try{preparedStatement_check.setInt(1, ExemplarID);}catch(Exception e){}
            ResultSet resultSet = preparedStatement_check.executeQuery();

            if (resultSet.getInt(1) < 1) {
                return Response.status(Response.Status.NOT_FOUND).entity(new SQLException("Kein Exemplar unter dieser ID vorhanden")).build();
            }

            String sql = "DELETE FROM [EXEMPLAR] WHERE ROWID = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            try{preparedStatement.setInt(1, ExemplarID);}catch(Exception e){}
            preparedStatement.executeUpdate();
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }
    //curl -u NineToFiveNancy@UniBib.de:3ndlichFreitag -X DELETE http://localhost:8080/exemplare/20
}
