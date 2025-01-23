package lochai.web_ban_sach.dao;

import lochai.web_ban_sach.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "cart") // sử dụng rest api
public interface CartRepository extends JpaRepository<Cart, Integer> {

}
