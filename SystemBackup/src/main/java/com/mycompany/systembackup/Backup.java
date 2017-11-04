/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.systembackup;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

/**
 *
 * @author Suporte
 */
public class Backup {
    public static void fazerBakup() throws FileNotFoundException, IOException, DbxException{
         //LENDO ARQUIVO CONFIG
        
                FileReader file = new FileReader(new File("config.txt"));
                BufferedReader ler = new BufferedReader(file);
                String linha = null;
                String token = "";
                String email = "";
                String arquivo = "";
                while ((linha = ler.readLine()) != null) {                   
                    
                        if(linha.startsWith("token")){
                           token = linha.substring(6);
                        } else {
                            if(linha.startsWith("email")){
                                email = linha.substring(6);
                            } else {
                                if(linha.startsWith("arquivo")){
                                    arquivo = linha.substring(8);
                                }
                            }
                        }
                    
                }
        final String ACCESS_TOKEN = token;        
        System.out.println(arquivo);
        //COMPACTAR O ARQUIVO
        SimpleDateFormat sdff = new SimpleDateFormat("ddMMyyyyHHmm");
         File file10 = new File(arquivo);

                if (file10.exists()) {
                    int cont;
                    byte[] dados = new byte[(int) file10.length()];
                    try {

                        FileOutputStream destino = new FileOutputStream("banco.zip");
                        ZipOutputStream saida = new ZipOutputStream(new BufferedOutputStream(destino));

                        FileInputStream streamEntrada = new FileInputStream(file10);
                        BufferedInputStream origem = new BufferedInputStream(streamEntrada, (int) file10.length());
                        ZipEntry entry = new ZipEntry(file10.getName());
                        saida.putNextEntry(entry);

                        while ((cont = origem.read(dados, 0, (int) arquivo.length())) != -1) {
                            saida.write(dados, 0, cont);
                        }

                        origem.close();
                        saida.close();
                    } catch (FileNotFoundException ex) {
                        System.out.println("Erro na compactação");
                    } catch (IOException ex) {
                        System.out.println("Erro ao compactar");
                    }
                    System.out.println("Arquivo compactado");
                }
                
        
        
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", Locale.getDefault().toString());
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        // Get current account info
        FullAccount account = client.users().getCurrentAccount();
        System.out.println(account.getName().getDisplayName());

        // Get files and folder metadata from Dropbox root directory
        /*ListFolderResult result = client.files().listFolder("");
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                System.out.println(metadata.getPathLower());
            }

            if (!result.getHasMore()) {
                break;
            }

            result = client.files().listFolderContinue(result.getCursor());
        }*/         
         // Upload "test.txt" to Dropbox
        try (InputStream in = new FileInputStream("banco.zip")) {
            FileMetadata metadata = client.files().uploadBuilder("/"+sdff.format(new Date()) + ".zip")
                .uploadAndFinish(in);
        }
        
        File delet = new File("banco.zip");
        delet.delete();
        
        /////////////////////////////////////////////////////////////
         Properties props = new Properties();
            /** Parâmetros de conexão com servidor Gmail */
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            Session session = Session.getDefaultInstance(props,
                        new javax.mail.Authenticator() {
                             protected PasswordAuthentication getPasswordAuthentication() 
                             {
                                   return new PasswordAuthentication("sistemaparabackup@gmail.com", "sistemabackup123");
                             }
                        });
            /** Ativa Debug para sessão */
            session.setDebug(true);
            try {

                  Message message = new MimeMessage(session);
                  message.setFrom(new InternetAddress("sistemaparabackup@gmail.com")); //Remetente

                  Address[] toUser = InternetAddress //Destinatário(s)
                             .parse("wylliansales@hotmail.com");  
                  message.setRecipients(Message.RecipientType.TO, toUser);
                  message.setSubject("BACKUP");//Assunto
                  message.setText("Backup realizado com sucesso! "+ new Date());
                  /**Método para enviar a mensagem criada*/
                  Transport.send(message);
                  System.out.println("Feito!!!");
             } catch (MessagingException e) {
                  throw new RuntimeException(e);
            }
    }
}
