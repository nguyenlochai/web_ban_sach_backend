//package lochai.web_ban_sach.service;
//
//import lochai.web_ban_sach.dao.SachRepository;
//import lochai.web_ban_sach.entity.Sach;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@Autowired
//private SachRepository sachRepository;
//
//public class SachServiceImpl implements SachService {
//    @Override
//    public Sach themSach(Sach sach) {
//        if (sach == null){
//            throw new RuntimeException("Không thêm được");
//        }else {
//            Sach sach1 = new Sach();
//            sach1 = sachRepository.save(sach);
//
//        }
//    }
//}
