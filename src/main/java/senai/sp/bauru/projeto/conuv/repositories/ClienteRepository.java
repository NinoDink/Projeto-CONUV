package senai.sp.bauru.projeto.conuv.repositories;

import senai.sp.bauru.projeto.conuv.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {}
