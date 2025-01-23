package lochai.web_ban_sach.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lochai.web_ban_sach.service.JwtService;
import lochai.web_ban_sach.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
// tóm lại là  kiểm tra và xác thực token JWT cho mỗi yêu cầu HTTP đến server
// JwtFilter là một lớp có chức năng kiểm tra và xác thực JWT cho mỗi yêu cầu HTTP
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private NguoiDungService nguoiDungService;

    //Phương thức này sẽ được gọi cho mỗi yêu cầu HTTP. Nó có trách nhiệm kiểm tra JWT và xác thực người dùng.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //sẽ lấy giá trị của header "Authorization" từ yêu cầu HTTP. Giá trị này thường chứa token xác thực, thường là JWT (JSON Web Token).
        // phần file SachForm (react) có gởi 'Authorization': `Bearer ${token}`,
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        // phần file SachForm có gởi 'Authorization': `Bearer ${token}`,
        if (authHeader != null && authHeader.startsWith("Bearer ")){ // Bearer <token>
            token = authHeader.substring(7);// Lấy token sau chuỗi "Bearer "
            username = jwtService.extractUsername(token); // Trích xuất username từ token
        }
        // SecurityContextHolder.getContext().getAuthentication() == null kiểm tra xem người dùng đã được xác thực hay chưa. Nếu đã có thông tin xác thực thì không cần xác thực lại
        if (username!=null && SecurityContextHolder.getContext().getAuthentication()== null){
            UserDetails userDetails  = nguoiDungService.loadUserByUsername(username);
            // kiểm tra tính hợp lệ của token JWT, xác định rằng token không bị thay đổi và chưa hết hạn
            if (jwtService.validateToken(token, userDetails)){
                //thông tin xác thực của người dùng và quyền hạn đã được lấy từ cơ sở dữ liệu hoặc JWT
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                //thêm thông tin chi tiết về yêu cầu HTTP (như địa chỉ IP và session ID) vào đối tượng xác thực (authToken).
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // lưu thông tin xác thực (authToken) vào SecurityContext, giúp Spring Security nhận diện rằng người dùng đã được xác thực và có thể truy cập các tài nguyên được bảo vệ.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }
        // tiếp tục chuỗi xử lý bộ lọc (filter chain)
        filterChain.doFilter(request, response);
    }
}
