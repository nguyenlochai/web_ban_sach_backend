package lochai.web_ban_sach.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{

    // javaMailSender là để gởi thông tin đi
    private JavaMailSender javaMailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendMessage(String from, String to, String subject, String text) {
        // MimeMailMessage => gửi có đính kèm media(tập tin, hình ảnh,...)
        // SimpleMailMessage => nội dung thông thường

        // SimpleMailMessage message = new SimpleMailMessage();
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }



        // 3 cái này là gởi về địa chỉ email
        //message.setReplyTo();
        //message.setBcc();
        //message.setCc();

        // thực hiện gửi email
        javaMailSender.send(message);
    }
}
