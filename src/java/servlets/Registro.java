/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlets;

import java.io.*;
import java.sql.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sergi
 */
public class Registro extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        RequestDispatcher view = request.getRequestDispatcher("html/registro.html");
        view.forward(request, response);
        
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        
        Connection con;
        Statement st;
        ResultSet rs;
        String SQL, SQL1;
        
        try{
            String nombre = request.getParameter("nombre");
            String password = request.getParameter("password");
            String correo = request.getParameter("correo");

            
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/conectacuatro","root","");
            st = con.createStatement();
                       
            SQL1 = "SELECT nick FROM usuario WHERE nick='"+nombre+"'";
            rs = st.executeQuery(SQL1);
            
            if(rs.next()){
                RequestDispatcher view = request.getRequestDispatcher("html/errorRegistro.html");
                view.forward(request, response);
            
            }else{
                SQL = "INSERT INTO usuario(nick, correo, password) VALUES ('"+nombre+"', '"+correo+"', '"+password+"')";
                st.executeUpdate(SQL);
                response.sendRedirect("/conecta4/inicio");
            }
            
            
            st.close();
            rs.close();
            con.close();
            
        }catch(Exception e){
            System.out.println(e);
            
        }
        
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
