/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.systembackup;

import com.dropbox.core.DbxException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Suporte
 */
public class Agendador {
    
    public void agendarTarefa(int hora, int minuto, int segundo) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hora);
        c.set(Calendar.MINUTE, minuto);
        c.set(Calendar.SECOND, segundo);        
        

        Date time = c.getTime();
        
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {   
                             
                System.out.println("Counting " +
                        " Time: " + new Date()
                );
                try {
                    Backup.fazerBakup();
                    //new Execucoes().tabuada(count);
                    // t.cancel();
                } catch (IOException ex) {
                    System.out.println("Erro na conexão");
                } catch (DbxException ex) {
                    System.out.println("\"Erro na conexão\"");
                }
            }

        }, time );      
       
    }
}
