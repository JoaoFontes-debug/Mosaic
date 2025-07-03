
package com.joaofontes.mosaic.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author JoãoFontes
 */
public class ServicoEmail {
    
    private final Properties mailProperties = new Properties();

    public ServicoEmail() {
        try (InputStream input = new FileInputStream("mail.properties")) {
            mailProperties.load(input);
        } catch (IOException ex) {
            System.err.println("Erro Crítico: O ficheiro 'mail.properties' não foi encontrado na raiz do projeto.");
            System.err.println("Por favor, crie o ficheiro e adicione as suas credenciais de e-mail.");
            ex.printStackTrace();
        }
    }

    public boolean enviarEmailRecuperacao(String destinatarioEmail, String token) {
        if (mailProperties.isEmpty()) {
            System.err.println("Não é possível enviar e-mail: as propriedades de e-mail não foram carregadas.");
            return false;
        }

        final String username = mailProperties.getProperty("mail.sender.username");
        final String password = mailProperties.getProperty("mail.sender.password");

        if (username == null || password == null || username.contains("seu-email-aqui") || password.contains("sua-senha-de-app-aqui")) {
            System.err.println("As credenciais de e-mail não estão configuradas corretamente no ficheiro mail.properties.");
            return false;
        }

        Session session = Session.getInstance(mailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatarioEmail));
            message.setSubject("Recuperação de Senha - Sistema MOSAIC");

            String corpoEmail = "Olá,\n\n"
                    + "Você solicitou a recuperação de senha para o Sistema MOSAIC.\n\n"
                    + "Use o seguinte token para redefinir sua senha. Este token é válido por 15 minutos.\n\n"
                    + "Seu token de recuperação: " + token + "\n\n"
                    + "Se você não solicitou esta recuperação, pode ignorar este e-mail.\n\n"
                    + "Atenciosamente,\n"
                    + "Equipa MOSAIC";

            message.setText(corpoEmail);

            Transport.send(message);
            System.out.println("E-mail de recuperação enviado com sucesso para " + destinatarioEmail);
            return true;

        } catch (MessagingException e) {
            System.err.println("Erro ao enviar o e-mail de recuperação.");
            e.printStackTrace();
            return false;
        }
    }
    
}
