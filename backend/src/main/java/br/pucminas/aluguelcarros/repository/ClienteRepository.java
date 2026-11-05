package br.pucminas.aluguelcarros.repository;

import br.pucminas.aluguelcarros.model.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findAllByOrderByNomeAsc();

    boolean existsByCpf(String cpf);

    boolean existsByRg(String rg);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByRgAndIdNot(String rg, Long id);
}
