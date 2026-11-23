package senai.sp.bauru.projeto.conuv.repositories;

import senai.sp.bauru.projeto.conuv.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {}
