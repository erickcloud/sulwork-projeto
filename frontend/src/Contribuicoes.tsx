
import { useEffect, useState } from 'react';

type Evento = {
    id: number;
    nome: string;
    status: string;
    prazoAlteracoes: string;
};

type Participante = {
    id: number;
    nome: string;
    restricoes: string;
};

type Categoria = {
    id: number;
    nome: string;
};

type Item = {
    id: number;
    nome: string;
    quantidadeNecessaria: number;
    obrigatorio: boolean;
    alergenos: string;
};

type Contribuicao = {
    id: number;
    participanteId: number;
    participanteNome: string;
    itemId: number;
    itemNome: string;
    quantidade: number;
};

type Props = {
    eventos: Evento[];
};

const API = 'http://localhost:8080/api';

export default function Contribuicoes({ eventos }: Props) {
    const [eventoId, setEventoId] = useState('');
    const [participantes, setParticipantes] = useState<Participante[]>([]);
    const [categorias, setCategorias] = useState<Categoria[]>([]);
    const [itens, setItens] = useState<Item[]>([]);
    const [contribuicoes, setContribuicoes] = useState<Contribuicao[]>([]);
    const [participanteId, setParticipanteId] = useState('');
    const [categoriaId, setCategoriaId] = useState('');
    const [itemId, setItemId] = useState('');
    const [quantidade, setQuantidade] = useState('1');
    const [mensagem, setMensagem] = useState('');

    const evento = eventos.find(e => String(e.id) === eventoId);

    const prazoValido = evento
        ? new Date(evento.prazoAlteracoes).getTime() > Date.now()
        : false;

    const podeContribuir = evento?.status === 'ABERTO' && prazoValido;

    useEffect(() => {
        async function carregarContribuicoes() {
            try {
                const resposta = await fetch(`${API}/contribuicoes`);

                if (!resposta.ok) {
                    setMensagem('Não foi possível carregar as contribuições.');
                    return;
                }

                const dados: Contribuicao[] = await resposta.json();
                setContribuicoes(dados);
            } catch {
                setMensagem('Erro ao carregar contribuições.');
            }
        }

        carregarContribuicoes();
    }, []);

    useEffect(() => {
        if (eventos.length > 0 && !eventoId) {
            setEventoId(String(eventos[0].id));
        }
    }, [eventos, eventoId]);

    useEffect(() => {
        if (eventos.length === 0) return;

        async function carregarParticipantes() {
            try {
                const resposta = await fetch(`${API}/participantes`);

                if (resposta.ok) {
                    const dados: Participante[] = await resposta.json();
                    setParticipantes(dados);

                    if (dados.length > 0) {
                        setParticipanteId(String(dados[0].id));
                    }
                }
            } catch {
                setMensagem('Erro ao carregar participantes.');
            }
        }

        carregarParticipantes();
    }, [eventos.length]);

    useEffect(() => {
        async function carregarCategorias() {
            if (!eventoId) {
                setCategorias([]);
                setItens([]);
                return;
            }

            try {
                const resposta = await fetch(
                    `${API}/categorias/evento/${eventoId}`
                );

                if (!resposta.ok) {
                    setMensagem('Não foi possível carregar as categorias.');
                    return;
                }

                const dados: Categoria[] = await resposta.json();
                setCategorias(dados);
                setCategoriaId(dados.length > 0 ? String(dados[0].id) : '');
            } catch {
                setMensagem('Erro ao carregar categorias.');
            }
        }

        carregarCategorias();
    }, [eventoId]);

    useEffect(() => {
        async function carregarItens() {
            if (!categoriaId) {
                setItens([]);
                setItemId('');
                return;
            }

            try {
                const resposta = await fetch(
                    `${API}/itens/categoria/${categoriaId}`
                );

                if (!resposta.ok) {
                    setMensagem('Não foi possível carregar os itens.');
                    return;
                }

                const dados: Item[] = await resposta.json();
                setItens(dados);
                setItemId(dados.length > 0 ? String(dados[0].id) : '');
            } catch {
                setMensagem('Erro ao carregar itens.');
            }
        }

        carregarItens();
    }, [categoriaId]);

    async function cadastrar(e: React.FormEvent) {
        e.preventDefault();

        if (!podeContribuir) {
            setMensagem('Este evento não aceita novas contribuições.');
            return;
        }

        if (!participanteId || !itemId || Number(quantidade) <= 0) {
            setMensagem('Selecione participante, item e quantidade válida.');
            return;
        }

        try {
            const resposta = await fetch(`${API}/contribuicoes`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    participanteId: Number(participanteId),
                    itemId: Number(itemId),
                    quantidade: Number(quantidade)
                })
            });

            if (!resposta.ok) {
                const erro = await resposta.text();
                setMensagem(erro || 'Não foi possível registrar a contribuição.');
                return;
            }

            const novaContribuicao: Contribuicao = await resposta.json();

            setContribuicoes(atual => [...atual, novaContribuicao]);
            setQuantidade('1');
            setMensagem('Contribuição registrada com sucesso.');
        } catch {
            setMensagem('Erro de conexão com a API.');
        }
    }

    return (
        <section className="panel">
            <h2>Contribuições</h2>

            {eventos.length === 0 ? (
                <p>Cadastre um evento primeiro.</p>
            ) : (
                <>
                    <label htmlFor="evento-contribuicao">Evento</label>
                    <select
                        id="evento-contribuicao"
                        value={eventoId}
                        onChange={e => setEventoId(e.target.value)}
                    >
                        {eventos.map(e => (
                            <option key={e.id} value={e.id}>
                                {e.nome} - {e.status}
                            </option>
                        ))}
                    </select>

                    {evento && !podeContribuir && (
                        <p role="status">
                            Este evento não aceita contribuições: verifique o
                            status e o prazo de alterações.
                        </p>
                    )}

                    <form onSubmit={cadastrar}>
                        <label htmlFor="participante-contribuicao">
                            Participante
                        </label>
                        <select
                            id="participante-contribuicao"
                            value={participanteId}
                            onChange={e => setParticipanteId(e.target.value)}
                            required
                        >
                            {participantes.map(p => (
                                <option key={p.id} value={p.id}>
                                    {p.nome}
                                </option>
                            ))}
                        </select>

                        <label htmlFor="categoria-contribuicao">
                            Categoria
                        </label>
                        <select
                            id="categoria-contribuicao"
                            value={categoriaId}
                            onChange={e => setCategoriaId(e.target.value)}
                        >
                            {categorias.map(c => (
                                <option key={c.id} value={c.id}>
                                    {c.nome}
                                </option>
                            ))}
                        </select>

                        <label htmlFor="item-contribuicao">Item</label>
                        <select
                            id="item-contribuicao"
                            value={itemId}
                            onChange={e => setItemId(e.target.value)}
                            required
                        >
                            {itens.map(i => (
                                <option key={i.id} value={i.id}>
                                    {i.nome || `Item ${i.id}`} - necessário: {i.quantidadeNecessaria}
                                </option>
                            ))}
                        </select>

                        {itens.find(i => String(i.id) === itemId) && (
                            <p className="muted">
                                Alérgenos: {
                                    itens.find(i => String(i.id) === itemId)?.alergenos || 'Não informado'
                                }
                            </p>
                        )}

                        <label htmlFor="quantidade-contribuicao">
                            Quantidade
                        </label>
                        <input
                            id="quantidade-contribuicao"
                            type="number"
                            min="0.01"
                            step="0.01"
                            value={quantidade}
                            onChange={e => setQuantidade(e.target.value)}
                            required
                        />

                        <button
                            type="submit"
                            disabled={!podeContribuir || !participantes.length || !itens.length}
                        >
                            Registrar contribuição
                        </button>
                    </form>
                </>
            )}

            <h3>Contribuições registradas</h3>

            {contribuicoes.length === 0 ? (
                <p className="muted">Nenhuma contribuição registrada.</p>
            ) : (
                contribuicoes.map(c => (
                    <div className="event" key={c.id}>
                        <strong>{c.itemNome}</strong>
                        <p>Participante: {c.participanteNome}</p>
                        <p>Quantidade: {c.quantidade}</p>
                    </div>
                ))
            )}

            {mensagem && <p role="status">{mensagem}</p>}
        </section>
    );
}
