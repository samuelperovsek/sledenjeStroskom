package si.feri.stroski.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import si.feri.stroski.Strosek;
import si.feri.stroski.exception.StrosekNotFoundException;
import si.feri.stroski.model.StatistikaKategorija;
import si.feri.stroski.repository.StrosekRepository;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stroski")
@CrossOrigin(origins = "*")
public class StrosekController {

    private final StrosekRepository repository;

    public StrosekController(StrosekRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Vrne vse zapise stroškov")
    @GetMapping
    public List<Strosek> getAll() {
        return repository.findAll();
    }

    @Operation(summary = "Vrne en strošek")
    @GetMapping("/{id}")
    public Strosek getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new StrosekNotFoundException(id));
    }

    @Operation(summary = "Ustvari nov strošek")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Strosek create(@Valid @RequestBody Strosek expense) {
        expense.setId(null);
        return repository.save(expense);
    }

    @Operation(summary = "Posodobi strošek")
    @PutMapping("/{id}")
    public Strosek update(@PathVariable Long id, @Valid @RequestBody Strosek updated) {
        Strosek existing = repository.findById(id).orElseThrow(() -> new StrosekNotFoundException(id));
        existing.setTitle(updated.getTitle());
        existing.setAmount(updated.getAmount());
        existing.setKategorija(updated.getKategorija());
        existing.setDate(updated.getDate());
        existing.setNote(updated.getNote());
        return repository.save(existing);
    }

    @Operation(summary = "Izbriše strošek")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new StrosekNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Operation(summary = "Išče in filtrira stroške")
    @GetMapping("/iskanje")
    public List<Strosek> isci(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") Long kategorijaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datumOd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datumDo) {

        String vzorec = "%" + q.toLowerCase() + "%";
        LocalDate zacetek = datumOd != null ? datumOd : LocalDate.of(1900, 1, 1);
        LocalDate konec = datumDo != null ? datumDo : LocalDate.of(9999, 12, 31);
        return repository.isci(vzorec, kategorijaId, zacetek, konec);
    }

    @Operation(summary = "Statistika porabe po kategorijah",
            description = "Za vsako kategorijo vrne skupni znesek in število stroškov.")
    @GetMapping("/statistika")
    public List<StatistikaKategorija> statistika() {
        return repository.statistikaPoKategorijah();
    }
}
