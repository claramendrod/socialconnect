package br.com.socialconnect.api.doacoes.service;

import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.Doacao;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.repository.DoacaoRepository;
import br.com.socialconnect.api.doadores.model.Doador;
import br.com.socialconnect.api.doadores.model.TipoDoador;
import br.com.socialconnect.api.doadores.service.DoadorService;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;

class DoacaoServiceTest {

    private final DoacaoRepository doacaoRepository = Mockito.mock(DoacaoRepository.class);
    private final DoadorService doadorService = Mockito.mock(DoadorService.class);
    private final DoacaoService doacaoService = new DoacaoService(doacaoRepository, doadorService);

    @Test
    @DisplayName("Deve criar doação quando dados válidos")
    void deveCriarDoacaoQuandoDadosValidos() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        DoacaoRequestDTO dto = new DoacaoRequestDTO(
                1L,
                LocalDate.now(),
                new BigDecimal("100.00"),
                TipoDoacao.ALIMENTO,
                "Doação de teste"
        );
        Doador doador = Doador.builder()
                .idDoador(1L)
                .nome("Instituto Solidário")
                .tipo(TipoDoador.PESSOA_JURIDICA)
                .build();
        Doacao doacaoSalva = Doacao.builder()
                .idDoacao(1L)
                .doador(doador)
                .dataDoacao(dto.dataDoacao())
                .valor(dto.valor())
                .tipo(dto.tipo())
                .descricao(dto.descricao())
                .build();

        Mockito.when(doadorService.buscarEntidadePorId(1L)).thenReturn(doador);
        Mockito.when(doacaoRepository.save(Mockito.any(Doacao.class))).thenReturn(doacaoSalva);

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        DoacaoResponseDTO resultado = doacaoService.criar(dto);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        Assertions.assertNotNull(resultado.idDoacao(), "ID da doação não deve ser nulo");
        Assertions.assertEquals(new BigDecimal("100.00"), resultado.valor());
        Assertions.assertEquals("Instituto Solidário", resultado.nomeDoador());
        Mockito.verify(doacaoRepository, Mockito.times(1)).save(Mockito.any(Doacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando doador não existir")
    void deveLancarExcecaoQuandoDoadorNaoExistir() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        DoacaoRequestDTO dto = new DoacaoRequestDTO(
                999L,
                LocalDate.now(),
                new BigDecimal("100.00"),
                TipoDoacao.ALIMENTO,
                "Doação de teste"
        );
        Mockito.when(doadorService.buscarEntidadePorId(999L))
                .thenThrow(new RecursoNaoEncontradoException("Doador não encontrado com o ID: 999"));

        // ==========================================
        // ACT + ASSERT: Executar e verificar exceção
        // ==========================================
        Assertions.assertThrows(RuntimeException.class, () -> doacaoService.criar(dto));
        Mockito.verify(doacaoRepository, Mockito.never()).save(Mockito.any(Doacao.class));
    }
}
