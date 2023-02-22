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
import java.text.DecimalFormat;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author sergi
 */

public class Principal extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection con;
        Statement st, st1, st2, st3, st4, st5;
        ResultSet rs, rs1, rs2, rs3, rs4, rs5;
        PrintWriter out;
        String idUsuario, SQL, SQL1, SQL3, SQL4, SQL5,rival, idPartida;
                
        try{
            HttpSession session = request.getSession();
            idUsuario = (String)session.getAttribute("idUsuario");
                        
            if(idUsuario == null){              //validamos la sesion
                response.sendRedirect("/conecta4/inicio");
            }
                            
            Class.forName("com.mysql.cj.jdbc.Driver");

            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/conectacuatro","root","");
            st = con.createStatement();
            st1 = con.createStatement();
            st2 = con.createStatement();
            st3 = con.createStatement();
            st4 = con.createStatement();
            st5 = con.createStatement();
            

            
            //BUSCAR idPARTIDAS de mis partidas
            SQL = "SELECT * FROM detallesPartida WHERE idUsuario=" + idUsuario + "";
            rs = st.executeQuery(SQL);
            
            
            out = response.getWriter();
            response.setContentType("text/html");

            out.println("<!DOCTYPE html><html><head>");
            out.println("<meta charset=UTF-8>");
            out.println("<title>Principal</title>");
            out.println("<link rel=stylesheet href=css/principal.css type=text/css> ");
            out.println("<link rel=stylesheet href=https://use.fontawesome.com/releases/v5.14.0/css/all.css>");
            out.println("<link href=https://fonts.cdnfonts.com/css/candy-beans rel=stylesheet>");
            out.println("<link href=https://fonts.cdnfonts.com/css/sugar-snow rel=stylesheet>");
            out.println("<meta http-equiv=\"refresh\" content=\"5\">");
            out.println("</head>");
            out.println("<header><p>CONECTA 4</p>");
            out.println("<nav><ul><li><a href=principal><i class=\"fa fa-home\"></i></a></li>");
            out.println("<li><a href=CerrarSesion><i class=\"fas fa-sign-out-alt\"></i></a></li></ul></nav></header>");
            out.println("<body><div class=fondo><div class=izquierda>");
            out.println("<div class=titulo><h1>PARTIDAS EN CURSO</h1></div>");
            out.println("<div class=botones-izq>");
            
            out.println("<Form Action=/conecta4/CrearPartida method=get>");
            out.println("<button class=button-izq>Iniciar partida</button></form><br><br>");
            
            out.println("<Form Action=/conecta4/UnirsePartida method=get>");
            out.println("<button class=button-izq>Unirse a partida</button></form></div>");
            
            //GANADAS
            SQL3 = "SELECT COUNT(detallesPartida.ganador) FROM detallespartida WHERE idUsuario="+idUsuario+" AND ganador=1";
            rs3 = st3.executeQuery(SQL3);
            rs3.next();
            double ganadas = rs3.getInt(1);
            
            //JUGADAS Y FINALIZADAS
            SQL4 = "SELECT COUNT(partida.finalizada) FROM partida INNER JOIN detallespartida ON partida.idPartida = detallespartida.idPartida WHERE idUsuario = "+idUsuario+" AND partida.finalizada = 1";
            rs4 = st4.executeQuery(SQL4);
            rs4.next();
            double finalizadas = rs4.getInt(1);
            
            //EN CURSO
            SQL5 = "SELECT COUNT(partida.finalizada) FROM partida INNER JOIN detallespartida ON partida.idPartida = detallespartida.idPartida WHERE idUsuario = "+idUsuario+" AND partida.finalizada = 0";
            rs5 = st5.executeQuery(SQL5);
            rs5.next();
            double enCurso = rs5.getInt(1);
            
            DecimalFormat df = new DecimalFormat("#.##");
            
            out.println("<div class=estadisticas><h2>Estadísticas</h2>");
            out.println("<p>Partidas ganadas: " + ganadas + " (" + df.format((ganadas/finalizadas)*100) + "%)</p><BR><BR>");
            out.println("<p>Partidas en curso: " + enCurso + "</p><BR><BR>");
            out.println("<p>Partidas jugadas: " + finalizadas + "</p></div>");
            out.println("</div>");
            out.println("<div class=derecha><div class=titulo><h1>PARTIDAS EN CURSO</h1></div>");
            out.println("<div class=partidas>");
                       
            
            
            int numPartida=1;
            
            while(rs.next()){       //Recorremos las partidas
                
                //sacamos el rival de las partidas q tengo
                SQL1 = "SELECT detallesPartida.idPartida, detallesPartida.idUsuario, detallesPartida.turno, detallesPartida.ganador, detallesPartida.empate, partida.finalizada, partida.estado FROM detallespartida INNER JOIN partida ON detallespartida.idPartida=partida.idPartida WHERE detallesPartida.idPartida = " + rs.getString(1) + "";
                rs1 = st1.executeQuery(SQL1);
                                                
                while(rs1.next()){                                                          //Recorremos los participantes de la partida
                    idPartida = rs1.getString(1);
                    
                    rival = rs1.getString(2);    
                                        
                    if(!rival.equals(idUsuario)){                            //Sacamos el rival
                        
                                                
                        SQL = "SELECT nick FROM usuario WHERE idUsuario = " + rival + "";
                        rs2 = st2.executeQuery(SQL);
                        rs2.next();
        

                        if(rs1.getBoolean(6) == true){              //COMPROBAMOS SI ESTA FINALIZADA

                            if(rs1.getBoolean(4) == true){          //COMPROBAMOS SI ES EL GANADOR
                                out.println("<form Action=/conecta4/juego method=get>");
                                out.println("<button class=button-perdida value="+idPartida+" name=idPartida><div class=turno><p>" + numPartida + ". " + rs2.getString(1) + "</p><div class=circulo-perdida></div><BR><p>PERDIDA!!</p></button>");
                                out.println("</form>");

                            }else if(rs1.getBoolean(5) == true){
                            
                                out.println("<form Action=/conecta4/juego method=get>");
                                out.println("<button class=button-empate value="+idPartida+" name=idPartida><div class=turno><p>" + numPartida + ". " + rs2.getString(1) + "</p><div class=circulo-empate></div><BR><p>EMPATE!!</p></button>");
                                out.println("</form>");
                            
                            } else{
                                out.println("<form Action=/conecta4/juego method=get>");
                                out.println("<button class=button-ganada value="+idPartida+" name=idPartida><div class=turno><p>" + numPartida + ". " + rs2.getString(1) + "</p><div class=circulo-ganada></div><BR><p>GANADA!!</p></button>");
                                out.println("</form>");
                            }


                        }else{          //NO esta finalizada

                            if(rs1.getBoolean(3) == true){          //COMPROBAMOS EL TURNO
                                out.println("<form Action=/conecta4/juego method=get>");
                                out.println("<button class=button-der value="+idPartida+" name=idPartida><div class=turno><p>" + numPartida + ". " + rs2.getString(1) + "</p><div class=circulo-rojo></div><BR><p>En curso</p></button>");
                                out.println("</form>");

                            }else{
                                out.println("<form Action=/conecta4/juego method=get>");
                                out.println("<button class=button-der value="+idPartida+" name=idPartida><div class=turno><p>" + numPartida + ". " + rs2.getString(1) + "</p><div class=circulo-verde></div><BR><p>En curso</p></button>");
                                out.println("</form>");
                            }
                        }

                        rs2.close();
                        
                    }else{              //YO
                        
                        if(rs1.getBoolean(7) == true){              //COMPROBAMOS ESTADO
                            out.println("<form Action=/conecta4/juego method=get>");
                            out.println("<button class=button-esp value="+idPartida+" name=idPartida><div class=turno><p>" + numPartida + ". </p><p>En espera</p><div class=circulo-espera></div><BR><p>Código de invitación: "+idPartida+"</p></button>");
                            out.println("</form>");
                        }
                    }
                    
                }
                numPartida++;
                rs1.close();
            }

    
            out.println("</div></div></div></body></html>");
            
            rs.close();
            
            st.close();
            st1.close();
            st2.close();
            st3.close();
            st4.close();
            st5.close();
            rs3.close();
            rs4.close();
            rs5.close();
            con.close();
            out.close();
        }
        catch(Exception e){
            System.err.println(e);
            System.out.println("ERROR en el catch");
        }
    }
    

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
