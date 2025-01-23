package lochai.web_ban_sach.dao;


import jakarta.transaction.Transactional;
import lochai.web_ban_sach.entity.HinhAnh;
import lochai.web_ban_sach.entity.Sach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "hinh-anh") // sử dụng rest api
public interface HinhAnhRepository extends JpaRepository<HinhAnh, Integer> {

    @Transactional
    void deleteAllBySach(Sach sach);
}
