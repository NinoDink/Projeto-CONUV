package senai.sp.bauru.projeto.conuv.controllers;

import senai.sp.bauru.projeto.conuv.models.Produto;
import senai.sp.bauru.projeto.conuv.repositories.ProdutoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoRepository repository;

    public ProdutoController(ProdutoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Produto> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Produto criar(@RequestBody Produto produto) {
        produto.setId(null); // garante INSERT
        return repository.save(produto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto dados) {
        return repository.findById(id).map(p -> {
            p.setNome(dados.getNome());
            p.setDescricao(dados.getDescricao());
            p.setValor(dados.getValor());
            return ResponseEntity.ok(repository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // produto já usado em alguma venda (chave estrangeira)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }
}
