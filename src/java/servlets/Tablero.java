/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlets;

import entidades.Partida;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 *
 * @author pablo
 */
@WebServlet(name = "Tablero", urlPatterns = {"/Tablero"})
public class Tablero extends HttpServlet {

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        if(session.getAttribute("idUsuario") == null){              //validamos la sesion
            response.sendRedirect("/conecta4/inicio");
        }

        int idUsuario = Integer.parseInt((String)session.getAttribute("idUsuario"));
        int idPartida = (int) session.getAttribute("idPartida");
        

        PrintWriter out = response.getWriter();
        out.print(TableroHtml(idPartida, idUsuario));
        out.close();
 
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        if(session.getAttribute("idUsuario") == null){              //validamos la sesion
            response.sendRedirect("/conecta4/inicio");
        }

        int idUsuario = Integer.parseInt((String)session.getAttribute("idUsuario"));
        int idPartida = (int) session.getAttribute("idPartida");
              
        int columna = Integer.parseInt(request.getParameter("columna"));

        Partida partida = new Partida (idPartida);
        partida.anadirMovimiento(idUsuario, columna);
        
        // Devolvemos el la tabla
        PrintWriter out = response.getWriter();
        out.print(TableroHtml(idPartida,idUsuario));
        out.close();
        
    }
    
    
    private String TableroHtml (int idPartida, int idUsuario){
        
        Partida partida = new Partida (idPartida);
        char[][] tablero = partida.getTablero();
        String [] nickJugadores = partida.getNickJugadores(idUsuario);
        String [] nickJugadoresSub = new String [2];
        
        String ganador = partida.getGanador();
        
        boolean espera = partida.getEspera();
        boolean finalizada = partida.getFinalizada();
        
        String turno;
        if (idUsuario != partida.getTurno()){
            turno = "disabled";
            nickJugadoresSub[0] = nickJugadores[0];
            nickJugadoresSub[1] = "<u>"+nickJugadores[1]+"</u>";
        } else {
            turno = "";
            nickJugadoresSub[0] = "<u>"+nickJugadores[0]+"</u>";
            nickJugadoresSub[1] = nickJugadores[1];
        }
    
        if (finalizada || espera){turno = "disabled";}
        
        
        String html = "";
        html += "<p class='vs'>"+nickJugadoresSub[0]+" VS "+nickJugadoresSub[1]+"</p>";
        html += "<table class=board><tbody>";
        for (int i=0;i<tablero.length; i++){
            html +=  "<tr>";
            for (int j=0; j<tablero[i].length; j++){
                
                String id = ""+j; //columna

                if (tablero[i][j] == 'X' ){
                    html += "<td> <button "+turno+" class=\"slot_X\" type=\"button\" onclick='postTablero(\""+id+"\")'></button></td>";
                } else if (tablero[i][j] == 'O' ){
                    html += "<td> <button "+turno+" class=\"slot_O\" type=\"button\" onclick='postTablero(\""+id+"\")'></button></td>";
                } else {
                    html += "<td> <button "+turno+" class=\"slot\" type=\"button\" onclick='postTablero(\""+id+"\")'></button></td>";
                }
            }
            html += "</tr>";
        }
        html += "</tbody></table>";
        
        if (finalizada){
            
            try {
                TimeUnit.MILLISECONDS.sleep(350);
            } catch (InterruptedException ex) {
                Logger.getLogger(Tablero.class.getName()).log(Level.SEVERE, "Se ha interrumpido la espera", ex);
            }
            
            if (ganador == nickJugadores[0]){
                html += "<p style=\"background-color: rgba(52, 152, 219, 0.8);\" class=ganador>GANADA</p>";
            } else if (ganador == nickJugadores[1]){
                html += "<p style=\"background-color: rgba(231, 76, 60, 0.8);\" class=ganador>PERDIDA</p>";
            } else {
                html += "<p style=\"background-color: rgba(131, 145, 146, 0.8);\" class=ganador>EMPATE</p>";
            }
            
            String [][] puntuacion = partida.getPuntuacion();
            html += "<p class=puntuacion>"+puntuacion[0][0]+": "+puntuacion[0][1]+"<br>"+puntuacion[1][0]+": "+puntuacion[1][1]+"</p>";
        }
         
        if (espera){
            
            html += "<p class=codigo>Codigo Invitacion: "+idPartida+"</p>";
            
        }
        
        return html;
    
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
