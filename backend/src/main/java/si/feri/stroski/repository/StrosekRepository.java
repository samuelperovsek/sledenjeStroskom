package si.feri.stroski.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import si.feri.stroski.Strosek;
import si.feri.stroski.model.StatistikaKategorija;

import java.time.LocalDate;
import java.util.List;

public interface StrosekRepository extends JpaRepository<Strosek, Long> {
    @Query("""
        SELECT s FROM Strosek s
        WHERE (LOWER(s.title) LIKE :vzorec OR LOWER(COALESCE(s.note, '')) LIKE :vzorec)
          AND (:kategorijaId = 0 OR s.kategorija.id = :kategorijaId)
          AND s.date >= :zacetek AND s.date <= :konec
        ORDER BY s.date DESC
        """)
    List<Strosek> isci(@Param("vzorec") String vzorec,
                       @Param("kategorijaId") Long kategorijaId,
                       @Param("zacetek") LocalDate zacetek,
                       @Param("konec") LocalDate konec);

    @Query("""
        SELECT new si.feri.stroski.model.StatistikaKategorija(k.naziv, SUM(s.amount), COUNT(s))
        FROM Strosek s JOIN s.kategorija k
        GROUP BY k.naziv
        ORDER BY SUM(s.amount) DESC
        """)
    List<StatistikaKategorija> statistikaPoKategorijah();
}
