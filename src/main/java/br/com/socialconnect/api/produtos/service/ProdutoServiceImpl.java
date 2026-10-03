package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import br.com.socialconnect.api.produtos.dto.*;
import br.com.socialconnect.api.produtos.exception.*;
import br.com.socialconnect.api.produtos.model.*;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class ProdutoServiceImpl implements ProdutoService {
    private final ProdutoRepository repository;

    public ProdutoServiceImpl(ProdutoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<ProdutoResponseDTO> listar(String nome, CategoriaProduto categoria, Pageable pageable) {
        Specification<Produto> filtro = (root, query, cb) -> cb.conjunction();
        if (nome != null && !nome.isBlank()) {
            String termo = nome.toLowerCase(java.util.Locale.ROOT)
                    .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
            filtro = filtro.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nome")), "%" + termo + "%", '\\'));
        }
        if (categoria != null) {
            filtro = filtro.and((root, query, cb) -> cb.equal(root.get("categoria"), categoria));
        }
        return repository.findAll(filtro, pageable).map(this::toResponseDTO);
    }

    @Override
    public ProdutoResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarEntidade(id));
    }

    @Override
    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        validarEstoque(dto);
        if (repository.existsByNome(dto.nome())) {
            throw new NomeProdutoDuplicadoException();
        }
        Produto produto = new Produto();
        preencher(produto, dto);
        produto.setDataCadastro(LocalDate.now());
        return salvar(produto);
    }

    @Override
    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = buscarEntidade(id);
        validarEstoque(dto);
        if (repository.existsByNomeAndIdProdutoNot(dto.nome(), id)) {
            throw new NomeProdutoDuplicadoException();
        }
        preencher(produto, dto);
        return salvar(produto);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Produto buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new RecursoNaoEncontradoException("Produto não encontrado com o ID: " + id));
    }

    private void validarEstoque(ProdutoRequestDTO dto) {
        if (dto.estoqueAtual() != null && dto.estoqueAtual() < 0) {
            throw new EstoqueNegativoException();
        }
    }

    private void preencher(Produto produto, ProdutoRequestDTO dto) {
        produto.setNome(dto.nome());
        produto.setCategoria(dto.categoria());
        produto.setEstoqueAtual(dto.estoqueAtual());
        produto.setEstoqueMinimo(dto.estoqueMinimo());
        produto.setUnidadeMedida(dto.unidadeMedida());
    }

    private ProdutoResponseDTO salvar(Produto produto) {
        try {
            return toResponseDTO(repository.saveAndFlush(produto));
        } catch (DataIntegrityViolationException ex) {
            if (ex.getMostSpecificCause().getMessage() != null
                    && ex.getMostSpecificCause().getMessage().contains("uk_produtos_nome")) {
                throw new NomeProdutoDuplicadoException();
            }
            throw ex;
        }
    }

    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(produto.getIdProduto(), produto.getNome(), produto.getCategoria(),
                produto.getEstoqueAtual(), produto.getEstoqueMinimo(), produto.getUnidadeMedida(),
                produto.getDataCadastro(), produto.getEstoqueAtual() < produto.getEstoqueMinimo());
    }
}
