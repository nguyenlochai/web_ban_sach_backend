

package lochai.web_ban_sach.controller;

import lochai.web_ban_sach.Dto.SachDto;
import lochai.web_ban_sach.Dto.TheLoaiDto;
import lochai.web_ban_sach.dao.HinhAnhRepository;
import lochai.web_ban_sach.dao.SachRepository;

import lochai.web_ban_sach.dao.TheLoaiRepository;
import lochai.web_ban_sach.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/sach")
@CrossOrigin(origins = "*")
public class SachController {

    @Autowired
    private SachRepository sachRepository;

    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @Autowired
    private TheLoaiRepository theLoaiRepository;


    @PostMapping("/them-sach")
    public ResponseEntity<Sach> themSach(@Validated @RequestBody SachDto sachDto) throws ThongBao {

        if (sachDto != null) {
            // Tạo đối tượng Sach mới
            Sach sachSave = new Sach();
            sachSave.setTenSach(sachDto.getTenSach());
            sachSave.setTenTacGia(sachDto.getTenTacGia());
            sachSave.setISBN(sachDto.getIsbn());
            sachSave.setMoTa(sachDto.getMoTa());
            sachSave.setGiaNiemYet(sachDto.getGiaNiemYet());
            sachSave.setGiaBan(sachDto.getGiaBan());
            sachSave.setSoLuong(sachDto.getSoLuong());

            // Xử lý danh sách thể loại
            List<TheLoai> theLoais = new ArrayList<>();
            for (String tenTheLoai : sachDto.getTheLoaiSach()) {
                TheLoai theLoai = theLoaiRepository.findByTenTheLoai(tenTheLoai);
                if (theLoai != null) {
                    theLoais.add(theLoai);
                } else {
                    throw new ThongBao("không tìm thấy thể loại");
                }
            }
            sachSave.setDanhSachTheLoai(theLoais);




            // Lưu sách vào cơ sở dữ liệu trước
            Sach savedSach = sachRepository.save(sachSave);



            // Tạo danh sách HinhAnh từ DTO và liên kết với sach1
            // phương thức được gọi từ đối tượng sachDto, và nó trả về danh sách các đối tượng HinhAnhDto (dữ liệu hình ảnh từ frontend mà bạn đã gửi lên).
            List<HinhAnh> hinhAnhs = sachDto.getHinhAnhs().stream().map(hinhAnhDto -> {
                HinhAnh hinhAnh = new HinhAnh();
                hinhAnh.setDuLieuAnh(hinhAnhDto.getDuLieuAnh());
                hinhAnh.setTenHinhAnh(hinhAnhDto.getTenHinhAnh());

                hinhAnh.setSach(savedSach); // Liên kết hình ảnh với sách
                return hinhAnh;
            }).collect(Collectors.toList());


            // Lưu danh sách hình ảnh vào cơ sở dữ liệu
            hinhAnhRepository.saveAll(hinhAnhs);

            // Trả về đối tượng sách đã được lưu
            return ResponseEntity.ok(savedSach);
        }
        return ResponseEntity.badRequest().build();
    }


    @PostMapping("/them-the-loai-sach")
    public ResponseEntity<TheLoai> themTheLoaiSach(@Validated @RequestBody TheLoaiDto theLoaiDto) throws ThongBao {
        if (theLoaiDto == null || theLoaiDto.getTenTheLoai() == null || theLoaiDto.getTenTheLoai().trim().isEmpty()) {
            throw new ThongBao("Dữ liệu không hợp lệ: Tên thể loại không được để trống");
        }
        TheLoai theLoaiExit = theLoaiRepository.findByTenTheLoai(theLoaiDto.getTenTheLoai());
        if (theLoaiExit != null) {
            throw new ThongBao("Thể loại đã tồn tại");
        }
        TheLoai theLoaiSave = new TheLoai();
        theLoaiSave.setTenTheLoai(theLoaiDto.getTenTheLoai());
        theLoaiRepository.save(theLoaiSave);

        return ResponseEntity.ok(theLoaiSave);
    }

    @PutMapping("/update-the-loai/{id}")
    public ResponseEntity<TheLoai> updateTheLoai(@PathVariable int id, @RequestBody TheLoaiDto theLoaiDto) throws ThongBao {
        Optional<TheLoai> theLoaiExit = theLoaiRepository.findById(id);
        if (!theLoaiExit.isPresent()) {
            throw new ThongBao("Thể loại không tồn tại");
        }

        // Lấy thể loại đã tồn tại từ cơ sở dữ liệu
        TheLoai theLoai = theLoaiExit.get();
        // Cập nhật tên thể loại
        theLoai.setTenTheLoai(theLoaiDto.getTenTheLoai());

        // Lưu lại đối tượng đã được cập nhật
        theLoaiRepository.save(theLoai);

        return ResponseEntity.ok(theLoai);
    }

    @PutMapping("/update-sach/{id}")
    public ResponseEntity<Sach> updateSach(@PathVariable int id, @RequestBody SachDto sachDto) throws ThongBao {
        // Tìm sách theo ID
        Sach sach = sachRepository.findById(id).orElseThrow(() -> new ThongBao("Không tìm thấy sách với ID: " + id));

        // Cập nhật thông tin sách từ SachDto
        sach.setTenSach(sachDto.getTenSach());
        sach.setTenTacGia(sachDto.getTenTacGia());
        sach.setISBN(sachDto.getIsbn());
        sach.setMoTa(sachDto.getMoTa());
        sach.setGiaNiemYet(sachDto.getGiaNiemYet());
        sach.setGiaBan(sachDto.getGiaBan());
        sach.setSoLuong(sachDto.getSoLuong());


        // Xóa tất cả hình ảnh liên quan đến sách
        hinhAnhRepository.deleteAllBySach(sach);

        // Cập nhật danh sách hình ảnh mới
        List<HinhAnh> hinhAnhList = sachDto.getHinhAnhs().stream()
                .map(hinhAnhDto -> {
                    HinhAnh hinhAnh = new HinhAnh();
                    hinhAnh.setDuLieuAnh(hinhAnhDto.getDuLieuAnh());
                    hinhAnh.setSach(sach);
                    return hinhAnh;
                }).collect(Collectors.toList());
        sach.setDanhSachHinhAnh(hinhAnhList);



        // Xử lý danh sách thể loại
        List<TheLoai> theLoais = new ArrayList<>();
        for (String tenTheLoai : sachDto.getTheLoaiSach()) {
            TheLoai theLoai = theLoaiRepository.findByTenTheLoai(tenTheLoai);
            if (theLoai != null) {
                theLoais.add(theLoai);
            } else {
                throw new ThongBao("không tìm thấy thể loại");
            }
        }
        sach.setDanhSachTheLoai(theLoais);

        // Lưu sách sau khi cập nhật
        Sach updatedSach = sachRepository.save(sach);

        return ResponseEntity.ok(updatedSach);
    }
















}
