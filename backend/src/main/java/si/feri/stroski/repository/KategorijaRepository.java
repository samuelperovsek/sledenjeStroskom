package si.feri.stroski.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import si.feri.stroski.Kategorija;

public interface KategorijaRepository extends JpaRepository<Kategorija, Long> {
}