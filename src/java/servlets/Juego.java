package servlets;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author pablo
 */
//import entidades.Board;
import entidades.Partida;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.http.HttpSession;


public class Juego extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        
        HttpSession session = request.getSession();
        
        if(session.getAttribute("idUsuario") == null){          //validamos la sesion
                response.sendRedirect("/conecta4/inicio");
        }
        
        
        int idPartida = Integer.parseInt((String)request.getParameter("idPartida"));
        session.setAttribute("idPartida", idPartida);
        
                        
        RequestDispatcher view = request.getRequestDispatcher("html/juego.html");
        view.forward(request, response);
        
    }

} //fin class

