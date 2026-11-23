
import Itens from './Itens';
import Categorias from './Categorias';
import { useEffect, useState } from 'react';
import Participantes from './Participantes';
import Contribuicoes from './Contribuicoes';

type Evento = {
    id: number;
    nome: string;
    descricao: string;
    tipo: string;
    dataHora: string;
    prazoAlteracoes: string;
    status: string;
};

type Cobertura = {
    coberturaGeralPercentual: number;
    totalItens: number;
    obrigatoriosConcluidos: number;
    obrigatoriosPendentes: number;
};

const API = 'http://localhost:8080/api';

function proximosStatus(status: string): string[] {
    switch (status) {
        case 'RASCUNHO':
            return ['ABERTO', 'CANCELADO'];
        case 'ABERTO':
            return ['ENCERRADO', 'CANCELADO'];
        case 'ENCERRADO':
            return ['REALIZADO', 'CANCELADO'];
        default:
            return [];
    }
}

export default function App() {
    const [eventos, setEventos] = useState<Evento[]>([]);
    const [coberturas, setCoberturas] = useState<Record<number, Cobertura>>({});
    const [nome, setNome] = useState('');
    const [tipo, setTipo] = useState('ALMOCO');
    const [mensagem, setMensagem] = useState('');

    async function carregar() {
        try {
            const r = await fetch(`${API}/eventos`);
            if (r.ok) {
                setEventos(await r.json());
            } else {
                setMensagem('Não foi possível carregar os eventos.');
            }
        } catch {
            setMensagem('Não foi possível conectar à API. Verifique se o Docker Compose está ativo.');
        }
    }

    useEffect(() => {
        carregar();
    }, []);

    useEffect(() => {
        async function carregarCoberturas() {
            const resultados = await Promise.all(
                eventos.map(async ev => {
                    try {
                        const r = await fetch(`${API}/eventos/${ev.id}/cobertura`);
                        if (!r.ok) return null;

                        const cobertura: Cobertura = await r.json();
                        return [ev.id, cobertura] as const;
                    } catch {
                        return null;
                    }
                })
            );

            const mapa: Record<number, Cobertura> = {};

            resultados.forEach(resultado => {
                if (resultado) {
                    mapa[resultado[0]] = resultado[1];
                }
            });

            setCoberturas(mapa);
        }

        if (eventos.length > 0) {
            carregarCoberturas();
        } else {
            setCoberturas({});
        }
    }, [eventos]);

    async function criar(e: React.FormEvent) {
        e.preventDefault();

        try {
            const agora = new Date();
            const data = new Date(agora.getTime() + 48 * 3600000);
            const prazo = new Date(agora.getTime() + 24 * 3600000);

            const r = await fetch(`${API}/eventos`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    nome,
                    descricao: 'Evento criado pelo painel',
                    tipo,
                    dataHora: data.toISOString(),
                    prazoAlteracoes: prazo.toISOString()
                })
            });

            if (r.ok) {
                setNome('');
                setMensagem('Evento criado com sucesso.');
                await carregar();
            } else {
                setMensagem('Não foi possível criar o evento. Confira a API.');
            }
        } catch {
            setMensagem('Não foi possível conectar à API. Verifique se o Docker Compose está ativo.');
        }
    }

    async function alterarStatus(ev: Evento, novoStatus: string) {
        try {
            const r = await fetch(`${API}/eventos/${ev.id}/status`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status: novoStatus })
            });

            if (r.ok) {
                setMensagem(`Status do evento "${ev.nome}" alterado para ${novoStatus}.`);
                await carregar();
            } else {
                const erro = await r.json().catch(() => null);
                setMensagem(erro?.erro || erro?.message || 'Não foi possível alterar o status.');
            }
        } catch {
            setMensagem('Não foi possível conectar à API.');
        }
    }

    async function excluirEvento(ev: Evento) {
        const confirmado = window.confirm(
            `Tem certeza que deseja excluir o evento "${ev.nome}"?\n\nAs categorias, itens, contribuições e inscrições desse evento também serão excluídos. Os participantes serão preservados.`
        );

        if (!confirmado) return;

        try {
            const r = await fetch(`${API}/eventos/${ev.id}`, {
                method: 'DELETE'
            });

            if (r.ok) {
                setMensagem(`Evento "${ev.nome}" excluído com sucesso.`);
                await carregar();
            } else {
                const erro = await r.json().catch(() => null);
                setMensagem(erro?.erro || erro?.message || 'Não foi possível excluir o evento.');
            }
        } catch {
            setMensagem('Não foi possível conectar à API.');
        }
    }

    return (
        <main>
            <header>
                <div>
                    <span className="eyebrow">SULWORK · ORGANIZAÇÃO</span>
                    <h1>Eventos de alimentação</h1>
                    <p>Organize participantes, itens e contribuições em um só lugar.</p>
                </div>
                <span className="mark">S</span>
            </header>

            <section className="stats">
                <article>
                    <small>EVENTOS CADASTRADOS</small>
                    <strong>{eventos.length}</strong>
                </article>
                <article>
                    <small>API</small>
                    <strong className="online">REST</strong>
                </article>
                <article>
                    <small>STATUS DO PROJETO</small>
                    <strong>MVP</strong>
                </article>
            </section>

            <section className="panel">
                <h2>Novo evento</h2>
                <form onSubmit={criar}>
                    <input
                        aria-label="Nome do evento"
                        placeholder="Nome do evento"
                        value={nome}
                        onChange={e => setNome(e.target.value)}
                        required
                    />
                    <select
                        aria-label="Tipo do evento"
                        value={tipo}
                        onChange={e => setTipo(e.target.value)}
                    >
                        <option value="ALMOCO">Almoço</option>
                        <option value="CAFE">Café</option>
                        <option value="LANCHE">Lanche</option>
                        <option value="JANTAR">Jantar</option>
                        <option value="OUTRO">Outro</option>
                    </select>
                    <button type="submit">Criar evento</button>
                </form>
                {mensagem && <p role="status">{mensagem}</p>}
            </section>

            <section className="panel">
                <div className="section-title">
                    <h2>Eventos</h2>
                    <button className="secondary" onClick={carregar}>
                        Atualizar
                    </button>
                </div>

                {eventos.length === 0 ? (
                    <p className="muted">Nenhum evento cadastrado ainda.</p>
                ) : (
                    eventos.map(ev => (
                        <div className="event" key={ev.id}>
                            <div>
                                <strong>{ev.nome}</strong>
                                <small>
                                    {ev.tipo} · {new Date(ev.dataHora).toLocaleString('pt-BR')}
                                </small>
                                {coberturas[ev.id] && (
                                    <small>
                                        Cobertura: {coberturas[ev.id].coberturaGeralPercentual}%
                                        {' · '}Itens: {coberturas[ev.id].totalItens}
                                        {' · '}Obrigatórios concluídos: {coberturas[ev.id].obrigatoriosConcluidos}
                                        {' · '}Pendentes: {coberturas[ev.id].obrigatoriosPendentes}
                                    </small>
                                )}
                            </div>

                            <span className="pill">{ev.status}</span>

                            <div className="event-actions">
                                {proximosStatus(ev.status).length > 0 && (
                                    <select
                                        aria-label={`Alterar status do evento ${ev.nome}`}
                                        value=""
                                        onChange={e => {
                                            if (e.target.value) {
                                                alterarStatus(ev, e.target.value);
                                            }
                                        }}
                                    >
                                        <option value="" disabled>
                                            Alterar status
                                        </option>
                                        {proximosStatus(ev.status).map(status => (
                                            <option key={status} value={status}>
                                                {status}
                                            </option>
                                        ))}
                                    </select>
                                )}

                                <button
                                    type="button"
                                    className="danger"
                                    onClick={() => excluirEvento(ev)}
                                >
                                    Excluir
                                </button>
                            </div>
                        </div>
                    ))
                )}
            </section>

            <Participantes />
            <Categorias eventos={eventos} />
            <Itens eventos={eventos} />
            <Contribuicoes eventos={eventos} />

            <footer>Sulwork · API de gestão de eventos</footer>
        </main>
    );
}
