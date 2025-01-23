package lochai.web_ban_sach.controller;

import lochai.web_ban_sach.entity.NguoiDung;
import lochai.web_ban_sach.security.JwtResponse;
import lochai.web_ban_sach.security.LoginRequest;
import lochai.web_ban_sach.service.JwtService;
import lochai.web_ban_sach.service.NguoiDungService;
import lochai.web_ban_sach.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tai-khoan")
@CrossOrigin(origins = "*")
public class TaiKhoanController {

    // PostMapping là dùng Validated
    // GetMapping là dùng RequestBody


    @Autowired
    private TaiKhoanService taiKhoanService;


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/dang-ky")
    public ResponseEntity<?> dangKyNguoiDung(@Validated @RequestBody NguoiDung nguoiDung){
        ResponseEntity<?> response = taiKhoanService.dangKyNguoiDung(nguoiDung);
        return response;

    }

    @GetMapping("/kich-hoat")
    public ResponseEntity<?> kichHoatTaiKhoan(@RequestParam String email, @RequestParam String maKichHoat){
        ResponseEntity<?> response = taiKhoanService.kichHoatTaiKhoan(email, maKichHoat);
        return response;

    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<?> dangNhapNguoiDung(@RequestBody LoginRequest loginRequest){
        // xác thực người dùng bằng tên đăng nhập và mật khẩu
        try {
            // kiểm tra đăng nhập
            // đi đếnn hàm authenticationProvider để xác thực
            //authenticationManager là class chịu trách nhiệm xác thực người dùng trong Spring Security. Nó gọi UserDetailsService để tải thông tin người dùng và kiểm tra thông tin đăng nhập.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            // nếu đănng nhập được, xác thực thành công thì tạo token JWT
            if (authentication.isAuthenticated()){
                final String jwt = jwtService.generateToken(loginRequest.getUsername());
                return ResponseEntity.ok(new JwtResponse(jwt));
            }
        }
        // xác thực không thành công
        // AuthenticationException là đăng nhập k chính xác
        catch (AuthenticationException e){
            return ResponseEntity.badRequest().body("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        return ResponseEntity.badRequest().body("Xác thực không thành công");
    }
}
