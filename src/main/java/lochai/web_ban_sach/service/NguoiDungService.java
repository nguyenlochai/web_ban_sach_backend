package lochai.web_ban_sach.service;

import lochai.web_ban_sach.entity.NguoiDung;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface NguoiDungService extends UserDetailsService {
    public NguoiDung findByUsername(String tenDangNhap);
}
