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

public class AusleihenController {

    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @Path("ausleihen")
    @RolesAllowed({"KUNDE"})
    @GET
    public Response getAusleihe(@QueryParam("zurueckgegeben") Boolean Zurueckgegeben,
                              @QueryParam("beginn") String Beginn) {

        try (Connection connection = dataSource.getConnection()) {

            String username = securityContext.getUserPrincipal().getName();

            String sql =    "SELECT [AUSLEIHE].[ROWID] AS [ausleiheid], " +
                            "[EXEMPLAR].[ROWID] AS [exemplarid], " +
                            "[zurueckgegeben], " +
                            "DATE([beginn]) AS [beginn], " +
                            "DATE([ende]) AS [ende] \n" +
                            "FROM [AUSLEIHE] NATURAL JOIN [EXEMPLAR] \n" +
                            "WHERE [email] = ? AND [beginn] <= datetime(ifnull(?,[beginn])) AND [zurueckgegeben] = ifnull(?,[zurueckgegeben]);";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, Beginn);
            try{preparedStatement.setBoolean(3,Zurueckgegeben);}catch(Exception e){}

            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData resultSetMD = resultSet.getMetaData();
            List<Map<String, Object>> entities = new ArrayList<>();
            Map<String, Object> entity;
            while (resultSet.next()) {
                entity = new HashMap<>();
                for(int i = 1; i <= 5; i++) {
                    if(resultSetMD.getColumnName(i).equals("zurueckgegeben"))
                    {
                        entity.put(resultSetMD.getColumnName(i), resultSet.getObject(i).equals(1));
                    }
                    else entity.put(resultSetMD.getColumnName(i), resultSet.getObject(i));
                }
                entities.add(entity);
            }
            resultSet.close();

            return Response.status(Response.Status.OK).entity(entities).build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @RolesAllowed({"KUNDE"})
    @Path("ausleihen")
    @POST
    public Response postAusleihen(@FormDataParam("exemplarid") Integer ExemplarID,
                              @FormDataParam("zurueckgegeben") Boolean Zurueckgegeben,
                              @FormDataParam("beginn") String Beginn,
                              @FormDataParam("ende") String Ende) {


        try (Connection connection = dataSource.getConnection()){
            String sql =    "WITH EXEMPLAR_CT AS (SELECT * FROM EXEMPLAR WHERE ROWID = ?) \n" +
                            "INSERT INTO [Ausleihe] ([isbn], [Nummer], [zurueckgegeben], [beginn], [ende], [email]) " +
                            "VALUES ((SELECT [isbn] FROM [EXEMPLAR_CT]), (SELECT [Nummer] FROM [EXEMPLAR_CT]), ?, datetime(?), datetime(?), ? );";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            try{preparedStatement.setInt(1, ExemplarID);}catch(Exception e){}
            try{preparedStatement.setBoolean(2, Zurueckgegeben);}catch(Exception e){}
            preparedStatement.setString(3, Beginn);
            preparedStatement.setString(4, Ende);
            preparedStatement.setString(5, securityContext.getUserPrincipal().getName());

            preparedStatement.executeUpdate();
            String rowID = preparedStatement.getGeneratedKeys().getString(1);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(rowID).build()).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }

        //curl -u Jojo@Allstar.net:IsThis4JoJoReference -X POST http://localhost:8080/ausleihen -F "exemplarid=12" -F "zurueckgegeben=false" -F "beginn=2021-03-17" -F "ende=2021-03-24" -v
    }

    @Path("ausleihen/{ausleiheid}")
    @RolesAllowed({"BIBLIOTHEKAR"})
    @PATCH
    public Response patchAusleihen(@PathParam("ausleiheid") Integer AusleiheID,
                                  @QueryParam("zurueckgegeben") Boolean Zurueckgegeben,
                                  @QueryParam("beginn") String Beginn,
                                  @QueryParam("ende") String Ende) {

        try (Connection connection = dataSource.getConnection()){

            String sql_check = "SELECT COUNT(*) FROM [AUSLEIHE] WHERE ROWID = ?";
            PreparedStatement preparedStatement_check = connection.prepareStatement(sql_check);
            preparedStatement_check.closeOnCompletion();
            try{preparedStatement_check.setInt(1, AusleiheID);}catch(Exception e){}
            ResultSet resultSet = preparedStatement_check.executeQuery();

            if (resultSet.getInt(1) < 1) {
                return Response.status(Response.Status.NOT_FOUND).entity(new SQLException("Keine Ausleihe unter dieser ID vorhanden")).build();
            }

            String sql = "UPDATE [AUSLEIHE] \n" +
                    "SET [zurueckgegeben] = ifnull(?,[zurueckgegeben]), [beginn] = datetime(ifnull(?,[beginn])), [ende] = datetime(ifnull(?,[ende])) \n" +
                    "WHERE ROWID = ?;";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            try{preparedStatement.setBoolean(1, Zurueckgegeben);}catch(Exception e){}
            preparedStatement.setString(2, Beginn);
            preparedStatement.setString(3, Ende);
            preparedStatement.setInt(4, AusleiheID);
            preparedStatement.executeUpdate();

            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }

        //curl -u NineToFiveNancy@UniBib.de:3ndlichFreitag -X PATCH "http://localhost:8080/ausleihen/5?zurueckgegeben=true&beginn=2021-03-17&ende=2021-03-24"
    }


}
