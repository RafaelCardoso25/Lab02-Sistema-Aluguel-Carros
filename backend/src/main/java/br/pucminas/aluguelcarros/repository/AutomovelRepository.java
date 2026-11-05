package br.pucminas.aluguelcarros.repository;

import br.pucminas.aluguelcarros.model.Automovel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutomovelRepository extends JpaRepository<Automovel, Long> {

    List<Automovel> findAllByOrderByMarcaAscModeloAsc();

    boolean existsByPlaca(String placa);

    boolean existsByMatricula(String matricula);

    boolean existsByPlacaAndIdNot(String placa, Long id);

    boolean existsByMatriculaAndIdNot(String matricula, Long id);
}
