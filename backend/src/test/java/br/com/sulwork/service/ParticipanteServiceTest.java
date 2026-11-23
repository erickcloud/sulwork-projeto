
package br.com.sulwork.service;

import br.com.sulwork.domain.Participante;
import br.com.sulwork.repository.ParticipanteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipanteServiceTest {

    @Mock
    private ParticipanteRepository repository;

    @InjectMocks
    private ParticipanteService service;

    @Test
    void deveCriarParticipanteQuandoDadosForemValidos() {
        String nome = "Erick";
        String cpf = "52998224725";
        String restricoes = "Nenhuma";

        Participante participanteSalvo =
                new Participante(nome, cpf, restricoes);

        when(repository.existsByCpf(cpf)).thenReturn(false);
        when(repository.save(any(Participante.class)))
                .thenReturn(participanteSalvo);

        Participante resultado =
                service.criar(nome, cpf, restricoes);

        assertNotNull(resultado);
        assertEquals(nome, resultado.getNome());
        assertEquals(cpf, resultado.getCpf());

        verify(repository).existsByCpf(cpf);
        verify(repository).save(any(Participante.class));
    }


    @Test
    void deveLancarExcecaoQuandoNomeForVazio() {
        String cpf = "52998224725";

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> service.criar(" ", cpf, "Nenhuma")
        );

        assertEquals("Nome é obrigatório.", excecao.getMessage());

        verifyNoInteractions(repository);
    }


    @Test
    void deveLancarExcecaoQuandoCpfForInvalido() {
        String nome = "Erick";
        String cpf = "12345678900";

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> service.criar(nome, cpf, "Nenhuma")
        );

        assertEquals("CPF inválido.", excecao.getMessage());

        verifyNoInteractions(repository);
    }


    @Test
    void deveLancarExcecaoQuandoCpfJaEstiverCadastrado() {
        String nome = "Erick";
        String cpf = "52998224725";

        when(repository.existsByCpf(cpf)).thenReturn(true);

        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,
                () -> service.criar(nome, cpf, "Nenhuma")
        );

        assertEquals("CPF já cadastrado.", excecao.getMessage());

        verify(repository).existsByCpf(cpf);
        verify(repository, never()).save(any(Participante.class));
    }

    @Test
    void deveListarTodosOsParticipantes() {
        Participante participante1 =
                new Participante("Erick", "52998224725", "Nenhuma");

        Participante participante2 =
                new Participante("João", "11144477735", "Nenhuma");

        when(repository.findAll())
                .thenReturn(java.util.List.of(participante1, participante2));

        var resultado = service.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Erick", resultado.get(0).getNome());
        assertEquals("João", resultado.get(1).getNome());

        verify(repository).findAll();
    }


}
