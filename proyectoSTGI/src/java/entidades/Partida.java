package entidades;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author pablo
 */
public class Partida {
    
    private int idPartida;
    private char [][] tablero = new char [6][6];
    private int [] jugadores = new int [2];
    private String [] nicks = new String [2];
    private int [] puntuaciones = new int [2];
    private int turno, ganador;
    private boolean finalizada = false;
    private boolean espera = false;

    
    
    public static int invitacion = 0;
    
    public Partida(int idPartida) {
        this.idPartida = idPartida;
        this.invitacion ++;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, "No ha sido posible cargar el driver mySQL", ex);
        }
        
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/conectacuatro", "root", "");
            Statement st = con.createStatement();
            ResultSet rs;
            String SQL;
            
            // CARGAMOS LOS JUGADORES
            
            SQL = "SELECT idUsuario, turno, ganador FROM detallespartida WHERE idPartida="+idPartida+" ORDER BY IdUsuario DESC";
            rs = st.executeQuery(SQL);
            
            if (rs.next()){
                jugadores[0] = rs.getInt("idUsuario");
                if (rs.getBoolean("turno") == true){turno = rs.getInt("idUsuario");}
                if (rs.getBoolean("ganador") == true){ganador = rs.getInt("idUsuario");}
            }
            if (rs.next()){
                jugadores[1] = rs.getInt("idUsuario");
                if (rs.getBoolean("turno") == true){turno = rs.getInt("idUsuario");}
                if (rs.getBoolean("ganador") == true){ganador = rs.getInt("idUsuario");}
            }
            
            // OBTENEMOS LOS NICKS
            
            SQL = "SELECT nick FROM usuario WHERE idUsuario="+jugadores[0];
            rs = st.executeQuery(SQL);
            if (rs.next()){
                nicks[0] = rs.getString("nick");
            }
            
            SQL = "SELECT nick FROM usuario WHERE idUsuario="+jugadores[1];
            rs = st.executeQuery(SQL);
            if (rs.next()){
                nicks[1] = rs.getString("nick");
            }
            
            // CARGAMOS LA PARTIDA
            
            SQL = "SELECT * FROM movimiento WHERE idPartida="+idPartida+";";
            rs = st.executeQuery(SQL);
            
            Boolean filaVacia = true;
            
            while (rs.next()){
                
                filaVacia = false;
                
                int fila = rs.getInt("fila");
                int columna = rs.getInt( "columna");

                if (rs.getInt("idUsuario") == jugadores[0]){
                    tablero[fila][columna] = 'X';
                }else if (rs.getInt("idUsuario") == jugadores[1]){
                    tablero[fila][columna] = 'O';
                }
                
            }
            
            // Comprobamos si alguna columna de la primera fila esta vacia
            for (int i = 0; i<6; i++){
                if (tablero[0][i] == 0){
                    filaVacia = true;
                }
            }
            
            // COMPROBAMOS SI LA PARTIDA HA FINALIZADO O SI ESTA EN ESPERA
            
            if (!filaVacia){
                // No quedan casillas libres
                // Actualizamos la casilla de finalizada de la BD
                SQL = "UPDATE partida SET finalizada = true WHERE idPartida = ?";
                PreparedStatement ps = con.prepareStatement(SQL);
                ps.setInt(1, idPartida);
                ps.executeUpdate();
                ps.close();
            }
            
            SQL = "SELECT finalizada, estado FROM partida WHERE idPartida="+idPartida+";";
            rs = st.executeQuery(SQL);
            
            if (rs.next()){
                this.finalizada = rs.getBoolean("finalizada");
                this.espera = rs.getBoolean("estado");
            }
            
            if (this.finalizada){
                
                // Hacemos recuento de los puntos
                
                puntuaciones[0] = calcularPuntuacion('X');
                puntuaciones[1]  = calcularPuntuacion('O');
                int idGanador;
                
                if (puntuaciones[0] != puntuaciones[1]) {
                    
                    if (puntuaciones[0] > puntuaciones[1]){
                        idGanador = jugadores[0];
                    }else {
                        idGanador = jugadores[1];
                    }

                    // Asignar ganador
                    SQL = "UPDATE detallespartida SET ganador = true WHERE idPartida = ? AND idUsuario = ?;";
                    PreparedStatement ps = con.prepareStatement(SQL);
                    ps.setInt(1, idPartida);
                    ps.setInt(2, idGanador);
                    ps.executeUpdate();
                    ps.close();
                    
                } else {
                
                    SQL = "UPDATE detallespartida SET empate = true WHERE idPartida = ? AND idUsuario = ?;";
                    PreparedStatement ps = con.prepareStatement(SQL);
                    ps.setInt(1, idPartida);
                    ps.setInt(2, jugadores[0]);
                    ps.executeUpdate();
                    ps.setInt(1, idPartida);
                    ps.setInt(2, jugadores[1]);
                    ps.executeUpdate();
                    
                    ps.close();
                
                }
            }
            
            con.close();
            st.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, "No se ha podido cargar la partida correctamente", ex);
        }
    }
    
    public void anadirMovimiento (int idUsuario, int columna){
        
        try
        {   
            // Obtenemos el tipo de ficha
            char tipoFicha = 0;
            int idRival = 0;
            if (idUsuario == jugadores[0]){
                tipoFicha = 'X';
                idRival = jugadores[1];
                
            }else if (idUsuario == jugadores[1]){
                tipoFicha = 'O';
                idRival = jugadores[0];
            }
            
            
            
            // Obtenemos la ultima fila libre
            int fila = -1;
            for (int i = 0; i<6; i++){
                if (tablero[i][columna] == 0){
                    fila = i;
                } else {
                    break;
                }
            }
            
            if (fila!= -1){
                
                // Añadimos el movimiento a la BD
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/conectacuatro", "root", "");
                PreparedStatement ps;
                
                String SQL = "INSERT INTO movimiento (idPartida,idUsuario,fila,columna) VALUES (?,?,?,?)";
                ps = con.prepareStatement(SQL);
                ps.setInt(1, idPartida);
                ps.setInt(2, idUsuario);
                ps.setInt(3, fila);
                ps.setInt(4, columna);
                ps.executeUpdate();
                
                // Cambiamos el turno
                String disableUser = "UPDATE detallespartida SET turno = false WHERE idPartida = ? AND idUsuario = ?;";
                String enableUser = "UPDATE detallespartida SET turno = true WHERE idPartida = ? AND idUsuario = ?;";
                ps = con.prepareStatement(disableUser);
                ps.setInt(1, idPartida);
                ps.setInt(2, idUsuario);
                ps.executeUpdate();
                ps = con.prepareStatement(enableUser);
                ps.setInt(1, idPartida);
                ps.setInt(2, idRival);
                ps.executeUpdate();
                
                con.close();
                ps.close();
                
            };
            
        } catch (SQLException ex) {
            Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, "No se ha podido ejecutar la query", ex);
        }
        
    }
    
    public String[] getNickJugadores(int idJugador) {
        
        String [] nickJugadores = new String [2];
        
        if (idJugador == jugadores[0]){nickJugadores[0] = nicks[0];}
        else {nickJugadores[1] = nicks[0];}
                
        if (idJugador == jugadores[0]){nickJugadores[1] = nicks[1];}
        else {nickJugadores[0] = nicks[1];}
        
        if (nickJugadores[1] == null) {nickJugadores[1] = "?";}
        
        return nickJugadores;
    }
    
    public String getGanador() {
        
        String nickGanador = "";
        
        if (ganador == jugadores[0]){nickGanador = nicks[0];}
        else if (ganador == jugadores[1]){nickGanador = nicks[1];}
        
        return nickGanador;
    }
    
    public String [][] getPuntuacion () {
        
        String [][] puntuacion = new String [2][2];
        
        puntuacion [0][0] = nicks[0];
        puntuacion [0][1] = puntuaciones[0]+"";
        puntuacion [1][0] = nicks[1];
        puntuacion [1][1] = puntuaciones[1]+"";
        
        return puntuacion;
    }
    
    private int calcularPuntuacion (char jugador){

        int puntos = 0;

        // Puntos Filas
        for (int fila = 0; fila<6; fila++){

            int contador = 0, contadorMax = 0;

            for (int columna = 0; columna<6; columna++){

                contador = (tablero[fila][columna] == jugador)? contador+1 : 0;
                if (contador > contadorMax){contadorMax = contador;}
            }
            switch (contadorMax){
                case 4: puntos += 1;break;
                case 5: puntos += 2;break;
                case 6: puntos += 3;break;
            }
        }
        
        // Puntos Columnas
        for (int columna = 0; columna<6; columna++){

            int contador = 0;
            int contadorMax = 0;
            
            for (int fila = 0; fila<6; fila++){

                contador = (tablero[fila][columna] == jugador)? contador+1 : 0;
                if (contador > contadorMax){contadorMax = contador;}

            }
            switch (contadorMax){
                case 4: puntos += 1;break;
                case 5: puntos += 2;break;
                case 6: puntos += 3;break;
            }
        }
        
        // Puntos Diagonales (topleft - bottomrigth)
        for (int sum = 0; sum <= 6 + 6 - 2; sum++) {

            int contador = 0, contadorMax = 0;

            for (int row = 0; row <= sum; row++) {
                int column = sum - row;
                if (row >= 6 || column >= 6) {
                    continue;
                }
                contador = (tablero[row][column] == jugador)? contador+1 : 0;
                if (contador > contadorMax){contadorMax = contador;}
            }
            switch (contadorMax){
                case 4: puntos += 1;break;
                case 5: puntos += 2;break;
                case 6: puntos += 3;break;
            }
        }
        
        // Puntos Diagonales (toprigth - bottomleft)
        for (int diff = 1 - 6; diff < 6; diff++) {

            int contador = 0, contadorMax = 0;

            for (int row = 0; row < 6; row++) {
                int column = row - diff;

                if (column < 0 || column >= 6) {
                    continue;
                }
                contador = (tablero[row][column] == jugador)? contador+1 : 0;
                if (contador > contadorMax){contadorMax = contador;}
            }
            switch (contadorMax){
                case 4: puntos += 1;break;
                case 5: puntos += 2;break;
                case 6: puntos += 3;break;
            }
        }
        
        return puntos;
    }
    

    /**
     * @return the tablero
     */
    public char[][] getTablero() {
        return tablero;
    }
    
    public boolean getEspera() {
        return espera;
    }
    
     public boolean getFinalizada() {
        return finalizada;
    }

    /**
     * @return the turno
     */
    public int getTurno() {
        return turno;
    }
    
    

    
}
