/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Admin
 */
@WebServlet(name = "CrearPartida", urlPatterns = {"/CrearPartida"})
public class CrearPartida extends HttpServlet {

    
        
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        
        Connection con;
        Statement st,st1,st2;
        ResultSet rs;
        PrintWriter out;
        String idUsuario, SQL, SQL1,SQL2;
     
        
        try  {
            /* TODO output your page here. You may use following sample code. */
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/conectacuatro","root","");
            st = con.createStatement();
            st1 = con.createStatement();
            st2 = con.createStatement();

            HttpSession session = req.getSession();
            idUsuario = (String)session.getAttribute("idUsuario");
            
          
           
            //con.setAutoCommit(false);
            SQL="INSERT INTO partida (finalizada,estado) VALUES (0,1)";
            st.executeUpdate(SQL);
                        
            SQL1="SELECT partida.idPartida FROM partida ORDER BY idPartida DESC LIMIT 1";
            rs = st1.executeQuery(SQL1);
                       
            
            rs.next();
            
            SQL2="INSERT INTO detallesPartida (idPartida,idUsuario,turno,ganador) VALUES ("+rs.getString(1)+ "," +idUsuario+ ",1,0)";
            st2.executeUpdate(SQL2);
           
            
            //REDIRIGIMOS A LA PÁGINA PRINCIPAL HASTA QUE SE ENCUENTRE OTRO JUGADORr
            res.sendRedirect("/conecta4/principal");
        
            st.close();
            st1.close();
            st2.close();
            rs.close();
            con.close();
        }
         catch(Exception e){
            System.err.println(e);
            
        }
    }
}
