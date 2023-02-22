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
 * @author sergi
 */
@WebServlet(name = "UnirsePartida", urlPatterns = {"/UnirsePartida"})
public class UnirsePartida extends HttpServlet {

        
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection con;
        Statement st, st1, st2;
        ResultSet rs, rs1, rs2;
        PrintWriter out;
        String idUsuario, SQL, SQL1, SQL2, rival, idPartida;
        
        try{
            HttpSession session = request.getSession();
            idUsuario = (String)session.getAttribute("idUsuario");
                        
            if(idUsuario == null){              //validamos la sesion
                response.sendRedirect("/conecta4/inicio");
            }
            
            String error = request.getParameter("error");
                        
            Class.forName("com.mysql.cj.jdbc.Driver");
        
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/conectacuatro","root","");
            st = con.createStatement();
            st1 = con.createStatement();
            st2 = con.createStatement();
            
                       
            out = response.getWriter();
            response.setContentType("text/html");
            
            //HTML
            out.println("<!DOCTYPE html><html><head>"); 
            out.println("<title>Conecta 4</title>");
            out.println("<meta charset=UTF-8><meta name=viewport content=width=device-width, initial-scale=1.0>");
            out.println("<link rel=stylesheet href=css/unirsePartida.css type=text/css>");
            out.println("<link rel=stylesheet href=https://use.fontawesome.com/releases/v5.14.0/css/all.css>");
            out.println("<link href=https://fonts.cdnfonts.com/css/candy-beans rel=stylesheet>");
            out.println("<link href=https://fonts.cdnfonts.com/css/candy-beans rel=stylesheet>");
            out.println("<link href=https://fonts.cdnfonts.com/css/sugar-snow rel=stylesheet>");
            out.println("</head><header><p>CONECTA 4</p>");
            out.println("<nav><ul><li><a href=principal><i class=\"fa fa-home\"></i></a></li>");
            out.println("<li><a href=CerrarSesion><i class=\"fas fa-sign-out-alt\"></i></a></li></ul></nav></header>");
            out.println("<body><div class=unirse><div class=id>");
            out.println("<form class=login-form method=post>");
            
            if(error == null){
                out.println("<h1 class=introduce>Introduce el codigo de la partida a la que <br>te quieres unir</h1>");
            }else if(error.equals("0")){
                out.println("<h1 class=error>La partida introducida ya esta en juego!! <br>Vuelve a introducirla</h1>");
            }else if(error.equals("1")){
                out.println("<h1 class=error>La partida introducida no existe!! <br>Vuelve a introducirla</h1>");
            }
            
            out.println("<input type=text placeholder=Código name=idPartida>");
            out.println("<input type=submit name=unirse value=unirse id=unirse></form></div>");
            out.println("<div class=partidas><div class=titulo><h1>Partidas a las que unirte</h1></div>");
            out.println("<div class=desplegable>");
            
            
            //idPartida del usuario a las q puede unirse
            SQL = "SELECT detallesPartida.idPartida, detallesPartida.idUsuario FROM detallesPartida INNER JOIN partida ON detallesPartida.idPartida=partida.idPartida WHERE NOT detallesPartida.idUsuario = "+idUsuario+" AND partida.estado = 1";
            rs = st.executeQuery(SQL);
                        
            int numPartida = 1;          
            
            while(rs.next()){               //Recorremos las partidas a las q me puedo unir
                idPartida = rs.getString(1);
                
                //Nombre del rivel de la partida a la q te puedes unir
                SQL1 = "SELECT idUsuario, nick FROM usuario WHERE idUsuario = " + rs.getString(2) + "";
                rs1 = st1.executeQuery(SQL1);
                
                
                while(rs1.next()){              //Recorremos a los participantes de esa partida
                    
                    if(!rs1.getString(1).equals(idUsuario)){                       //Sacamos el rival

                        //Nombre del usuario rival
                        rival = rs1.getString(1);

                        SQL2 = "SELECT nick FROM usuario WHERE idUsuario = " + rival + "";
                        rs2 = st2.executeQuery(SQL2);

                        rs2.next();

                        out.println("<form Action=/conecta4/UnirsePartida Method=post>");
                        out.println("<button class=button-der value="+idPartida+" name=idPartida><div class=turno>" + numPartida + ". vs " + rs2.getString(1) + "</button>");
                        out.println("</form>");
                        
                        rs2.close();
                    }
                }
                rs1.close();
                numPartida++;
            }
                 
            
            out.println("</div></div></div></body></html>");
            
            st.close();
            st1.close();
            st2.close();
            rs.close();
            out.close();
            con.close();
            
        } catch(Exception e){
          
            System.err.println(e);
        }    
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection con;
        Statement st, st1, st2;
        ResultSet rs;
        String idUsuario, SQL, SQL1, SQL2, idPartida;

        try{
            HttpSession session = request.getSession();
            idUsuario = (String)session.getAttribute("idUsuario");
            
            idPartida = request.getParameter("idPartida");
            
                        
            Class.forName("com.mysql.cj.jdbc.Driver");
        
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/conectacuatro","root","");
            st = con.createStatement();
        
            SQL = "SELECT partida.idPartida, partida.estado FROM partida WHERE idPartida = "+idPartida+"";
            rs = st.executeQuery(SQL);
            
            
            if(rs.next()){
                
                if(rs.getBoolean(2) == false){          //la partida no esta en espera
                    
                    //SACAR MENSAJE POP UP CON Q ESA PARTIDA YA ESTA EN JUEGO
                    
                    response.sendRedirect("/conecta4/UnirsePartida?error=0");
                }else{
                    
                    st1 = con.createStatement();
                    st2 = con.createStatement();
                    
                    SQL1 = "INSERT INTO detallesPartida(idPartida,idUsuario,turno, ganador) VALUES ("+idPartida+","+idUsuario+", false, false)";
                    st1.executeUpdate(SQL1); 
                    
                    SQL2 = "UPDATE partida SET estado = false WHERE idPartida ="+idPartida+"";
                    st2.executeUpdate(SQL2);
                    
                    response.sendRedirect("/conecta4/juego?idPartida=" +idPartida);
                }
                
            }else{
                //SACAR MENSAJE POP UP CON Q ESA PARTIDA NO EXISTE
                                
                response.sendRedirect("/conecta4/UnirsePartida?error=1");
            }
            
            
            
        }catch(Exception e){
          
            System.err.println(e);
            
        
        }
    }

    
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
