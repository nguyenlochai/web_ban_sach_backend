package lochai.web_ban_sach.service;

import lochai.web_ban_sach.entity.Cart;
import lochai.web_ban_sach.entity.Sach;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public interface CartService {

    public Cart AddCart(Sach sach, int soLuong);

}
