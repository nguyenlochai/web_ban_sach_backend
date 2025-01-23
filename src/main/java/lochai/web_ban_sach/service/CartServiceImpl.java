package lochai.web_ban_sach.service;

import lochai.web_ban_sach.dao.CartRepository;
import lochai.web_ban_sach.dao.SachRepository;
import lochai.web_ban_sach.entity.Cart;
import lochai.web_ban_sach.entity.Sach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private SachRepository sachRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public Cart AddCart(Sach sach, int soLuong) {
        Cart cart = new Cart();

        if (sach != null) {
            Sach sach1 = sachRepository.findById(sach.getMaSach()).orElse(null);

            if (sach1 != null) {

                cart.setSoLuong(soLuong);
                cart.setPrice(sach1.getGiaBan());
                cart.setTotalPrice(cart.getPrice() * cart.getSoLuong());


                if (cart.getDanhSachSach() == null) {
                    cart.setDanhSachSach(new ArrayList<>());
                }



                cart.getDanhSachSach().add(sach1);

                cartRepository.save(cart);
                return cart;
            } else {
                throw new RuntimeException("Sách không tồn tại");
            }
        }

        return null;
    }


}
