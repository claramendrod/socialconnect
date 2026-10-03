package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.produtos.dto.*;
import br.com.socialconnect.api.produtos.exception.*;
import br.com.socialconnect.api.produtos.model.*;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {
    @Mock ProdutoRepository repository;
    @InjectMocks ProdutoServiceImpl service;

    @Test
    void deveCriarProdutoQuandoDadosValidos() {
        // Arrange
        ProdutoRequestDTO dto = new ProdutoRequestDTO("Arroz", CategoriaProduto.ALIMENTO, 3, 10, "kg");
        when(repository.saveAndFlush(any())).thenAnswer(invocacao -> {
            Produto produto = invocacao.getArgument(0);
            produto.setIdProduto(1L);
            return produto;
        });
        // Act
        ProdutoResponseDTO resposta = service.criar(dto);
        // Assert
        assertEquals(1L, resposta.idProduto());
        assertTrue(resposta.estoqueBaixo());
        assertNotNull(resposta.dataCadastro());
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueNegativo() {
        // Arrange
        ProdutoRequestDTO dto = new ProdutoRequestDTO("Arroz", CategoriaProduto.ALIMENTO, -1, 10, "kg");
        // Act
        EstoqueNegativoException erro = assertThrows(EstoqueNegativoException.class, () -> service.criar(dto));
        // Assert
        assertNotNull(erro);
        verifyNoInteractions(repository);
    }

    @Test
    void deveLancarExcecaoQuandoNomeDuplicado() {
        // Arrange
        ProdutoRequestDTO dto = new ProdutoRequestDTO("Arroz", CategoriaProduto.ALIMENTO, 3, 10, "kg");
        when(repository.existsByNome("Arroz")).thenReturn(true);
        // Act
        NomeProdutoDuplicadoException erro = assertThrows(NomeProdutoDuplicadoException.class, () -> service.criar(dto));
        // Assert
        assertNotNull(erro);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void devePreservarDataCadastroAoAtualizarECalcularLimiteDoEstoque() {
        // Arrange
        java.time.LocalDate data = java.time.LocalDate.of(2026, 9, 18);
        Produto produto = new Produto(1L, "Arroz", CategoriaProduto.ALIMENTO, 3, 10, "kg", data);
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(produto));
        when(repository.saveAndFlush(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
        // Act
        ProdutoResponseDTO resposta = service.atualizar(1L,
                new ProdutoRequestDTO("Arroz", CategoriaProduto.ALIMENTO, 10, 10, "kg"));
        // Assert
        assertEquals(data, resposta.dataCadastro());
        assertFalse(resposta.estoqueBaixo());
    }
}
