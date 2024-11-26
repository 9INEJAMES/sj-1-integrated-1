package int221.integrated1backend.services;
import int221.integrated1backend.models.AccessRight;
import int221.integrated1backend.entities.in.Board;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${web.host}")
    private String WEB_HOST;
    @Value("${spring.mail.username}")
    private String SENDER_EMAIL;
    @Value("${no.replay.email}")
    private String NO_REPLY_EMAIL;
    public void sendInviteEmail(String toEmail, String inviterName, AccessRight accessRight, Board board) throws MessagingException, UnsupportedEncodingException {
        String link = WEB_HOST+"board/"+board.getId()+"/collab/invitations";
        String htmlMsg = "<p>You have been invited to collaborate on the <strong>" + board.getName() + "</strong> board.</p>"
                + "<p>Inviter: " + inviterName + "<br>"
                + "Access Right: " + accessRight + "</p>"
                + "<p>Please click the link below to accept the invitation:</p>"
                + "<a href=\"" + link + "\"><button style=\"background-color: #4CAF50; color: white; border: none; padding: 10px 20px; font-size: 16px; cursor: pointer; border-radius: 5px; transition: background-color 0.3s ease;\">Accept Invitation</button></a>";
        String subject = inviterName + " has invited you to collaborate with " + accessRight + " access on the " + board.getName() + " board";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(toEmail.trim());
        helper.setSubject(subject);
        helper.setReplyTo(Objects.requireNonNull(NO_REPLY_EMAIL));

        String senderName = "ITBKK-SJ-1";
        helper.setFrom(new InternetAddress(SENDER_EMAIL, senderName));
        helper.setText(htmlMsg, true);  // true indicates HTML content
        mailSender.send(mimeMessage);

    }
}
