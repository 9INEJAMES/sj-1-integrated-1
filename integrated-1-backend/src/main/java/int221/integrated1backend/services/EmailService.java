package int221.integrated1backend.services;
import int221.integrated1backend.entities.in.AccessRight;
import int221.integrated1backend.entities.in.Board;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${web.host}")
    private String WEB_HOST;
    public void sendInviteEmail(String toEmail, String inviterName, AccessRight accessRight, Board board) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String link = WEB_HOST+"board/"+board.getId()+"/collab/invitations";
        String htmlMsg = "<p>You have been invited to collaborate on the <strong>" + board.getName() + "</strong> board.</p>"
                + "<p>Inviter: " + inviterName + "<br>"
                + "Access Right: " + accessRight + "</p>"
                + "<p>Please click the link below to accept the invitation:</p>"
                + "<a href=\"" + link + "\">Accept Invitation</a>";

        helper.setTo(toEmail);
        helper.setSubject(inviterName + " has invited you to collaborate with " + accessRight + " access on the " + board.getName() + " board");
        helper.setFrom("noreply@intproj23.sit.kmutt.ac.th");
        helper.setText(htmlMsg, true);  // true indicates HTML content

        mailSender.send(mimeMessage);
    }
}
