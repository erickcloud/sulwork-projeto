
import { useState } from 'react';

type Participante = {
    id: number;
    nome: string;
    cpf: string;
    restricoes: string;
};

const API = 'http://localhost:8080/api';

export default function Participantes() {
    const [participantes, setParticipantes] = useState<Participante[]>([]);
    const [nome, setNome] = useState('');
    const [cpf, setCpf] = useState('');
    const [restricoes, setRestricoes] = useState('');
    const [mensagem, setMensagem] = useState('');
    const [carregado, setCarregado] = useState(false);

    async function carregar() {
        try {
            const resposta = await fetch(`${API}/participantes`);

            if (!resposta.ok) {
                setMensagem('Não foi possível carregar os participantes.');
                return;
            }

            setParticipantes(await resposta.json());
            setCarregado(true);
            setMensagem('');
        } catch {
            setMensagem('Erro de conexão com a API.');
        }
    }

    async function cadastrar(e: React.FormEvent) {
        e.preventDefault();

        try {
            const resposta = await fetch(`${API}/participantes`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    nome,
                    cpf,
                    restricoes
                })
            });

            if (!resposta.ok) {
                const erro = await resposta.text();
                setMensagem(erro || 'Não foi possível cadastrar o participante. Verifique o CPF.');
                return;
            }

            setNome('');
            setCpf('');
            setRestricoes('');
            setMensagem('Participante cadastrado com sucesso.');

            await carregar();
        } catch {
            setMensagem('Erro de conexão com a API.');
        }
    }

    return (
        <section className="panel">
            <div className="section-title">
                <h2>Participantes</h2>
                <button type="button" className="secondary" onClick={carregar}>
                    Atualizar lista
                </button>
            </div>

            <form onSubmit={cadastrar}>
                <input
                    aria-label="Nome do participante"
                    placeholder="Nome completo"
                    value={nome}
                    onChange={e => setNome(e.target.value)}
                    required
                />

                <input
                    aria-label="CPF"
                    placeholder="CPF (11 dígitos)"
                    value={cpf}
                    onChange={e => setCpf(e.target.value)}
                    maxLength={14}
                    required
                />

                <input
                    aria-label="Restrições alimentares"
                    placeholder="Restrições alimentares (opcional)"
                    value={restricoes}
                    onChange={e => setRestricoes(e.target.value)}
                />

                <button type="submit">Cadastrar participante</button>
            </form>

            {mensagem && <p role="status">{mensagem}</p>}

            <h3>Participantes cadastrados</h3>

            {!carregado && (
                <p className="muted">
                    Clique em Atualizar lista para consultar os participantes.
                </p>
            )}

            {carregado && participantes.length === 0 && (
                <p className="muted">Nenhum participante cadastrado.</p>
            )}

            {participantes.map(participante => (
                <div className="event" key={participante.id}>
                    <div>
                        <strong>{participante.nome}</strong>
                        <small>CPF: {participante.cpf}</small>
                        <small>
                            Restrições: {participante.restricoes || 'Nenhuma informada'}
                        </small>
                    </div>
                </div>
            ))}
        </section>
    );
}
