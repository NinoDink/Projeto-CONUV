package senai.sp.bauru.projeto.conuv.controllers;

import senai.sp.bauru.projeto.conuv.models.Cliente;
import senai.sp.bauru.projeto.conuv.repositories.ClienteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository repository;

    public ClienteController(ClienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Cliente> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Cliente criar(@RequestBody Cliente cliente) {
        cliente.setId(null); // garante INSERT
        return repository.save(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody Cliente dados) {
        return repository.findById(id).map(c -> {
            c.setNome(dados.getNome());
            c.setCpf(dados.getCpf());
            c.setIdade(dados.getIdade());
            c.setProfissao(dados.getProfissao());
            c.setEndereco(dados.getEndereco());
            return ResponseEntity.ok(repository.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // cliente já usado em alguma venda (chave estrangeira)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }
}
