package si.feri.stroski;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Strosek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Naziv je obvezen")
    private String title;

    @NotNull(message = "Znesek je obvezen")
    @DecimalMin(value = "0.01", message = "Znesek mora biti večji od 0")
    private BigDecimal amount;

    @NotNull(message = "Kategorija je obvezna")
    @ManyToOne
    @JoinColumn(name = "kategorija_id")
    private Kategorija kategorija;

    @NotNull(message = "Datum je obvezen")
    private LocalDate date;

    private String note;

    public Strosek() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Kategorija getKategorija() { return kategorija; }
    public void setKategorija(Kategorija kategorija) { this.kategorija = kategorija; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
