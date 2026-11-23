package senai.sp.bauru.projeto.conuv.controllers;

import senai.sp.bauru.projeto.conuv.models.Cliente;
import senai.sp.bauru.projeto.conuv.models.Produto;
import senai.sp.bauru.projeto.conuv.models.Venda;
import senai.sp.bauru.projeto.conuv.repositories.ClienteRepository;
import senai.sp.bauru.projeto.conuv.repositories.ProdutoRepository;
import senai.sp.bauru.projeto.conuv.repositories.VendaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public VendaController(VendaRepository vendaRepository,
                           ClienteRepository clienteRepository,
                           ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    public record VendaRequest(
            LocalDate data,
            String loja,
            Long idCliente,
            Long idProduto,
            Integer quantidade
    ) {}

    @GetMapping
    public List<Venda> listar() {
        return vendaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Venda> criar(@RequestBody VendaRequest req) {
        return montarVenda(new Venda(), req)
                .map(vendaRepository::save)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venda> atualizar(@PathVariable Long id, @RequestBody VendaRequest req) {
        Optional<Venda> existente = vendaRepository.findById(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return montarVenda(existente.get(), req)
                .map(vendaRepository::save)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!vendaRepository.existsById(id)) return ResponseEntity.notFound().build();
        vendaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** Só aceita cliente e produto já cadastrados; o valor da venda é calculado aqui. */
    private Optional<Venda> montarVenda(Venda venda, VendaRequest req) {
        if (req.data() == null
                || req.loja() == null || req.loja().isBlank()
                || req.idCliente() == null || req.idProduto() == null
                || req.quantidade() == null || req.quantidade() < 1) {
            return Optional.empty();
        }
        Cliente cliente = clienteRepository.findById(req.idCliente()).orElse(null);
        Produto produto = produtoRepository.findById(req.idProduto()).orElse(null);
        if (cliente == null || produto == null) {
            return Optional.empty();
        }

        BigDecimal total = produto.getValor().multiply(BigDecimal.valueOf(req.quantidade()));
        venda.setData(req.data());
        venda.setLoja(req.loja().trim());
        venda.setCliente(cliente);
        venda.setProduto(produto);
        venda.setQuantidade(req.quantidade());
        venda.setValorVenda(total);
        return Optional.of(venda);
    }
}
