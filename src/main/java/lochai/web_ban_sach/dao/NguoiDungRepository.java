package lochai.web_ban_sach.dao;


import lochai.web_ban_sach.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "nguoi-dung") // sử dụng rest api
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {

    public boolean existsByTenDangNhap(String tenDangNhap);

    public boolean existsByEmail(String email);

    public NguoiDung findByTenDangNhap(String tenDangNhap);

    public NguoiDung findByEmail(String email);
}
