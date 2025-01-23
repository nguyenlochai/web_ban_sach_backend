package lochai.web_ban_sach.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    //from là người gởi, to là người nhận, subject là tiêu đề, text là nội dung
    public void sendMessage(String from, String to, String subject, String text);

}
