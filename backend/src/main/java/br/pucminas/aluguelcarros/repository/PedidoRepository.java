package br.pucminas.aluguelcarros.repository;

import br.pucminas.aluguelcarros.model.Pedido;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findAllByOrderByCriadoEmDesc();

    List<Pedido> findByClienteIdOrderByCriadoEmDesc(Long clienteId);

    boolean existsByClienteId(Long clienteId);

    boolean existsByAutomovelId(Long automovelId);

    @Query("""
            select count(p) > 0 from Pedido p
            where p.automovel.id = :automovelId
              and p.status = br.pucminas.aluguelcarros.model.StatusPedido.CONTRATADO
              and p.dataInicio <= :dataFim
              and p.dataFim >= :dataInicio
              and p.id <> :pedidoIgnorado
            """)
    boolean existeContratoNoPeriodo(
            @Param("automovelId") Long automovelId,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("pedidoIgnorado") Long pedidoIgnorado);
}
