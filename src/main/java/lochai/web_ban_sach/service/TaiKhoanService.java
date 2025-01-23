package lochai.web_ban_sach.service;

import lochai.web_ban_sach.dao.NguoiDungRepository;
import lochai.web_ban_sach.entity.NguoiDung;
import lochai.web_ban_sach.entity.Quyen;
import lochai.web_ban_sach.entity.ThongBao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TaiKhoanService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private EmailService emailService;

    public ResponseEntity<?> dangKyNguoiDung(NguoiDung nguoiDung) {

        if (nguoiDungRepository.existsByTenDangNhap(nguoiDung.getTenDangNhap())) {

            return ResponseEntity.badRequest().body(new ThongBao("Tên đăng nhập đã tồn tại"));
        }

        if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {

            return ResponseEntity.badRequest().body(new ThongBao("Tên đăng nhập đã tồn tại"));
        }


        String encryptPassword = passwordEncoder.encode(nguoiDung.getMatKhau());
        nguoiDung.setMatKhau(encryptPassword);

        Quyen quyen = new Quyen();
        quyen.setTenQuyen("USER");
        List<Quyen> list = new ArrayList<>();
        list.add(quyen);
        nguoiDung.setDanhSachQuyen(list);


        // Gán và gửi thông tin kích hoạt
        nguoiDung.setMaKichHoat(taoMaKichHoat());
        nguoiDung.setDaKichHoat(false);

        //lưu
        nguoiDungRepository.save(nguoiDung);

        // gửi email kích hoạt
        guiEmailKichHoat(nguoiDung.getEmail(), nguoiDung.getMaKichHoat());

        return ResponseEntity.ok("Đăng ký thành công");

    }

    private String taoMaKichHoat(){
        // tạo đoạn mã ngẫu nhiên
        return UUID.randomUUID().toString();
    }

    private void guiEmailKichHoat(String email, String maKichHoat){
        String subject = "Kích hoạt tài khoản của bạ tại WebBanSach";
        String text = "Vui lòng sử dụng mã sau để kích hoạt cho tài khoản <" + email + ">: <html> <body> <br/><h1>" + maKichHoat + "</h1> </body> </html>";
        text+="<br/> Click vào đường link để kích hoạt tài khoản: ";
        String url = "http://localhost:3000/kich-hoat/" + email + "/" + maKichHoat;
        text += "<br/> <a href="+ url +"></a>";
        emailService.sendMessage("huongnguyen.dn1973@gmail.com", email, subject, text);
    }

    public ResponseEntity<?> kichHoatTaiKhoan(String email, String maKichHoat){
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email);
        if (nguoiDung == null){
            return ResponseEntity.badRequest().body(new ThongBao("Người dùng không tồn tại!"));
        }

        if (nguoiDung.getDaKichHoat()){
            return ResponseEntity.badRequest().body(new ThongBao("Người dùng đã được kích hoạt!"));
        }

        if (maKichHoat.equals(nguoiDung.getMaKichHoat())){
            nguoiDung.setDaKichHoat(true);
            nguoiDungRepository.save(nguoiDung);
            return ResponseEntity.ok("Kích hoạt tài khoản thành công");
        }else{
            return ResponseEntity.badRequest().body(new ThongBao("Mã kích hoạt không chính xác!"));
        }

    }

}
