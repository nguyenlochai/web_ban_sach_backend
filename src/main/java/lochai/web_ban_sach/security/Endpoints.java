package lochai.web_ban_sach.security;

public class Endpoints {

    public static  final String front_end_host = "http://localhost:3000";

    public static final String[] PUBLIC_GET_ENDPOINTS = {
            "/sach",
            "/sach/**",
            "/hinh-anh/**",
            "/nguoi-dung/search/existsByTenDangNhap",
            "/nguoi-dung/search/existsByEmail",
            "/the-loai",
            "/tai-khoan/kich-hoat",
            "/cart",
            "/cart/**"

    };

    public static final String[] PUBLIC_DELETE_ENDPOINTS = {
            "/cart/**"
    };

    public static final String[] PUBLIC_PUT_ENDPOINTS = {
            "/cart/**"
    };




    public static final String[] PUBLIC_POST_ENDPOINTS = {
            "/tai-khoan/dang-ky",
            "/tai-khoan/dang-nhap",
            "/cart/them-cart/**"

    };

    public static final String[] ADMIN_GET_ENDPOINTS = {
            "/nguoi-dung",
            "/nguoi-dung/**",



    };

    public static final String[] ADMIN_POST_ENDPOINTS = {
            "/sach",

    };
    public static final String[] ADMIN_DELETE_ENDPOINTS = {
            "/sach",

    };
}
