package lochai.web_ban_sach.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lochai.web_ban_sach.entity.NguoiDung;
import lochai.web_ban_sach.entity.Quyen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    @Autowired
    NguoiDungService nguoiDungService;

    public static final String SERECT = "5367366B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    // Tạo JWT dựa trên tên đăng nhập
    public String generateToken(String tenDangNhap){
        // claims được sử dụng để chứa thông tin bổ sung (payload) mà bạn muốn nhúng vào token JWT
        Map<String, Object> claims = new HashMap<>();
        NguoiDung nguoiDung = nguoiDungService.findByUsername(tenDangNhap);
        int idNguoiDung = nguoiDung.getMaNguoiDung();


        boolean isAdmin = false;
        boolean isStaff = false;
        boolean isUser = false;

        if (nguoiDung != null && nguoiDung.getDanhSachQuyen().size()>0){
            List<Quyen> list = nguoiDung.getDanhSachQuyen();
            for (Quyen q: list){
                if (q.getTenQuyen().equals("ADMIN")){
                    isAdmin = true;
                }
                if (q.getTenQuyen().equals("STAFF")){
                    isStaff = true;
                }
                if (q.getTenQuyen().equals("USER")){
                    isUser = true;
                }
            }
        }
        claims.put("isAdmin", isAdmin);
        claims.put("isStaff", isStaff);
        claims.put("isUser", isUser);
        claims.put("idNguoiDung", idNguoiDung);


        return CreateToken(claims, tenDangNhap);
    }

    // Tạo JWT với cái claim
    //Claims là chứa payload của token
    private String CreateToken(Map<String, Object> claims, String tenDangNhap){
        return Jwts.builder()
                .setClaims(claims)  // Đặt claims vào payload
                .setSubject(tenDangNhap)
                .setIssuedAt(new Date())
                //.setExpiration(new Date(System.currentTimeMillis()+30*60*1000)) // JWT hết hạng sau 30 phút
                .setExpiration(new Date(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000)) //JWT hết hạng sau 2 ngày

                .signWith(SignatureAlgorithm.HS256, getSigneKey()) //  ký JWT bằng thuật toán HMAC-SHA256 với khóa bí mật được cung cấp từ getSigneKey()
                .compact(); // đóng gói tất cả thông tin này lại
    }

    // lấy serect key
    // làm cho mã hóa phức tạp hơn
    private Key getSigneKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SERECT); // chuyển thành dạng BASE64 và dùng decode giải mã từng chữ như: ví dụ {83, 69, 82, 69, 67, 84}
        return Keys.hmacShaKeyFor(keyBytes);//  tạo ra khóa bí mật từ mảng byte để ký và xác thực JWT bằng HMAC-SHA.
    }



    // trích xuất tất cả các thông tin (claims) từ một token JWT.
    private Claims extractAllClaims(String token){
        // Jwts.parser(): Tạo một đối tượng parser để phân tích cú pháp (parse) token.
        // setSigningKey(getSigneKey() là lấy khóa bí mật của server để ký xem token có đúng không
        //parseClaimsJws(token): Phân tích token và trả về một đối tượng Jws<Claims> chứa tất cả các claims.
        //getBody(): Lấy phần thân (body) của claims từ đối tượng Jws.(có nghĩa là lấy phần payload ở giữa jwt)
        return Jwts.parser().setSigningKey(getSigneKey()).parseClaimsJws(token).getBody();

    }

    // trích xuất thông tin cho 1 claim
    public <T> T extractClaims(String token, Function<Claims, T> claimsTFunction){
        // Gọi lại hàm extractAllClaims(token) để lấy toàn bộ claims từ token
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    // dựa vào hàm extractClaims lấy thời gian hết hạng từ JWT
    public Date extractExpiration(String token){
        return extractClaims(token, Claims::getExpiration);
    }

    // dựa vào hàm extractClaims lấy tên người dùng (username) từ JWT
    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    // dựa vào hàm extractExpiration kiểm tra JWT đã hết hạng
    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    // kiểm tra tính hợp lệ
    // người dùng đăng nhập là có lưu tên đăng nhập vào userDetails rồi
    // kiểm tra tính hợp lệ của token JWT, xác định rằng token không bị thay đổi và chưa hết hạn
    public Boolean validateToken(String token, UserDetails userDetails){
        final String tenDangNhap = extractUsername(token);
        return (tenDangNhap.equals(userDetails.getUsername()) && !isTokenExpired(token)); // isTokenExpired(token) kiểm tra xem token có bị hết hạn không
    }



}
