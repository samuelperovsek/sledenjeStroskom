package si.feri.stroski.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import si.feri.stroski.Kategorija;
import si.feri.stroski.repository.KategorijaRepository;

import java.util.List;

@Tag(name = "Kategorije", description = "Upravljanje kategorij stroškov")
@RestController
@RequestMapping("/api/kategorije")
@CrossOrigin(origins = "*")
public class KategorijaController {

    private final KategorijaRepository repository;

    public KategorijaController(KategorijaRepository repository) {
        this.repository = repository;
    }
    @Operation(summary = "Vrne vse kategorije")
    @GetMapping
    public List<Kategorija> getAll() {
        return repository.findAll();
    }
    @Operation(summary = "Vrne eno kategorijo")
    @GetMapping("/{id}")
    public Kategorija getById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategorija ne obstaja"));
    }
    @Operation(summary = "Ustvari novo kategorijo")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Kategorija create(@Valid @RequestBody Kategorija k) {
        k.setId(null);
        return repository.save(k);
    }
    @Operation(summary = "Posodobi kategorijo")
    @PutMapping("/{id}")
    public Kategorija update(@PathVariable Long id, @Valid @RequestBody Kategorija k) {
        Kategorija existing = getById(id);
        existing.setNaziv(k.getNaziv());
        return repository.save(existing);
    }

    @Operation(summary = "Izbriše kategorijo")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        getById(id);
        repository.deleteById(id);
    }
}
