package si.feri.stroski;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Kategorija {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Naziv je obvezen")
    private String naziv;

    public Kategorija() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }
}