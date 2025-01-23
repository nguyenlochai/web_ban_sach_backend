package lochai.web_ban_sach.dao;

import jakarta.transaction.Transactional;
import lochai.web_ban_sach.entity.Sach;
import lochai.web_ban_sach.entity.TheLoai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "the-loai") // sử dụng rest api
public interface TheLoaiRepository extends JpaRepository<TheLoai, Integer> {

    Optional<TheLoai> findOptionalByTenTheLoai(String tenTheLoai);

    TheLoai findByTenTheLoai(String tenTheLoai);

    Optional<TheLoai> findById(int id);

//    @Transactional
//    void deleteAllBySach(Sach sach);
}
