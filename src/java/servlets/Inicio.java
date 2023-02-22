package servlets;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author pablo
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;


public class Inicio extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws IOException, ServletException
    {
        RequestDispatcher view = req.getRequestDispatcher("html/inicio.html");
        view.forward(req, res);
    }
    
    
    public void doPost (HttpServletRequest req, HttpServletResponse res)
    {
        Connection con;
        Statement st, st1;
        ResultSet rs, rs1;
        String nombre, SQL,SQL1,contrasena, idUsuario;
                
        try
        {
            nombre = req.getParameter("nombre");
            contrasena = req.getParameter("clave");
            
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/conectacuatro","root","");
            st = con.createStatement();
            
            st1 = con.createStatement();
                    

            SQL = "SELECT idUsuario FROM usuario WHERE nick='"+nombre+"'";
            rs = st.executeQuery(SQL);
                       
            SQL1 = "SELECT password FROM usuario WHERE password='"+contrasena+"'";
            rs1 = st1.executeQuery(SQL1);
            
            if(rs.next()){
                if(rs1.next()){
                    idUsuario = rs.getString(1);
                    
                    HttpSession session = req.getSession();
                    session.setAttribute("idUsuario", idUsuario);
                    
                    res.sendRedirect("/conecta4/principal");
                } else{
                    res.sendRedirect("/conecta4/errorInicio");
                }
            }else{
                res.sendRedirect("/conecta4/errorInicio");
            }
            
            rs.close();
            rs1.close();
            st.close();
            st1.close();
            con.close();
            
        }
        catch(Exception e){
            System.err.println(e);
        }
        
        
        
    } //fin doPost*/
} //fin class

