package lochai.web_ban_sach.controller;

import lochai.web_ban_sach.dao.CartRepository;
import lochai.web_ban_sach.dao.SachRepository;
import lochai.web_ban_sach.entity.Cart;
import lochai.web_ban_sach.entity.Sach;
import lochai.web_ban_sach.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private SachRepository sachRepository;


    @PostMapping("/them-cart/{maSach}/{soLuong}")
    public ResponseEntity<?> ThemGioHang(@PathVariable int maSach, @PathVariable int soLuong) {
        Sach sach = sachRepository.findById(maSach).orElse(null);

        if (sach != null) {

            cartService.AddCart(sach, soLuong);
            return ResponseEntity.ok("Thêm vào giỏ hàng thành công");
        }

        return ResponseEntity.badRequest().body("Thêm vào giỏ hàng không thành công");
    }




}
